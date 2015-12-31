package StockManager.Objects;

import StockManager.SimpleDatabase.Columns.BooleanColumn;
import StockManager.SimpleDatabase.Columns.IntegerColumn;
import StockManager.SimpleDatabase.DataObject;

/**
 * <p>
 *     Records a snapshot of an item's stock level at one point in time.
 * </p>
 *
 * <p>
 *     TBA
 * </p>
 *
 * @author Nick Sifniotis u5809912
 * @since 31/12/2015
 * @version 1.0.0
 */
public class StockRecord extends DataObject
{
    public IntegerColumn item_id;
    public IntegerColumn quantity;
    public IntegerColumn date;
    public BooleanColumn is_restock;
}
