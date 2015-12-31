package StockManager;

import StockManager.Objects.Item;
import StockManager.Objects.StockRecord;
import StockManager.SimpleDatabase.SimpleDB;

import java.text.SimpleDateFormat;
import java.util.Date;
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


    private static void __add_record(Item item, int quantity, Date date, boolean is_restock)
    {
        StockRecord new_record = (StockRecord) SimpleDB.New(StockRecord.class);
        new_record.item_id.Value = item.PrimaryKey;
        new_record.is_restock.Value = is_restock;
        new_record.quantity.Value = quantity;
        new_record.date.Value = (int)getDateDiff(new Date(2016, 1, 1), date, TimeUnit.DAYS);

        SimpleDB.Save(new_record);
    }


    /**
     * Shamelessly taken from the internet.
     *
     * Get a diff between two dates
     * @param date1 the oldest date
     * @param date2 the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit)
    {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}
