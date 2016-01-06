package StockManager.Objects;

import NickSifniotis.SimpleDatabase.*;
import NickSifniotis.SimpleDatabase.Columns.*;


/**
 * <p>
 *     Represents a 'type' of stock that the stock manager keeps track of.
 * </p>
 *
 * <p>
 *     Instances of the 'item' class store data relating to the types of items that the
 *     stock manager manages. Right now, the data that is stored is the name of the item
 *     (eg. 'toilet paper') and a description of the units that the item is measured in
 *     (eg. 'each / kg / mL / pkts)
 * </p>
 *
 * @author Nick Sifniotis u5809912
 * @since 31/12/2015
 * @version 1.0.0
 */
public class Item extends DataObject
{
    public TextColumn item_name = new TextColumn();
    public TextColumn item_quantity = new TextColumn();


    public static Item[] LoadAll()
    {
        DataObject[] items = SimpleDB.LoadAll(Item.class);
        Item[] res = new Item[items.length];

        for (int i = 0; i < items.length; i++)
            res[i] = (Item) items[i];

        return res;
    }
}
