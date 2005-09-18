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
#ifndef _SQLITE_JDBC_H_
#define  _SQLITE_JDBC_H

#include <sqlite3.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "org_osjava_jdbc_sqlite_Driver.h"
#include "org_osjava_jdbc_sqlite_Connection.h"
#include "org_osjava_jdbc_sqlite_ResultSet.h"
#include "org_osjava_jdbc_sqlite_Statement.h"

extern void sqliteThrowSQLException(JNIEnv *env, const char *message);
extern sqlite3 *getSQLiteHandle(JNIEnv *env, jobject con);
extern sqlite3_stmt *getStatementHandle(JNIEnv *env, jobject rs);
extern void populateRow(JNIEnv *env, sqlite3_stmt *stmt, jobject resultSet);
extern void populateResultSetMetadata(JNIEnv *env, sqlite3_stmt *stmt, jobject resultSet);
jstring convertNativeString(JNIEnv *env, const char *inStr);

/* Error messages */
#define SQLITE_BUSY_MESSAGE "Database busy, cannot perform operation."
#define SQLITE_OUT_OF_BOUNDS "ResultSet range out of bounds."

#endif /* _SQLITE_JDBC_H */
