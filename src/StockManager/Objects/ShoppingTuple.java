package StockManager.Objects;

/**
 * <p>
 *      Container object that stores both the rate of consumption of an item, and the current quanitity on hand
 *      (as computed by summing over the stock records)
 * </p>
 *
 * @author Nick Sifniotis u5809912
 * @since 02/01/2016
 * @version 1.0.0
 */
public class ShoppingTuple
{
    public Item item;
    public StockRecord current_record;
    public double consumption_rate;
}
