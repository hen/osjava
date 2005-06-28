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
#include <jni.h>
#include <sqlite3.h>
#include <stdio.h>
#include <stdlib.h>
#include "org_osjava_jdbc_sqlite_Driver.h"

/*
 * Attempts to connect to the database pointed to by the filename in the
 * 'fileName' argument.
 */
JNIEXPORT jobject JNICALL
Java_org_osjava_jdbc_sqlite_Driver_proxyConnect(JNIEnv *env,
                                                jobject obj,
                                                jstring fileName) {
    int result;
    sqlite3 *handle;
    const char *stringFileName = (*env)->GetStringUTFChars(env, fileName, 0);
    jobject connection;
    jclass connectionClass;
    jmethodID connectionMethod;
    jthrowable e;
    

    handle = (sqlite3 *)malloc(sizeof(sqlite3 *));
    result = sqlite3_open(stringFileName, &handle);
    printf("Result of open for filename %s is %i.\n",
         stringFileName,
         result);
    if(result) {
        /* Set the error message to wrap in an SQLException */
        const char *message = sqlite3_errmsg(handle);
        jclass cls = (*env)->GetObjectClass(env, obj);
        jmethodID mid = (*env)->GetMethodID(env,
                                            cls,
                                            "setErrorMessage",
                                            "(Ljava/lang/String;)V");
        (*env)->CallVoidMethod(env, obj, mid, message);

        /* There is always an open handle from SQLite.  Close it when there's
         * an error */
        sqlite3_close(handle);
        return NULL;
    }

    /* Create the connection object */
    connectionClass = (*env)->FindClass(env,
                                        "org/osjava/jdbc/sqlite/Connection");
    /* Make sure the class was found */
    printf("Connection class -- %p", connectionClass);
    connectionMethod = (*env)->GetMethodID(env,
                                           connectionClass,
                                           "<init>",
                                           "(I)V");
    connection = (*env)->NewObject(env,
                                   connectionClass,
                                   connectionMethod,
                                   handle);
    return connection;
}
