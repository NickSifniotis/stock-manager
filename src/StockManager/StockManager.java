package StockManager;

import StockManager.Objects.Item;
import StockManager.SimpleDatabase.DataObject;
import StockManager.SimpleDatabase.SimpleDB;

import java.util.Date;
import java.util.Scanner;

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

    public static void main(String[] args)
    {
        PerformStocktake();
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
