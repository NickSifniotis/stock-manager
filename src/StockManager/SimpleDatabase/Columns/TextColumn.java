package StockManager.SimpleDatabase.Columns;

/**
 * A class that is a string field / table column.
 *
 * @author Nick Sifniotis u5809912
 * @since 09/11/2015
 * @version 1.2.0
 */
public class TextColumn extends Column
{
    public String Value;


    /**
     * Default constructor. Sets the default value stored in this object to the enpty string.
     */
    public TextColumn()
    {
        this.Value = "";
    }


    /**
     * Constructor that initialises this object's value to a number given by the user/programmer.
     *
     * @param v The value to initialise to.
     */
    public TextColumn (String v)
    {
        this.Value = v;
    }


    /**
     * @return the current value of the String, in an SQL-happy format.
     */
    @Override
    public String SQLFieldValue()
    {
        return "\"" + this.Value + "\"";
    }


    /**
     * Updates the value stored in this field.
     *
     * @param new_value - the value returned from the database.
     */
    @Override
    public void DBUpdateValue(String new_value)
    {
        this.Value = new_value;
    }


    /**
     * This method returns the column type that the database will use to store these columns.
     *
     * @return An SQL fragment that can be injected directly into queries.
     */
    public static String SQLColumnDescriptor()
    {
        return "TEXT";
    }
}
