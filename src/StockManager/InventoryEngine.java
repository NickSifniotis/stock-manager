package StockManager;

import StockManager.Objects.Item;
import StockManager.Objects.ShoppingTuple;
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

    public static Date AddDays(Date date, int num_days)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, num_days);
        return c.getTime();
    }


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


    public static List<ShoppingTuple> ComputeConsumption(Item[] items)
    {
        List<ShoppingTuple> res = new LinkedList<>();
        Map<Item, List<StockRecord>> record_lists = StockRecord.LoadStockRecords(items);

        Set item_keys = record_lists.keySet();
        // work through the lists one by one.
        for (Iterator<Item> it = item_keys.iterator(); it.hasNext(); )
        {
            Item item = it.next();
            List record_list = record_lists.get(item);

            StockRecord[] record_array = new StockRecord[record_list.size()];
            record_list.toArray(record_array);

            Arrays.sort(record_array);
            int total_restocks = 0;
            StockRecord first = null;
            StockRecord last = null;
            StockRecord current = null;

            for (StockRecord record: record_array)
            {
                if (first == null)
                {
                    if (!record.is_restock.Value)
                        first = record;
                }
                else
                    if (record.is_restock.Value)
                    {
                        total_restocks += record.quantity.Value;
                        if (current != null)
                        {
                            current.date.Value = record.date.Value;
                            current.quantity.Value += record.quantity.Value;
                        }
                    }
                    else
                    {
                        last = record;
                        current = record.copy();
                    }
            }

            if (first != null && last != null)
            {
                int date_differential = last.date.Value - first.date.Value;
                int usage = first.quantity.Value + total_restocks - last.quantity.Value;

                ShoppingTuple item_record = new ShoppingTuple();
                item_record.item = item;
                item_record.current_record = current;
                item_record.consumption_rate = usage / (double) date_differential;

                res.add(item_record);
            }
            else
                System.out.println("Insufficient data for item " + item.item_name.Value + ". Please stocktake and try again.");
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
