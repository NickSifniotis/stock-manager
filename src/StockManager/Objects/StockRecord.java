package StockManager.Objects;

import StockManager.SimpleDatabase.Columns.BooleanColumn;
import StockManager.SimpleDatabase.Columns.IntegerColumn;
import StockManager.SimpleDatabase.DataObject;
import StockManager.SimpleDatabase.SimpleDB;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


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
    public IntegerColumn item_id = new IntegerColumn();
    public IntegerColumn quantity = new IntegerColumn();
    public IntegerColumn date = new IntegerColumn();
    public BooleanColumn is_restock = new BooleanColumn();


    @Override
    public int compareTo(StockRecord o)
    {
        return Integer.compare(date.Value, o.date.Value);
    }


    public StockRecord copy()
    {
        StockRecord res = new StockRecord();
        res.item_id.Value = item_id.Value;
        res.quantity.Value = quantity.Value;
        res.date.Value = date.Value;
        res.is_restock.Value = is_restock.Value;

        return res;
    }


    public static Map<Item, List<StockRecord>> LoadStockRecords(Item[] items)
    {
        Map<Integer, Item> item_map = new HashMap<>();
        Map<Item, List<StockRecord>> record_lists = new HashMap<>();

        // set initial values
        for (Item i: items)
        {
            item_map.put(i.PrimaryKey, i);
            record_lists.put(i, new LinkedList<>());
        }

        // do the big data load from the database
        DataObject[] records = SimpleDB.LoadAll(StockRecord.class);
        for (DataObject o: records)
        {
            StockRecord record = (StockRecord) o;

            Item working_item = item_map.get(record.item_id.Value);
            if (working_item != null)
            {
                List working_list = record_lists.get(working_item);
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
