package StockManager.Objects;

import NickSifniotis.SimpleDatabase.*;
import NickSifniotis.SimpleDatabase.Columns.*;

import java.util.HashMap;
import java.util.Map;


/**
 * <p>
 *     Table that represents one 'database', or collection of records for one organisation or entity
 *     that wishes to manage its stock using this system.
 * </p>
 *
 * @author Nick Sifniotis u5809912
 * @version 1.0.0
 * @since 06/01/2014
 */
public class Database extends DataObject
{
    /** The name of the 'database' of records. **/
    public TextColumn database_name = new TextColumn();


    /**
     * <p>
     *     Loads all database rows from the, ahem, database, and returns them in the most useful
     *     format possible; a map that associates the record's primary keys with the records themselves.
     * </p>
     *
     * @return A map that contains all database rows loaded from the database.
     */
    public static Map<Integer, Database> LoadAll()
    {
        DataObject[] unprocessed_load = SimpleDB.LoadAll(Database.class);
        Map <Integer, Database> res = new HashMap<>();

        for (DataObject o: unprocessed_load)
        {
            Database processed = (Database) o;
            res.put(processed.PrimaryKey, processed);
        }

        return res;
    }
}
