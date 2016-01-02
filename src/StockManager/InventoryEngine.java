package StockManager;

import StockManager.Objects.Item;
import StockManager.Objects.ShoppingTuple;
import StockManager.Objects.StockRecord;
import StockManager.SimpleDatabase.SimpleDB;

import java.util.*;


/**
 * <p>
 *     Static class that contains a variety of useful helper functions.
 * </p>
 *
 * @author Nick Sifniotis u5809912
 * @since 31/12/2015
 * @version 1.0.0
 */
public class InventoryEngine
{
    /**
     * <p>
     *     Adds a stocktake record for an item into the database. The record is built based on the parameters
     *     passed into this method.
     * </p>
     *
     * <p>
     *     Stocktake records are 'snapshots in time' of how many <i>items</i> were on hand at a particular date.
     *     The quantity of items on hand can be zero, but it can never be negative. If a negative quantity is
     *     encountered, this method will fail silently.
     * </p>
     *
     * @param item The item that this record relates to.
     * @param quantity The number of items that were on hand at the time of the stocktake. If this parameter is
     *                 less than zero, this method fails silently.
     * @param date The date of the stocktake. This field is computed using the Stocktake.Calendar static methods.
     *             Do not try and peer inside this abstraction, just trust the Calendar to do its thing.
     */
    public static void AddStocktakeRecord(Item item, int quantity, int date)
    {
        if (quantity >= 0)
            __add_record(item, quantity, date, false);
    }


    /**
     * <p>
     *     Adds a restock record for an item into the database. The record is built based on the parameters
     *     passed into this method.
     * </p>
     *
     * <p>
     *     A restocking event is ostensibly an alteration of an item's stock level due to a purchase of more items,
     *     but it can be used for any extraordinary event which alters the stock levels of an item. For example,
     *     giving ten rolls of toilet paper away to a poor friend in need (?) will change the amount of toilet
     *     paper available, but it will not affect the usage rate of that item.
     * </p>
     *
     * <p>
     *     Usage rates are computed based on movements in stock levels that are not accounted for by these restock
     *     records. Therefore, there are no restrictions imposed on the quanitity parameter. A negative stock
     *     shift could be the result of giving away items, or loaning them, or shrinkage, etc ..
     * </p>
     *
     * @param item The item that this record relates to.
     * @param quantity The change in the number of items on hand. A positive change could signify the purchase of
     *                 more items, while a negative could signify theft, spoilage or loan of items.
     * @param date The date of the restock. This field is computed using the Stocktake.Calendar static methods.
     *             Do not try and peer inside this abstraction, just trust the Calendar to do its thing.
     */
    public static void AddRestockRecord(Item item, int quantity, int date)
    {
        __add_record(item, quantity, date, true);
    }


    /**
     * <p>
     *     Computes the rate of consumption of all items for which there is sufficient information available.
     * </p>
     *
     * <p>
     *     This method really gets to the heart of what this application is about. Using the stocktake and
     *     restock records created by the AddStocktakeRecord and AddRestockRecord methods, ComputeConsumption
     *     calculates the rate at which items are being consumed and returns this data, along with the most up-to-date
     *     stock counts for each item.
     * </p>
     *
     * <p>
     *     The consumption rate is the quantity of the item that is consumed per day. This value is computed by
     *     dividing the change in an items stock level by the number of days that the change in stock was measured
     *     over.
     * </p>
     *
     * @param items The Items for which we want to calculate the rates of consumption.
     * @return A List of ShoppingTuple instances. The ShoppingTuple class contains references to an Item,
     *         the current quantity of the item on hand (based on the last StockRecord available) and the
     *         rate of consumption of the item, as a double.
     */
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


    /**
     * <p>
     *     Inserts a new StockRecord into the database. The StockRecord is created based on the parameters
     *     passed into this method.
     * </p>
     *
     * @param item The item that this record refers to.
     * @param quantity If this is a stocktake record, the number of items on hand. If this is a restocking record,
     *                 the number of items that have been purchased.
     * @param date The date of the restock / stocktake.
     * @param is_restock TRUE if this record is for a restocking event, FALSE if it is for a stocktake event.
     */
    private static void __add_record(Item item, int quantity, int date, boolean is_restock)
    {
        StockRecord new_record = (StockRecord) SimpleDB.New(StockRecord.class);
        new_record.item_id.Value = item.PrimaryKey;
        new_record.is_restock.Value = is_restock;
        new_record.quantity.Value = quantity;
        new_record.date.Value = date;

        SimpleDB.Save(new_record);
    }
}
