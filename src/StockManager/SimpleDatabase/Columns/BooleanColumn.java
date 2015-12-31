package StockManager.SimpleDatabase.Columns;

/**
 * A vanilla Boolean column type for SimpleDB DataObjects.
 *
 * @author Nick Sifniotis u5809912
 * @since 09/11/2015
 * @version 1.2.0
 */
public class BooleanColumn extends Column
{
    public boolean Value;


    /**
     * Default constructor. Sets the default value stored in this object to false.
     */
    public BooleanColumn()
    {
        this.Value = false;
    }


    /**
     * Constructor that initialises this object's value to a number given by the user/programmer.
     *
     * @param v The value to initialise to.
     */
    public BooleanColumn (boolean v)
    {
        this.Value = v;
    }


    /**
     * @return an SQL-friendly representation of this field's current value
     */
    @Override
    public String SQLFieldValue()
    {
        return Value ? "1" : "0";
    }


    /**
     * Updates the current value of this field from the string returned by the DB.
     *
     * @param new_value - the value returned from the database.
     */
    @Override
    public void DBUpdateValue(String new_value)
    {
        this.Value = (new_value.equals("1"));
    }


    /**
     * This method returns the column type that the database will use to store these columns.
     *
     * @return An SQL fragment that can be injected directly into queries.
     */
    public static String SQLColumnDescriptor()
    {
        return "INTEGER";
    }
}
