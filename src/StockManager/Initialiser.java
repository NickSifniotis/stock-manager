package StockManager;

import StockManager.Objects.Item;
import StockManager.Objects.StockRecord;
import StockManager.SimpleDatabase.SimpleDB;

/**
 * <p>
 *      A static class that holds a number of system initialisation routines for testing purposes.
 * </p>
 *
 * @author Nick Sifniotis u5809912
 * @since 31/12/2015
 * @version 1.0.0
 */
public class Initialiser
{
    public static void main(String[] args)
    {
        // rebuild the database.
        SimpleDB.CreateTable(Item.class);
        SimpleDB.CreateTable(StockRecord.class);

        // put in some dummy data
        Item toilet_paper = new Item();
        toilet_paper.item_name.Value = "Toilet Paper";
        toilet_paper.item_quantity.Value = "rolls";

        SimpleDB.Save(toilet_paper);

        Item toothpaste = new Item();
        toothpaste.item_name.Value = "Toothpaste";
        toothpaste.item_quantity.Value = "tubes";
    }
}
