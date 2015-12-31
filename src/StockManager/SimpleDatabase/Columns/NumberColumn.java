package StockManager.SimpleDatabase.Columns;

/**
 * Represents a real number column in the database.
 *
 * @author Nick Sifniotis u5809912
 * @since 09/11/2015
 * @version 1.2.0
 */
public class NumberColumn extends Column
{
    public double Value;


    /**
     * Default constructor. Sets the default value stored in this object to zero.
     */
    public NumberColumn()
    {
        this.Value = 0;
    }


    /**
     * Constructor that initialises this object's value to a number given by the user/programmer.
     *
     * @param v The value to initialise to.
     */
    public NumberColumn (double v)
    {
        this.Value = v;
    }


    /**
     * @return the current value of this field, in a DB-friendly way.
     */
    @Override
    public String SQLFieldValue()
    {
        return String.valueOf(this.Value);
    }


    /**
     * Updates the value of this field with data from the DB.
     *
     * @param new_value - the value returned from the database.
     */
    @Override
    public void DBUpdateValue(String new_value)
    {
        this.Value = Double.parseDouble(new_value);
    }


    /**
     * This method returns the column type that the database will use to store these columns.
     *
     * @return An SQL fragment that can be injected directly into queries.
     */
    public static String SQLColumnDescriptor()
    {
        return "REAL";
    }
}
