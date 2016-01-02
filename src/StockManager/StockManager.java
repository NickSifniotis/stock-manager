package StockManager;

import StockManager.Objects.Item;
import StockManager.Objects.ShoppingTuple;
import StockManager.SimpleDatabase.DataObject;
import StockManager.SimpleDatabase.SimpleDB;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Nick Sifniotis u5809912
 * @since 31/12/2015
 * @version 1.0.0
 */
public class StockManager
{
    public static void PerformStocktake()
    {
        Item[] items = __get_items();

        System.out.println("Performing a stocktake on " + items.length + " items.\n");

        System.out.println("Enter the date of the stocktake (leave blank for today's date): ");
        Scanner in = new Scanner(System.in);
        String unparsed_date = in.nextLine();

        Date date;
        if (unparsed_date.equals(""))
            date = new Date();
        else
            date = InventoryEngine.ParseDate(unparsed_date);

        for (Item i: items)
        {
            System.out.print("Stock on hand for item " + i.item_name.Value + ": ");
            int quantity = Integer.parseInt(in.nextLine());

            InventoryEngine.AddStocktakeRecord(i, quantity, date);
        }

        System.out.println("Stocktake complete. Thank you, come again.");
    }


    public static void PerformRestock()
    {
        // @TODO this code needs to be refactored. One method for both would be better.
        Item[] items = __get_items();

        System.out.println("Performing a restock on " + items.length + " items.\n");

        System.out.println("Enter the date the stock was received (leave blank for today's date): ");
        Scanner in = new Scanner(System.in);
        String unparsed_date = in.nextLine();

        Date date;
        if (unparsed_date.equals(""))
            date = new Date();
        else
            date = InventoryEngine.ParseDate(unparsed_date);

        for (Item i: items)
        {
            System.out.print("Stock received for item " + i.item_name.Value + ": ");
            int quantity = Integer.parseInt(in.nextLine());

            InventoryEngine.AddRestockRecord(i, quantity, date);
        }

        System.out.println("Restock complete. Thank you, come again.");
    }


    public static void GenerateShoppingList()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date start_date = null;
        try
        {
            start_date = sdf.parse("01/12/2015");
        }
        catch (Exception e)
        {
            // it'll never ever happen
        }

        List<ShoppingTuple> usage_rates = InventoryEngine.ComputeConsumption(__get_items());
        for (ShoppingTuple record: usage_rates)
        {
            Item i = record.item;
            int on_hand = record.current_record.quantity.Value;
            int days_remaining = (int)(on_hand / record.consumption_rate);
            Date finished_date = InventoryEngine.AddDays(start_date, record.current_record.date.Value + days_remaining);

            System.out.println("ITEM: " + i.item_name.Value + "  ON HAND: " + on_hand
                    + "  USAGE: " + (record.consumption_rate * 7) + " " + i.item_quantity.Value + "/week  EST REMAINING: "
                    + days_remaining + "  EST RUNOUT DATE: " + sdf.format(finished_date));
        }
    }


    public static void main(String[] args)
    {
        GenerateShoppingList();
    }

    private static Item[] __get_items()
    {
        DataObject[] items = SimpleDB.LoadAll(Item.class);
        Item[] res = new Item[items.length];

        for (int i = 0; i < items.length; i ++)
            res[i] = (Item)items[i];

        return res;
    }
}
