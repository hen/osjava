package code316.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Iterator;



/**
 * Feed this class all your Connections, ResultSets, and
 * Statements.  When you're done, call the closeAll() method and 
 * all of the objects will be closed for you.
 * <p>This helper class helps eliminate the sql pyramid found in
 * the finally class of lots of jdbc code.</p>
 * Old Code
 * <pre>
 * Connection conn;
 * ResultSet rs;
 * Statement stmt;
 * 
 * try {
 *     ... sql stuff here
 * }
 *    ...
 * finally {
 *    if ( rs != null ) {
 *        try {
 *           rs.close();
 *        }
 *        catch (SqlException e) {}
 *    }
 * 
 *    if ( stmt != null ) {
 *        try {
 *           stmt.close();
 *        }
 *        catch (SqlException e) {}
 *    }
 * 
 *    if ( conn != null ) {
 *        try {
 *           conn.close();
 *        }
 *        catch (SqlException e) {}
 *    }
 * }
 * </pre>
 */
public class DbHelper {
    private ArrayList connections = new ArrayList();
    private ArrayList statements = new ArrayList();
    private ArrayList resultSets = new ArrayList();
    private boolean suppress = true;
    private boolean silent = false;
    private Category log = new Category(DbHelper.class);

    public DbHelper() {    	
        super();
        log.setLogLevel(Category.FATAL);
    }

    public void closeAll() {
        Exception resultsException = null;
        Exception statementsException = null;
        Exception connectionsException = null;

        // close result sets
        Iterator tor = resultSets.iterator();

        while (tor.hasNext()) {
            ResultSet rs = (ResultSet) tor.next();

            try {
            	log.debug("closing resultset: " + rs);            	
                rs.close();
                log.debug("closed resultset: " + rs);
            }
            catch (Exception e) {
                e.printStackTrace();
                resultsException = e;
            }
        }


        // close statements   
        tor = statements.iterator();

        while (tor.hasNext()) {
            Statement statement = (Statement) tor.next();

            try {
            	log.debug("closing statement: " + statement);
                statement.close();
                log.debug("closed statement: " + statement);
            }
            catch (Exception e) {
                e.printStackTrace();
                statementsException = e;
            }
        }


        // close connections
        tor = connections.iterator();

        while (tor.hasNext()) {
            Connection connection = (Connection) tor.next();

            try {
            	log.debug("closing connection: " + connection);
                connection.close();
                log.debug("closed connection: " + connection);
            }
            catch (Exception e) {
                e.printStackTrace();
                connectionsException = e;
            }
        }

    }


    private void closeConnections() {
        Iterator tor = connections.iterator();

        while (tor.hasNext()) {
            Connection connection = (Connection) tor.next();

            try {
                connection.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeResultSets() {
        Iterator tor = resultSets.iterator();

        while (tor.hasNext()) {
            ResultSet rs = (ResultSet) tor.next();

            try {
                rs.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeStatements() {
        Iterator tor = statements.iterator();

        while (tor.hasNext()) {
            Statement statement = (Statement) tor.next();

            try {
                statement.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection addConnection(Connection connection) {
        if (connection == null) {
            throw new NullPointerException("connection can not be null");
        }

        connections.add(connection);

        return connection;
    }

    public PreparedStatement addPreparedStatement(PreparedStatement statement) {
        return (PreparedStatement) addStatement(statement);
    }

    public ResultSet addResultSet(ResultSet resultSet) {
        if (resultSet == null) {
            throw new NullPointerException("resultSet can not be null");
        }

        resultSets.add(resultSet);

        return resultSet;
    }

    public Statement addStatement(Statement statement) {
        if (statement == null) {
            throw new NullPointerException("statement can not be null");
        }

        statements.add(statement);

        return statement;
    }
    
    /**
     * Closes a Connection.  If an SQLException is thrown during the
     * close, it is thrown back to the caller.
     * 
     * @param connection null is a valid value.
     * @throws SQLException
     */
    public static void close(Connection connection) throws SQLException {
    	if ( connection == null ) {
    		return;
    	}
    	connection.close();
    }
    
    /**
     * Closes Connection.  Will not throw an Exception if
     * there is problem while closing the Connection.
     * 
     * <p>
     * If an Exception is thrown during the close prinStackTrace
     * is called on the Exception.
     * 
     * @param connection is a valid value.
     */
    public static void closeQuietly(Connection connection) {
    	if ( connection == null ) {
    		return;
    	}
    	
    	try {
            connection.close();
        }
        catch (SQLException e) {
        	e.printStackTrace();
        }        
    }
    
    
    public static void close(Statement statement) throws SQLException {
    	if ( statement == null ) {
    		return;
    	}
    	statement.close();
    }

	/**
	 * @see DbHelper
	 * @param stmt
	 */
    public static void closeQuietly(Statement stmt) {
    	if ( stmt == null ) {
    		return;
    	}
    	
    	try {
            stmt.close();
        }
        catch (SQLException e) {
        	e.printStackTrace();
        }        
    }


    
    public static void close(ResultSet rs) throws SQLException {
    	if ( rs == null ) {
    		return;
    	}
    	rs.close();
    }
    
    public static void closeQuietly(ResultSet rs) {
    	if ( rs == null ) {
    		return;
    	}
    	
    	try {
            rs.close();
        }
        catch (SQLException e) {
        	e.printStackTrace();
        }        
    }

    
    
    
    public static void close(Connection connection, Statement statement, ResultSet rs) throws SQLException {
    	DbHelper.close(rs);
    	DbHelper.close(statement);
    	DbHelper.close(connection);    	
    }
    
    

    
    
    
    
    
    
    
    
    
    
    
    
    
}