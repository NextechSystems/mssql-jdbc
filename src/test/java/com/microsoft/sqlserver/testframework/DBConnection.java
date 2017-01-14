// ---------------------------------------------------------------------------------------------------------------------------------
// File: DBConnection.java
//
//
// Microsoft JDBC Driver for SQL Server
// Copyright(c) Microsoft Corporation
// All rights reserved.
// MIT License
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files(the "Software"),
// to deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense,
// and / or sell copies of the Software, and to permit persons to whom the
// Software is furnished to do so, subject to the following conditions :
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.
// ---------------------------------------------------------------------------------------------------------------------------------

package com.microsoft.sqlserver.testframework;

import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;

/*
 * Wrapper class for SQLServerConnection
 */
public class DBConnection extends AbstractParentWrapper {

    // TODO: add Isolation Level
    // TODO: add auto commit
    // TODO: add connection Savepoint and rollback
    // TODO: add additional connection properties
    // TODO: add DataSource support
    private SQLServerConnection connection = null;
    private boolean _closed = false;
    private boolean _closeCalled = false;
    public int _holdability = ResultSet.HOLD_CURSORS_OVER_COMMIT;


    /**
     * establishes connection using the input
     * 
     * @param connectionString
     */
    public DBConnection(String connectionString) {
        super(null, null, "connection");
        getConnection(connectionString);
    }

    /**
     * establish connection
     * 
     * @param connectionString
     */
    void getConnection(String connectionString) {
        try {
            connection = PrepUtil.getConnection(connectionString);
            setInternal(connection);
        }
        catch (SQLException ex) {
            fail(ex.getMessage());
        }
        catch (ClassNotFoundException ex) {
            fail(ex.getMessage());
        }
    }

    @Override
    void setInternal(Object internal) {
        this.internal = internal;
    }

    /**
     * 
     * @return Statement wrapper
     */
    public DBStatement createStatement() {
        try {
            DBStatement dbstatement = new DBStatement(this);
            return dbstatement.createStatement();
        }
        catch (SQLException ex) {
            fail(ex.getMessage());
        }
        return null;
    }

    /**
     * 
     * @param type
     * @param concurrency
     * @return
     * @throws SQLException
     */
    public DBStatement createStatement(int type, int concurrency) throws SQLException {
        DBStatement dbstatement = new DBStatement(this);
        return dbstatement.createStatement(type, concurrency);

    }
    
    /**
     * 
     * @param query
     * @return
     * @throws SQLException
     */
    public DBPreparedStatement prepareStatement(String query) throws SQLException
    {
       DBPreparedStatement dbpstmt = new DBPreparedStatement(this, internal, "preparedStatement");
       return dbpstmt.prepareStatement(query);    
    }

    /**
     * clsoe connection
     */
    public void close() {
        try {
            connection.close();
        }
        catch (SQLException ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * checks if the connection is closed.
     * 
     * @return true if connection is closed.
     * @throws SQLException
     */
    public boolean isClosed() {
        boolean current = false;
        try {
            current = connection.isClosed();
        }
        catch (SQLException ex) {
            fail(ex.getMessage());
        }
        return current;
    }

    /**
     * Retrieves metaData
     * 
     * @return
     * @throws SQLException
     */
    public DatabaseMetaData getMetaData() throws SQLException {
        DatabaseMetaData product = connection.getMetaData();
        return product;
    }

    /**
     * 
     * @param con
     * @return
     * @throws SQLException
     */
    public static boolean isSqlAzure(Connection con) throws SQLException {
        boolean isSqlAzure = false;

        ResultSet rs = con.createStatement().executeQuery("SELECT CAST(SERVERPROPERTY('EngineEdition') as INT)");
        rs.next();
        int engineEdition = rs.getInt(1);
        rs.close();
        if (ENGINE_EDITION_FOR_SQL_AZURE == engineEdition) {
            isSqlAzure = true;
        }

        return isSqlAzure;
    }

}
