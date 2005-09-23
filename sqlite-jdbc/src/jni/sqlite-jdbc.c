/*
 * sqlite-jdbc.c
 *
 * $Id$
 * $Rev$
 * $Date$
 * $Author$
 * $URL$
 *
 * Created on Jun 25, 2005
 *
 * Copyright (c) 2004, Robert M. Zigweid.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * + Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * + Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * + Neither the name of the SQLite-JDBC nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
#include "sqlite-jdbc.h"

/*
 * Class:     org_osjava_jdbc_sqlite_Driver
 * Method:    proxyConnect
 * Signature: (Ljava/lang/String;)Ljava/sql/Connection;
 *
 * Attempts to connect to the database pointed to by the filename in the
 * 'fileName' argument.
 */
JNIEXPORT jobject JNICALL
Java_org_osjava_jdbc_sqlite_Driver_proxyConnect(JNIEnv *env,
                                                jobject obj,
                                                jstring fileName) {
    int result;
    sqlite3 *dbPtr;
    const char *stringFileName = (*env)->GetStringUTFChars(env, fileName, 0);
    jobject connection;
    jclass connectionClass;
    jmethodID connectionMethod;

    result = sqlite3_open(stringFileName, &dbPtr);
    if(result) {
        /* There is always an open dbPtr from SQLite.  Close it when there's
         * an error */
        sqlite3_close(dbPtr);
        /* Set the error message to wrap in an SQLException */
        const char *message = sqlite3_errmsg(dbPtr);
        sqliteThrowSQLException(env, message);
        return NULL;
    }

    /* Create the connection object */
    connectionClass = (*env)->FindClass(env,
                                        "org/osjava/jdbc/sqlite/Connection");
    /* Make sure the class was found */
    /* FIXME: This really should be improved.  Right now, we're passing the
              exception directly up to the calling function rather than doing
              any handling on it.  At the very least we should send back an
              improved error message */
    if((*env)->ExceptionOccurred(env)) {
        return NULL;
    }

    connectionMethod = (*env)->GetMethodID(env,
                                           connectionClass,
                                           "<init>",
                                           "(I)V");
    if((*env)->ExceptionOccurred(env)) {
        return NULL;
    }

    connection = (*env)->NewObject(env,
                                   connectionClass,
                                   connectionMethod,
                                   dbPtr);
    if((*env)->ExceptionOccurred(env)) {
        return NULL;
    }
    return connection;
}

/*
 * Class:     org_osjava_jdbc_sqlite_Connection
 * Method:    proxyCloseConnection
 * Signature: (I)V
 *
 * Close an existing Connection object.
 * The dbPtr that is passed as argument should represent the sqlite3 dbPtr.
 * Returns true on success, else false.
 */
JNIEXPORT void JNICALL
Java_org_osjava_jdbc_sqlite_Connection_proxyCloseConnection(JNIEnv *env,
                                                            jobject obj) {
    sqlite3 *dbPtr = getSQLiteHandle(env, obj);
    int result = sqlite3_close((sqlite3 *)dbPtr);
    /* FIXME: Busy handling should be improved at some point down the line to
     *        allow the client to try again, perhaps.  */
    if(result == SQLITE_BUSY) {
        sqliteThrowSQLException(env, SQLITE_BUSY_MESSAGE);
        return;
    }

    /* An error occurred */
    if(result == SQLITE_ERROR) {
        sqliteThrowSQLException(env, sqlite3_errmsg((sqlite3 *)dbPtr));
        return;
    }
}

/*
 * Class:     org_osjava_jdbc_sqlite_ResultSet
 * Method:    proxyCloseStatement
 * Signature: (I)Z
 */
JNIEXPORT void JNICALL
Java_org_osjava_jdbc_sqlite_ResultSet_proxyCloseStatement(JNIEnv *env,
                                                          jobject stmt) {
    //int result =
    sqlite3_finalize(getStatementHandle(env, stmt));
}


/*
 * Class:     org_osjava_jdbc_sqlite_Statement
 * Method:    executeSQL
 * Signature: (Ljava/lang/String;Ljava/sql/Connection;)I
 *
 * Execute a raw SQL statement.
 * This method should be used when there is no result set that is to be
 * created/returned.
 */
JNIEXPORT jint JNICALL
Java_org_osjava_jdbc_sqlite_Statement_executeSQL(JNIEnv *env,
                                                 jobject obj,
                                                 jstring query,
                                                 jobject con) {
    int count;
    int result;
    char *errmsg;
    /* Convert the java query into a char array that can be used by the
     * sqlite method */
    /* 'jbyte *' and 'char *' are synonymous? */
    char *sql = (char *)(*env)->GetStringUTFChars(env, query, 0);

    sqlite3 *dbPtr = getSQLiteHandle(env, con);
    result = sqlite3_exec(dbPtr, sql, NULL, NULL, &errmsg);
    count = sqlite3_changes(dbPtr);
    (*env)->ReleaseStringUTFChars(env, query, sql);

    /* Check the result */
    if(result == SQLITE_BUSY) {
        sqliteThrowSQLException(env, SQLITE_BUSY_MESSAGE);
        return -1;
    }
    if(result) {
        sqliteThrowSQLException(env, errmsg);
        return -1;
    }
    return count;
}

/*
 * Class:     org_osjava_jdbc_sqlite_Statement
 * Method:    executeSQLWithResultSet
 * Signature: (Ljava/lang/String;Ljava/sql/Connection;Lorg/osjava/jdbc/sqlite/ResultSet;II)V
 *
 * Execute a statement in which the results will be put into a ResultSet.
 *
 * Due to the need for more flexibility all statements requiring ResultSets
 * should utilize this method.  It uses sqlite3_prepare() instead of
 * sqlite3_exec() and it's callback.  A java.sql.SQLException is thrown if
 * there are any errors encountered.
 *
 * statement    - A String representing the SQL statement to be executed.
 * con          - The java representation of the SQLite3 database connection.
 * resultSet    - The ResultSet to populate.
 */
JNIEXPORT void JNICALL
Java_org_osjava_jdbc_sqlite_Statement_executeSQLWithResultSet(JNIEnv *env,
                                                              jobject this,
                                                              jstring query,
                                                              jobject con,
                                                              jobject resultSet,
                                                              jint numRows) {
    int result;
    char *errmsg;
    sqlite3_stmt *stmt;

    /* Convert the java query into a char array that can be used by the
     * sqlite method */
    /* 'jbyte *' and 'char *' are synonymous? */
    char *sql = (char *)(*env)->GetStringUTFChars(env, query, 0);

    sqlite3 *dbPtr = getSQLiteHandle(env, con);
    result = sqlite3_prepare(dbPtr, sql, -1, &stmt, NULL);
    /* Check the result */
    if(result == SQLITE_BUSY) {
        sqliteThrowSQLException(env, SQLITE_BUSY_MESSAGE);
        return;
    }
    if(result) {
        sqliteThrowSQLException(env, errmsg);
    }

    (*env)->ReleaseStringUTFChars(env, query, sql);

    /* Associate the statement pointer to the ResultSet */
    jclass rsClass = (*env)->GetObjectClass(env, resultSet);
    if((*env)->ExceptionOccurred(env)) {
        return;
    }
    jmethodID methID = (*env)->GetMethodID(env,
                                           rsClass,
                                           "setStatementPointer",
                                           "(I)V");
    if((*env)->ExceptionOccurred(env)) {
        return;
    }
    (*env)->CallVoidMethod(env, resultSet, methID, stmt);
    if((*env)->ExceptionOccurred(env)) {
        return;
    }

    /* Fill the ResultSet Metadata. */
    populateResultSetMetadata(env, stmt, resultSet);
}

/* Populate a row of the ResultSet */
void populateRow(JNIEnv *env, sqlite3_stmt *stmt, jobject resultSet) {
    int numCols = sqlite3_column_count(stmt);
    int curCol;
    jclass resultSetClass = (*env)->GetObjectClass(env, resultSet);
    jmethodID method;

    fprintf(stderr, "Number of columns to populate -- %i.\n", numCols);
    for(curCol = 0; curCol < numCols; curCol++) {
        /* What is actually done is dependant upon the column type. */
        switch(sqlite3_column_type(stmt, curCol)) {
            case SQLITE_INTEGER:
                fprintf(stderr, "Found Number %lld.\n", sqlite3_column_int64(stmt, curCol));
                method = (*env)->GetMethodID(env,
                                             resultSetClass,
                                             "fillColumnWithNumber",
                                             "(IJ)V");
                if((*env)->ExceptionOccurred(env)) {
                    return;
                }
                (*env)->CallVoidMethod(env,
                                       resultSet,
                                       method,
                                       curCol,
                                       sqlite3_column_int64(stmt, curCol));
                if((*env)->ExceptionOccurred(env)) {
                    return;
                }
                break;
            case SQLITE_FLOAT:
                fprintf(stderr, "Found float.\n");
                method = (*env)->GetMethodID(env,
                                             resultSetClass,
                                             "fillColumnWithFloat",
                                             "(ILjava/lang/String;)V");
                if((*env)->ExceptionOccurred(env)) {
                    return;
                }
                (*env)->CallVoidMethod(env,
                                       resultSet,
                                       method,
                                       curCol,
                                       sqlite3_column_text(stmt, curCol));
                if((*env)->ExceptionOccurred(env)) {
                    return;
                }
                break;
            case SQLITE_TEXT:
                fprintf(stderr, "Found text.\n");
                /* Convert the returned string to a Java String */
                const char *cstr = sqlite3_column_text(stmt, curCol);
                jstring str = convertNativeString(env, cstr);
                method = (*env)->GetMethodID(env,
                                             resultSetClass,
                                             "fillColumnWithString",
                                             "(ILjava/lang/String;)V");
                if((*env)->ExceptionOccurred(env)) {
                    return;
                }
                (*env)->CallVoidMethod(env,
                                       resultSet,
                                       method,
                                       curCol,
                                       str);
                if((*env)->ExceptionOccurred(env)) {
                    return;
                }
                fprintf(stderr, "Successfully planted string.\n");
                break;
            case SQLITE_BLOB:
                fprintf(stderr, "Found blob.\n");
                /* FIXME: Wrong method signature */
                method = (*env)->GetMethodID(env,
                                             resultSetClass,
                                             "fillColumnWitBlob",
                                             "(IF)V");
                if((*env)->ExceptionOccurred(env)) {
                    return;
                }
                (*env)->CallVoidMethod(env,
                                       resultSet,
                                       method,
                                       curCol,
                                       sqlite3_column_blob(stmt, curCol));
                if((*env)->ExceptionOccurred(env)) {
                    return;
                }
                break;
            case SQLITE_NULL:
                fprintf(stderr, "Found null.\n");
                method = (*env)->GetMethodID(env,
                                             resultSetClass,
                                             "fillColumnWithNull",
                                             "(I)V");
                if((*env)->ExceptionOccurred(env)) {
                    return;
                }
                (*env)->CallVoidMethod(env,
                                        resultSet,
                                        method,
                                        curCol);
                if((*env)->ExceptionOccurred(env)) {
                    return;
                }
                break;
            default:
                /* FIXME: WTF.  Throw an exception here. */
                fprintf(stderr, "Found uhh..something different WTFO!\n");
                break;
        }
    }
}

/* Populate a range of rows for a given sqlite3 statement. */
JNIEXPORT void JNICALL 
Java_org_osjava_jdbc_sqlite_ResultSet_populateRows(JNIEnv *env, 
                                                   jobject resultSet, 
                                                   jint startRow, 
                                                   jint finishRow) {
    int count;
    char *errmsg;
    int result;
    jmethodID method;
    jclass resultSetClass = (*env)->GetObjectClass(env, resultSet);
    if((*env)->ExceptionOccurred(env)) {
        return;
    }

    fprintf(stderr, "Start populate rows.\n");
    /* Get the statement pointer. */
    sqlite3_stmt *stmt = getStatementHandle(env, resultSet);
    fprintf(stderr, "Start row %i\tEnd row %i\n", (int)startRow, (int)finishRow);
    /* Skip statements up to startRow.  These statements will be ignored. */
    for(count = 0; count < startRow; count++) {
        result = sqlite3_step(stmt);
        /* Check the result */
        if(result == SQLITE_BUSY) {
            sqliteThrowSQLException(env, SQLITE_BUSY_MESSAGE);
            return;
        }
        /* ?hrow an SQLException if the result set range is out of bounds.
         * This exception will be caught and properly processed on the java
         * side of things. */
        if(result == SQLITE_DONE) {
            sqliteThrowSQLException(env, SQLITE_OUT_OF_BOUNDS);
        }
        if(result) {
            sqliteThrowSQLException(env, errmsg);
        }
    }

    /* Start populating the ResultSet */
    method = (*env)->GetMethodID(env,
                                 resultSetClass,
                                 "insertRowIntoResultSet",
                                 "(I)V");
    if((*env)->ExceptionOccurred(env)) {
        return;
    }
    for(count = startRow; count <= finishRow; count ++) {
        result = sqlite3_step(stmt);
        /* Check the result */
        if(result == SQLITE_BUSY) {
            sqliteThrowSQLException(env, SQLITE_BUSY_MESSAGE);
            return;
        }
        /* The expected result most of the time.  Work gets done here.*/
        if(result == SQLITE_ROW) {
            /* First insert the representation of the row into the ResultSet */
            /* Use count - startRow because we need to specify the position in 
             * the page, rather than in the total of the resultset */
            (*env)->CallVoidMethod(env,
                                   resultSet,
                                   method,
                                   count - startRow);
            if((*env)->ExceptionOccurred(env)) {
                return;
            }
            populateRow(env, stmt, resultSet);
            /* Skip the rest of the result conditions */
            continue;
        }
        /* Done populating the result set.  We're done here. */
        if(result == SQLITE_DONE) {
            fprintf(stderr, "There is no more to populate, returning.\n");
            return;
        }
        if(result) {
            sqliteThrowSQLException(env, errmsg);
        }
    }
    fprintf(stderr, "Done populating resultset section.\n");
    
}

/* Populate the metadata for the ResultSet */
void populateResultSetMetadata(JNIEnv *env, sqlite3_stmt *stmt, jobject resultSet) {
    int i;
    int numCols;
    jclass resultSetClass = (*env)->GetObjectClass(env, resultSet);
    jclass metaDataClass;
    /* We have to get the resultSet's metadata object */
    jobject metaData;
    jmethodID getMetaID = (*env)->GetMethodID(env,
                                              resultSetClass,
                                              "getMetaData",
                                              "()Ljava/sql/ResultSetMetaData;");
    if((*env)->ExceptionOccurred(env)) {
        return;
    }
    metaData = (*env)->CallObjectMethod(env, resultSet, getMetaID);
    if((*env)->ExceptionOccurred(env)) {
        return;
    }
    /* There's a problem if there is not metadata object */
    if(metaData == NULL) {
        sqliteThrowSQLException(env, "Could not populate ResultSetMetaData.  Object does not exist.");
        return;
    }
    /* Determine first whether or not the metadata has already been filled.
     * If it has, abort immediately.  This should only be done once.
     * This can easily be done by looking at the number of columns in the
     * ResultSet.  Anything less than 0 means that it can be populated.  We
     * do not throw an exception here because we don't know whether or not
     * this is a first run through. */
    numCols = sqlite3_column_count(stmt);
    metaDataClass = (*env)->GetObjectClass(env, metaData);
    if((*env)->ExceptionOccurred(env)) {
        return;
    }
    jmethodID getColID = (*env)->GetMethodID(env,
                                             metaDataClass,
                                             "getColumnCount",
                                             "()I");
    if((*env)->ExceptionOccurred(env)) {
        return;
    }
    /* If the column count is < 0 (i.e. -1) the ResultSetMetadata hasn't been
       set yet */
    if((*env)->CallIntMethod(env, metaData, getColID) >= 0) {
        if((*env)->ExceptionOccurred(env)) {
            return;
        }
        fprintf(stderr, "Already filled metadata.\n");
        /* FIXME: Should probably throw an exception here instead? */
        return;
    }

    jmethodID setColID = (*env)->GetMethodID(env,
                                             metaDataClass,
                                             "setColumnCount",
                                             "(I)V");
    if((*env)->ExceptionOccurred(env)) {
        return;
    }
    (*env)->CallVoidMethod(env, metaData, setColID, numCols);
    if((*env)->ExceptionOccurred(env)) {
        return;
    }
    fprintf(stderr, "Successfully set column width.\n");
    
    /* Set the column names */
    jmethodID setNameID = (*env)->GetMethodID(env,
                                              metaDataClass,
                                              "setColumnName",
                                              "(ILjava/lang/String;)V");
    if((*env)->ExceptionOccurred(env)) {
        return;
    }
    for(i = 0; i < numCols; i++) {
        const char *colName = sqlite3_column_name(stmt, i);
        (*env)->CallVoidMethod(env,
                               metaData,
                               setNameID,
                               i,
                               convertNativeString(env, colName));
        if((*env)->ExceptionOccurred(env)) {
            return;
        }        
    }
}

/*
 * Get the SQLite3 handle from a Connection object.
 */
sqlite3 *getSQLiteHandle(JNIEnv *env, jobject con) {
    jclass conClass = (*env)->GetObjectClass(env, con);
    if((*env)->ExceptionOccurred(env)) {
        return NULL;
    }
    jmethodID methID = (*env)->GetMethodID(env,
                                           conClass,
                                           "getDBPointer",
                                           "()I");
    if((*env)->ExceptionOccurred(env)) {
        return NULL;
    }
    sqlite3 *result = (sqlite3 *)(*env)->CallIntMethod(env, con, methID);
    if((*env)->ExceptionOccurred(env)) {
        return NULL;
    }
    return result;
}

/*
 * Get the SQLite3 statement handle from a ResultSet object.
 */
sqlite3_stmt *getStatementHandle(JNIEnv *env, jobject rs) {
    jclass rsClass = (*env)->GetObjectClass(env, rs);
    if((*env)->ExceptionOccurred(env)) {
        return NULL;
    }
    jmethodID methID = (*env)->GetMethodID(env,
                                           rsClass,
                                           "getStatementPointer",
                                           "()I");
    if((*env)->ExceptionOccurred(env)) {
        return NULL;
    }
    sqlite3_stmt *result = (sqlite3_stmt *)(*env)->CallIntMethod(env, rs, methID);
    if((*env)->ExceptionOccurred(env)) {
        return NULL;
    }
    return result;
}

/*
 * Convert a Native String to a Java String
 * This is largely taken from Sun's example.
 */
jstring convertNativeString(JNIEnv *env, const char *inStr) {
    int len = strlen(inStr);
    jbyteArray bytes = (*env)->NewByteArray(env, len);
    if(bytes != NULL) {
        (*env)->SetByteArrayRegion(env, bytes, 0, len, (jbyte *)inStr);
        jclass stringClass = (*env)->FindClass(env, "java/lang/String");
        if((*env)->ExceptionOccurred(env)) {
            return NULL;
        }
        jmethodID methID = (*env)->GetMethodID(env,
                                               stringClass,
                                               "<init>",
                                               "([B)V");
        if((*env)->ExceptionOccurred(env)) {
            return NULL;
        }
        jstring result = (*env)->NewObject(env, stringClass, methID, bytes);
        if((*env)->ExceptionOccurred(env)) {
            return NULL;
        }
        (*env)->DeleteLocalRef(env, bytes);
        if((*env)->ExceptionOccurred(env)) {
            return NULL;
        }
        return result;
    }
    /* FIXME: An exception should be thrown? */
    return NULL;
}

/*
 * Throw an SQLException to the object 'ob', with the message 'message'.
 */
void sqliteThrowSQLException(JNIEnv *env, const char *message) {
    jclass excClass = (*env)->FindClass(env,
                                        "java/sql/SQLException");
    if((*env)->ExceptionOccurred(env)) {
        return;
    }
    /* Can't find the class?  Give up, though this should never happen */
    if(excClass == 0) {
        return;
    }
    (*env)->ThrowNew(env,
                     excClass,
                     message);
    if((*env)->ExceptionOccurred(env)) {
        return;
    }
    return;
}
