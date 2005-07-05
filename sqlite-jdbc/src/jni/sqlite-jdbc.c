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
    if(connectionClass == NULL) {
        return NULL;
    }

    connectionMethod = (*env)->GetMethodID(env,
                                           connectionClass,
                                           "<init>",
                                           "(I)V");
    /* FIXME: This really should be improved.  Right now, we're passing the
              exception directly up to the calling function rather than doing
              any handling on it.  At the very least we should send back an
              improved error message */
    if(connectionMethod == NULL) {
        return NULL;
    }

    printf("SQLite3 Handle is %p.\n", dbPtr);
    connection = (*env)->NewObject(env,
                                   connectionClass,
                                   connectionMethod,
                                   dbPtr);
    return connection;
}

/*
 * Class:     org_osjava_jdbc_sqlite_Connection
 * Method:    proxyClose
 * Signature: (I)V
 *
 * Close an existing Connection object.
 * The dbPtr that is passed as argument should represent the sqlite3 dbPtr.
 * Returns true on success, else false.
 */
JNIEXPORT void JNICALL
Java_org_osjava_jdbc_sqlite_Connection_proxyClose(JNIEnv *env,
                                                  jobject obj,
                                                  jint dbPtr) {
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
 * Class:     org_osjava_jdbc_sqlite_Statement
 * Method:    executeSQL
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL
Java_org_osjava_jdbc_sqlite_Statement_executeSQL(JNIEnv *env,
                                                 jobject obj,
                                                 jstring query,
                                                 jobject con,
                                                 jobject resultSet) {
    int result;
    char *errmsg;
    /* Convert the java query into a char array that can be used by the
     * sqlite method */
    /* 'jbyte *' and 'char *' are synonymous? */
    char *sql = (char *)(*env)->GetStringUTFChars(env, query, 0);

    /* Extract the dbpointer out of the connection object */
    jclass conClass = (*env)->GetObjectClass(env, con);
    jmethodID methID = (*env)->GetMethodID(env,
                                           conClass,
                                           "getDBPointer",
                                           "()I");
    sqlite3 *dbPtr = (sqlite3 *)(*env)->CallIntMethod(env, con, methID);
    result = sqlite3_exec(dbPtr,
                          sql,
                          &sqliteResultSetCallback,
                          resultSet,
                          &errmsg);

    /* Check the result */
    printf("Result of statement -- %i\n", result);
    if(result == SQLITE_BUSY) {
        sqliteThrowSQLException(env, SQLITE_BUSY_MESSAGE);
        return;
    }
    if(result) {
        sqliteThrowSQLException(env, errmsg);
    }
    (*env)->ReleaseStringUTFChars(env, query, sql);
}

/*
 * Callback used by sqlite3_exec() or other functions that callback with
 * results that can be put into a ResultSet
 */
int sqliteResultSetCallback(void *resultSet,
                            int count,
                            char **rows,
                            char **colNames) {
    printf("Hit callback.\n");
    return 0;
}

/*
 * Throw an SQLException to the object 'ob', with the message 'message'.
 */
void sqliteThrowSQLException(JNIEnv *env, const char *message) {
    jclass excClass = (*env)->FindClass(env,
                                        "java/sql/SQLException");
    /* Can't find the class?  Give up, though this should never happen */
    if(excClass == 0) {
        return;
    }
    (*env)->ThrowNew(env,
                     excClass,
                     message);
    return;
}

