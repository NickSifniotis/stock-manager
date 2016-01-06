package StockManager.Objects;

import NickSifniotis.SimpleDatabase.Columns.*;
import NickSifniotis.SimpleDatabase.*;

import java.util.*;


/**
 * <p>
 *     Records a snapshot of an item's stock level at one point in time.
 * </p>
 *
 * <p>
 *     TBA
 * </p>
 *
 * @author Nick Sifniotis u5809912
 * @since 31/12/2015
 * @version 1.0.0
 */
public class StockRecord extends DataObject implements Comparable<StockRecord>
{
    /** The primary key of this record's row in the database. **/
    public IntegerColumn item_id = new IntegerColumn();

    /** The quantity of stock measured at this point in time. **/
    public IntegerColumn quantity = new IntegerColumn();

    /** The date that this stocktake or restock took place, measured as the number of days after 01/12/2015. **/
    public IntegerColumn date = new IntegerColumn();

    /** True if this is a restock record, false if this is a stocktake record. **/
    public BooleanColumn is_restock = new BooleanColumn();


    /**
     * <p>
     *     The default ordering for StockRecord objects is by date.
     * </p>
     *
     * @param o Another StockRecord object to compare to.
     * @return -1 if this instance is ordered before o, 0 if they are equal, and 1 if this instance is order after.
     */
    @Override
    public int compareTo(StockRecord o)
    {
        return Integer.compare(date.Value, o.date.Value);
    }


    /**
     * <p>
     *     Duplicate this StockRecord instance. Note that the duplication only affects the objects in memory;
     *     the database row that this StockRecord corresponds to is <i>not</i> duplicated.
     * </p>
     *
     * @return A deep copy of this instance.
     */
    public StockRecord copy()
    {
        StockRecord res = new StockRecord();
        res.item_id.Value = item_id.Value;
        res.quantity.Value = quantity.Value;
        res.date.Value = date.Value;
        res.is_restock.Value = is_restock.Value;

        return res;
    }


    /**
     * <p>
     *     Loads all StockRecords from the database, and returns them in a format that the InventoryEngine
     *     methods can use.
     * </p>
     *
     * @param items The items for which to load records.
     * @return A Map that associates items with lists of their StockRecords
     */
    public static Map<Item, List<StockRecord>> LoadStockRecords(Item[] items)
    {
        Map<Integer, Item> item_map = new HashMap<>();
        Map<Item, List<StockRecord>> record_lists = new HashMap<>();

        // set initial values
        for (Item i: items)
        {
            item_map.put(i.PrimaryKey, i);
            record_lists.put(i, new ArrayList<>());
        }

        // do the big data load from the database
        DataObject[] records = SimpleDB.LoadAll(StockRecord.class);
        for (DataObject o: records)
        {
            StockRecord record = (StockRecord) o;

            Item working_item = item_map.get(record.item_id.Value);
            if (working_item != null)
            {
                List<StockRecord> working_list = record_lists.get(working_item);
                if (working_list != null)
                    working_list.add(record);
            }
        }

        // filter out lists with obviously insufficient data.
        for (Item item: items)
        {
            List record_list = record_lists.get(item);
            if (record_list.size() < 2)
            {
                System.out.println("Unable to compute for " + item.item_name.Value + ": Not enough data");
                record_lists.remove(item);
            }
        }

        return record_lists;
    }
}
