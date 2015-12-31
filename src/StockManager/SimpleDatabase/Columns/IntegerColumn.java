package StockManager.SimpleDatabase.Columns;

/**
 * A simple Integer column type for SimpleDB DataObjects.
 *
 * @author Nick Sifniotis u5809912
 * @since 09/11/2015
 * @version 1.0.0
 */
public class IntegerColumn extends Column
{
    public int Value;


    /**
     * Default constructor. Sets the default value stored in this object to zero.
     */
    public IntegerColumn()
    {
        this.Value = 0;
    }


    /**
     * Constructor that initialises this object's value to a number given by the user/programmer.
     *
     * @param v The value to initialise to.
     */
    public IntegerColumn (int v)
    {
        this.Value = v;
    }


    /**
     * @return Returns the current value of this field, in an SQL-happy format.
     */
    @Override
    public String SQLFieldValue()
    {
        return String.valueOf(this.Value);
    }


    /**
     * Updates this field with data retrieved from the database.
     *
     * @param new_value The value returned from the database.
     */
    @Override
    public void DBUpdateValue(String new_value)
    {
        this.Value = Integer.parseInt(new_value);
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
