package StockManager.SimpleDatabase;

import StockManager.SimpleDatabase.Columns.Column;
import java.lang.reflect.Field;


/**
 * <p>
 *     DataObject is the base class for any objects that you wish to store in the database.
 * </p>
 * <p>
 *     To store data in a database, create a sub-class that inherits from DataObject.
 *     The data you want to store in the database needs to be held in public fields
 *     that are descended from the class Column. Basic data types are provided in this package;
 *     IntegerColumn, TextColumn, NumberColumn and BooleanColumn.
 * </p>
 * <p>
 *      There is no need to specify a database schema. The schema is inferred from
 *      the structure of the class itself. Any public Column field will be stored
 *      in the database.
 * </p>
 * <p>
 *     Please note that as of this writing, there is no implementation of any SQL Join queries.
 *     SELECTs affect one table only. Until this changes, do your joins in your Java code.
 *     (if you are relying on this library to do heavy database processing, you are using it wrong)
 * </p>
 *
 *
 * @author Nick Sifniotis u5809912
 * @since 07/11/2015
 * @version 1.2.0
 */
public abstract class DataObject
{
    /** The primary key of this object in the database. */
    public int PrimaryKey;


    /**
     * Finds all of this object's Column fields, and sets their names.
     * It might be possible to get a Column instance to infer its own name - maybe - watch
     * this space. For now, they have to be set and stored manually, like this.
     */
    public void SetColumnNames()
    {
        // this will be interesting
        Field[] fields = this.getClass().getFields();

        try
        {
            for (Field f : fields)
            {
                Column col = (Column) f.get(this);
                col.SetName(f.getName());
            }
        }
        catch (Exception e)
        {
            // no fucks given here
        }
    }


    /**
     * Set the default values for this DataObject's columns. If a column does not require a
     * default value, there is no need to set anything. If none of your columns need a default value,
     * there is no need to override this method.
     *
     * Note that if you do override this, it is imperative that your first line invokes this method.
     * {@code super.SetDefaults();}
     */
    public void SetDefaults()
    {
        PrimaryKey = -1;
    }
}
