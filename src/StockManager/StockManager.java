package StockManager;

import StockManager.Objects.Item;
import StockManager.Objects.ShoppingTuple;

import java.util.List;
import java.util.Scanner;

/**
 * @author Nick Sifniotis u5809912
 * @since 31/12/2015
 * @version 1.0.0
 */
public class StockManager
{
    public static void PerformStocktake(boolean restock)
    {
        String word = (restock) ? "restock" : "stocktake";
        Item[] items = Item.LoadAll();

        System.out.println("Performing a " + word + " on " + items.length + " items.\n");

        System.out.println("Enter the date of the " + word + " (leave blank for today's date): ");
        Scanner in = new Scanner(System.in);
        String unparsed_date = in.nextLine();
        int date = Calendar.GetDateAsInt(unparsed_date);

        for (Item i: items)
        {
            System.out.print("Stock on hand for item " + i.item_name.Value + ": ");
            int quantity = Integer.parseInt(in.nextLine());

            if (restock)
                InventoryEngine.AddRestockRecord(i, quantity, date);
            else
                InventoryEngine.AddStocktakeRecord(i, quantity, date);
        }

        System.out.println(word + " complete. Thank you, come again.");
    }


    public static void GenerateShoppingList()
    {
        List<ShoppingTuple> usage_rates = InventoryEngine.ComputeConsumption(Item.LoadAll());
        for (ShoppingTuple record: usage_rates)
        {
            Item i = record.item;
            int on_hand = record.current_record.quantity.Value;
            int days_remaining = (int)(on_hand / record.consumption_rate);
            int finishing_date = record.current_record.date.Value + days_remaining;

            System.out.println("ITEM: " + i.item_name.Value + "  ON HAND: " + on_hand
                    + "  USAGE: " + (record.consumption_rate * 7) + " " + i.item_quantity.Value + "/week  EST REMAINING: "
                    + days_remaining + "  EST RUNOUT DATE: " + Calendar.GetDateAsString(finishing_date));
        }

        System.out.println ("Enter the date that you want to replenish your stock to:");
        Scanner in = new Scanner(System.in);
        String unparsed_date = in.nextLine();
        int date_to = Calendar.GetDateAsInt(unparsed_date);

        for (ShoppingTuple record: usage_rates)
        {
            int stock_required = (int)((date_to - record.current_record.date.Value) * record.consumption_rate);
            int stock_to_buy = stock_required - record.current_record.quantity.Value;

            if (stock_to_buy > 0)
                System.out.println(record.item.item_name.Value + ": " + stock_to_buy + " " + record.item.item_quantity.Value);
        }
    }


    public static void main(String[] args)
    {
        SystemOptions.ParseOptions(args);

        if (SystemOptions.databases_are_target)
        {

        }

        if (SystemOptions.do_restock)
            PerformStocktake(true);

        if (SystemOptions.do_stocktake)
            PerformStocktake(false);

        if (SystemOptions.do_shopping)
            GenerateShoppingList();
    }
}
