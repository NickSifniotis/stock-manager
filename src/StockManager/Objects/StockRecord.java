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
public class StockRecord extends DataObject implements Comparable<StockRecord>
{
    public IntegerColumn item_id = new IntegerColumn();
    public IntegerColumn quantity = new IntegerColumn();
    public IntegerColumn date = new IntegerColumn();
    public BooleanColumn is_restock = new BooleanColumn();


    @Override
    public int compareTo(StockRecord o)
    {
        return Integer.compare(date.Value, o.date.Value);
    }
}
