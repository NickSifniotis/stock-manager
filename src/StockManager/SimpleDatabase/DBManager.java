package StockManager.SimpleDatabase;

import java.sql.*;


/**
 * Wrapper class for interfacing with the SQLite database. This is an internal class that you don't
 * need to worry about. Unless you are working on this project, in which case, you probably need
 * to worry about it.
 *
 * @author Nick Sifniotis u5809912
 * @since 31/08/2015
 * @version 2.0.0
 */
class DBManager
{
    private static String database_path = "database";
    private static String database_name = "database.db";


    /**
     * Changes the location and name of the database file.
     *
     * If you are going to change the database from the default database/database.db,
     * make sure that you call this function when you initialise your program, before you attempt
     * to perform any operations on the database.
     *
     * @param new_path - the path where the database file will be located, without the trailing '/'
     * @param new_name - the name of the database file
     */
    public static void SetDBLocation (String new_path, String new_name)
    {
        database_name = new_name;
        database_path = new_path;
    }


    /**
     * Executes the given query.
     * No safety checks whatsoever are conducted on the query string. Use with caution!
     *
     * @param query - the query to execute
     * @throws SQLException - this static class handles no exceptions.
     */
    public static void Execute (String query) throws SQLException
    {
        Connection connection = null;
        Statement statement = null;

        try
        {
            connection = Connect();
            statement = connection.createStatement();
            statement.execute(query);
        }
        finally
        {
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }


    /**
     * Executes the SQL and return the key of the affected row
     * Obviously .. the query can only be one INSERT only.
     *
     * @param query the query to execute
     * @return the pri key of the newly created row
     * @throws SQLException - this class does not handle faulty queries
     */
    public static int ExecuteReturnKey (String query) throws SQLException
    {
        int res = -1;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generated_keys = null;

        try
        {
            connection = Connect();
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            int affected_rows = statement.executeUpdate();

            if (affected_rows != 1)
                throw new SQLException("DBManager.ExecuteReturnKey - Insert into database failed. Affected rows: " + affected_rows + ": " + query);

            generated_keys = statement.getGeneratedKeys();

            if (generated_keys.next())
                res = generated_keys.getInt(1);
            else
                throw new SQLException("DBManager.ExecuteReturnKey - Creating user failed, no ID obtained.");
        }
        finally
        {
            closeQuietly(generated_keys);
            closeQuietly(statement);
            closeQuietly(connection);
        }

        return res;
    }


    /**
     * Executes a query and returns the results in a resultSet
     * These queries are executed in three stages
     * connect -> executeQuery -> disconnect
     *
     * @param query The SELECT query to execute
     * @param connection The object that represents a connection to the database. It is returned by DBManager.Connect()
     * @return The results of the query, stored in a java.sql.ResultSet object.
     * @throws SQLException, because this class does not handle exceptions at all.
     */
    public static ResultSet ExecuteQuery (String query, Connection connection) throws SQLException
    {
        ResultSet results = null;

        if (connection != null)
            results = connection.createStatement().executeQuery(query);

        return results;
    }


    /**
     * Connects to the database, and
     * returns a connection object that can be used to process SQL and so forth.
     *
     * @return A connection object that is connected to the database. Remember to close when finished!
     * @throws SQLException - SQL Exceptions aren't handled by this class.
     */
    public static Connection Connect() throws SQLException
    {
        Connection connection = null;
        try
        {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + database_path + "/" + database_name);
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return connection;
    }


    /**
     * Disconnects the given connection from the database.
     *
     * @param connection The connection to disconnect
     */
    public static void Disconnect (Connection connection)
    {
        closeQuietly(connection);
    }


    /**
     * Disconnect the ResultSet from the database.
     *
     * @param results The ResultSet to disconnect.
     */
    public static void Disconnect (ResultSet results)
    {
        closeQuietly(results);
    }


    /**
     * Sssh!
     *
     * Utility functions to close database connections without a fuss.
     *
     * @param res - the database entity to close
     */
    private static void closeQuietly (ResultSet res)
    {
        if (res == null)
            return;

        try
        {
            res.close();
        }
        catch (Exception e)
        {
            // ignore it
        }
    }

    private static void closeQuietly (Connection res)
    {
        if (res == null)
            return;

        try
        {
            res.close();
        }
        catch (Exception e)
        {
            // ignore it
        }
    }

    private static void closeQuietly (Statement res)
    {
        if (res == null)
            return;

        try
        {
            res.close();
        }
        catch (Exception e)
        {
            // ignore it
        }
    }
}
