package StockManager;

import StockManager.Objects.Item;
import StockManager.Objects.StockRecord;
import StockManager.SimpleDatabase.DataObject;
import StockManager.SimpleDatabase.SimpleDB;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *     Static class that contains a variety of useful functions.
 * </p>
 *
 */
public class InventoryEngine
{
    public static Date ParseDate(String unparsed_date)
    {
        SimpleDateFormat sdfmt1 = new SimpleDateFormat("dd/MM/yy");
        Date res = null;
        try
        {
            res = sdfmt1.parse(unparsed_date);
        }
        catch (Exception e)
        {
            // @TODO make this meaningful
        }
        return res;
    }


    public static void AddStocktakeRecord(Item item, int quantity, Date date)
    {
        __add_record(item, quantity, date, false);
    }


    public static void AddRestockRecord(Item item, int quantity, Date date)
    {
        __add_record(item, quantity, date, true);
    }


    public static Map<Item, Double> ComputeConsumption(Item[] items)
    {
        Map<Item, Double> res = new HashMap<>();
        Map<Integer, Item> item_map = new HashMap<>();
        Map<Item, List<StockRecord>> record_lists = new HashMap<>();

        // set initial values
        for (Item i: items)
        {
            res.put(i, 0.0);
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

        // work through the lists one by one.
        for (Item item: items)
        {
            List record_list = record_lists.get(item);
            if (record_list.size() < 2)
            {
                System.out.println("Unable to compute for " + item.item_name.Value + ": Not enough data");
            }
            else
            {
                StockRecord[] record_array = new StockRecord[record_list.size()];
                record_list.toArray(record_array);

                Arrays.sort(record_array);
                int total_restocks = 0;
                StockRecord first = null;
                StockRecord last = null;

                for (StockRecord record: record_array)
                {
                    if (first == null)
                    {
                        if (!record.is_restock.Value)
                            first = record;
                    }
                    else
                        if (record.is_restock.Value)
                            total_restocks += record.quantity.Value;
                        else
                            last = record;
                }

                if (first != null && last != null)
                {
                    int date_differential = last.date.Value - first.date.Value;
                    int usage = first.quantity.Value + total_restocks - last.quantity.Value;

                    res.put(item, usage / (double) date_differential);
                }
                else
                    System.out.println("Insufficient data for item " + item.item_name.Value + ". Please stocktake and try again.");
            }
        }
        return res;
    }


    private static void __add_record(Item item, int quantity, Date date, boolean is_restock)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date start_date = null;
        try
        {
            start_date = sdf.parse("01/12/2015");
        }
        catch (Exception e)
        {
            // do nothing
        }

        StockRecord new_record = (StockRecord) SimpleDB.New(StockRecord.class);
        new_record.item_id.Value = item.PrimaryKey;
        new_record.is_restock.Value = is_restock;
        new_record.quantity.Value = quantity;
        new_record.date.Value = (int)getDateDiff(start_date, date);

        SimpleDB.Save(new_record);
    }


    /**
     * Shamelessly taken from the internet.
     *
     * Get a diff between two dates
     * @param date1 the oldest date
     * @param date2 the newest date
     * @return the diff value, in the provided unit
     */
    public static long getDateDiff(Date date1, Date date2)
    {
        long diffInMillies = date2.getTime() - date1.getTime();
        return TimeUnit.MILLISECONDS.toDays(diffInMillies);
    }
}
