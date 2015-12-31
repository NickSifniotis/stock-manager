package StockManager.SimpleDatabase;

import StockManager.SimpleDatabase.Columns.Column;


import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


/**
 * <p>
 *     This static class contains all the methods for loading and saving DataObject descendants to the database.
 * </p>
 * <p>
 *     DataObjects contain the data that you want to store in the database. See DataObjects for more information about
 *     creating and working with them. This class assumes that you have created your child objects and provides you
 *     with the methods that you need to save and load them to your database.
 * </p>
 *
 * @author Nick Sifniotis u5809912
 * @since 06/11/2015
 */
public class SimpleDB
{
    /**
     * Loads a DataObject object from the database by Primary Key. As the object returned is a DataObject,
     * you will need to cast it into your class to access its fields.
     *
     * Typical usage is as below:
     *
     * <pre>
     * {@code
     *     public class Person extends DataObject
     *     {
     *         public StringColumn Name = new StringColumn();
     *
     *         ... etc
     *     }
     *
     *     Person my_person = (Person) SimpleDB.Load (Person.class, 3);
     *     // person now contains the database row corresponding to PriKey 3
     * }
     * </pre>
     *
     * @param object_type The type of object to load.
     * @param pri_key The primary key of the object to retrieve.
     * @return An instance of object_type, with current data loaded from the database.
     */
    public static DataObject Load(Class object_type, int pri_key)
    {
        DataObject result = null;
        String query = "SELECT * FROM " + object_type.getSimpleName() + " WHERE PrimaryKey = " + String.valueOf(pri_key);

        Connection connection = null;
        ResultSet results = null;
        try
        {
            connection = DBManager.Connect();
            results = DBManager.ExecuteQuery(query, connection);
            if (results.next())
                result = __load_from_db(object_type, results);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            DBManager.Disconnect(results);
            DBManager.Disconnect(connection);
        }

        return result;
    }


    /**
     * Loads all items from this class's table.
     *
     * @param object_type The type of objects to load
     * @return An array of all the loaded objects.
     */
    public static DataObject[] LoadAll(Class object_type)
    {
        List<DataObject> result_list = new LinkedList<>();
        String query = "SELECT * FROM " + object_type.getSimpleName();

        Connection connection = null;
        ResultSet results = null;
        try
        {
            connection = DBManager.Connect();
            results = DBManager.ExecuteQuery(query, connection);
            while (results.next())
                result_list.add(__load_from_db(object_type, results));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            DBManager.Disconnect(results);
            DBManager.Disconnect(connection);
        }

        DataObject[] res = new DataObject[result_list.size()];
        return result_list.toArray(res);
    }


    /**
     * Saves the object into the database.
     *
     * @param object - the object to save.
     */
    public static void Save(DataObject object)
    {
        // the structure of INSERT and UPDATE queries is fundamentally different,
        // so there will be two largish blocks of code here.


        // first step - extract the relevant data from the class and the instance itself.
        String table_name = object.getClass().getSimpleName();
        Column[] fields = __get_object_fields(object);


        // second step - build and execute the SQL queries.
        // as I said above, it's fundamentally different for INSERT and UPDATE
        if (object.PrimaryKey == -1)
        {
            // this is an insert operation
            String query = "INSERT INTO " + table_name + "(";

            for (int i = 0; i < fields.length; i++)
                query += fields[i].Name() + (i < (fields.length - 1) ? ", " : "");

            query += ") VALUES (";

            for (int i = 0; i < fields.length; i++)
                query += fields[i].SQLFieldValue() + (i < (fields.length - 1) ? ", " : "");

            query += ");";

            try
            {
                object.PrimaryKey = DBManager.ExecuteReturnKey(query);
            }
            catch (Exception e)
            {
                System.out.println("SQL error on insert.");
                System.out.println(query);
                e.printStackTrace();
            }
        }
        else
        {
            // this is an update
            String query = "UPDATE " + table_name + " SET ";

            for (int i = 0; i < fields.length; i++)
                query += fields[i].Name() + " = " + fields[i].SQLFieldValue() + (i < (fields.length - 1) ? ", " : "");

            query += " WHERE PrimaryKey = " + String.valueOf(object.PrimaryKey);

            try
            {
                DBManager.Execute(query);
            }
            catch (Exception e)
            {
                System.out.println("SQL error on update.");
                System.out.println(query);
                e.printStackTrace();
            }
        }
    }


    /**
     * Saves all of the objects into the database.
     *
     * @param objects - the list of objects to save.
     */
    public static void SaveAll(DataObject[] objects)
    {
        for (DataObject o : objects)
            Save(o);
    }


    /**
     * Drops the table for the given object type from the database. Bye bye data.
     *
     * @param object_type - the class that corresponds to the table to drop.
     */
    public static void DeleteTable(Class object_type)
    {
        String table_name = object_type.getSimpleName();
        __delete_table_by_name(table_name);
    }


    /**
     * Creates a table in the database who's schema conforms to the fields declared in the class object_type
     *
     * @param object_type - the class that contains the metadata for the table construction.
     */
    public static void CreateTable(Class object_type)
    {
        // pull out all of the relevant data about the class that we are saving
        String table_name = object_type.getSimpleName();
        String[] column_declarations = __get_column_declarations(object_type);

        if (column_declarations == null)
            return;


        // if it exists, drop the old table
        __delete_table_by_name(table_name);


        // construct the SQL query.
        String query = "CREATE TABLE " + table_name + "(";
        for (String col: column_declarations)
            query += col + ", ";
        query += "PrimaryKey INTEGER PRIMARY KEY);";

        // GO GO GO
        try
        {
            DBManager.Execute(query);
        }
        catch (Exception e)
        {
            System.out.println("SQL error on create table " + table_name);
            e.printStackTrace();
        }
    }


    /**
     * <p>
     *      Creates a new instance of the type of object specified by the user.
     * </p>
     * <p>
     *     Creates a new instance of object_type, autosets the default values, and saves to the database.
     *     Do not call this method if you do not intend to save the object.
     * </p>
     *
     * <p>
     *     The following code illustrates typical usage, again using the Person class as an example.
     * </p>
     *
     * <pre>
     * {@code
     *     Person new_person = (Person) SimpleDB.New (Person.class);
     *     new_person.FirstName.Value = "Nick";
     * }
     * </pre>
     *
     * <p>
     *     Note that as of this time, there is no way to send initialisation values to the new object.
     *     The object is initialised with the default values provided in the class's SetDefaults() method.
     * </p>
     *
     * @param object_type The type of DataObject to instantiate and initialise.
     * @return A new object of that type
     */
    public static DataObject New(Class object_type)
    {
        if (!__validate_descendant_of_dataobject(object_type))
            return null;

        DataObject new_instance;
        try
        {
            new_instance = (DataObject) object_type.newInstance();
            new_instance.SetColumnNames();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        new_instance.SetDefaults();
        Save(new_instance);

        return new_instance;
    }


    /**
     * Returns a list of Column objects for the given DataObject instance.
     *
     * @param object - the DataObject who's fields we want
     * @return - an array of all Columns (or descendants) found in that object.
     */
    private static Column[] __get_object_fields(DataObject object)
    {
        // perform basic validation to ensure that we are not trying to save a class type that is
        // not a child class of DataObject.
        if (!__validate_descendant_of_dataobject(object.getClass()))
            return new Column[0];

        Field[] holding_array = object.getClass().getDeclaredFields();
        List<Column> holding_list = new LinkedList<>();

        try
        {
            for (Field f : holding_array)
                holding_list.add((Column) f.get(object));
        }
        catch (Exception e)
        {
            // do nothing
        }

        Column[] results = new Column[holding_list.size()];
        return holding_list.toArray(results);
    }


    /**
     * Returns an array containing the names of every Column type field in the class.
     *
     * @param class_type The DataObject child that we are listing the fields for
     * @return The array of strings
     */
    private static String[] __get_column_declarations(Class class_type)
    {
        // perform basic validation to ensure that we are not trying to save a class type that is
        // not a child class of DataObject.
        if (!__validate_descendant_of_dataobject(class_type))
            return null;

        Field[] holding_array = class_type.getDeclaredFields();
        List<String> holding_list = new LinkedList<>();

        for (Field f: holding_array)
        {
            Class current_class = f.getType();
            try
            {
                String res = (String) current_class.getMethod("SQLColumnDescriptor").invoke(null);
                holding_list.add(f.getName() + " " + res);
            }
            catch (Exception e)
            {
                // haha do nothing.
            }
        }

        String[] results = new String[holding_list.size()];
        return holding_list.toArray(results);
    }


    /**
     * Tests to determine whether or not the class that has been passed to this function
     * is a descendant of DataObject.
     *
     * SimpleDB is only able to work with descendants of that class.
     *
     * @param unknown The class that is being tested.
     * @return True if the class is a valid descendant, false otherwise.
     */
    private static boolean __validate_descendant_of_dataobject(Class unknown)
    {
        boolean res = false;

        Class parent = unknown.getSuperclass();
        if (parent != null)
            if (parent.getSimpleName().equals("DataObject"))
                res = true;

        return res;
    }


    /**
     * Drops a table from the database, but by table name, not class type.
     *
     * @param table_name The table to delete.
     */
    private static void __delete_table_by_name(String table_name)
    {
        String query = "DROP TABLE IF EXISTS " + table_name;

        try {
            DBManager.Execute(query);
        } catch (SQLException e) {
            System.out.println("SQL error on drop table " + table_name);
            e.printStackTrace();
        }
    }


    /**
     * Loads all of this object's fields from the given dataset.
     * The field names - which it obtains through a bit of reflection -
     * form the names of the database columns. Brilliant, no?
     *
     * @param object_type The type of object to load / the table to load from.
     * @param dataset The database recordset to load data from.
     * @return An instance of object_type, containing the data loaded from dataset.
     */
    private static DataObject __load_from_db(Class object_type, ResultSet dataset)
    {
        DataObject result = null;
        try
        {
            result = (DataObject) object_type.newInstance();
            result.SetColumnNames();

            Column[] fields = __get_object_fields(result);
            if (fields == null)
                return null;

            for (Column f: fields)
                f.DBUpdateValue(dataset.getString(f.Name()));

            result.PrimaryKey = dataset.getInt("PrimaryKey");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * Processes the supplied SQL query and returns an array of DataObject instances
     * that have been initialised to the data returned by the database.
     *
     * @param object_type The type of objects to return.
     * @param query The SQL query to execute
     * @return An array of DataObject instances.
     */
    private static DataObject[] __load_all_from_db(Class object_type, String query)
    {
        List<DataObject> result_list = new LinkedList<>();

        Connection connection = null;
        ResultSet results = null;
        try
        {
            connection = DBManager.Connect();
            results = DBManager.ExecuteQuery(query, connection);
            while (results.next())
                result_list.add(__load_from_db(object_type, results));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            DBManager.Disconnect(results);
            DBManager.Disconnect(connection);
        }

        DataObject[] res = new DataObject[result_list.size()];
        return result_list.toArray(res);
    }
}
