package StockManager.Reports;

/**
 * <p>
 *     Abstract base class for reports.
 * </p>
 *
 * @author Nick Sifniotis u5809912
 * @since 06/01/2016
 * @version 1.0.0
 */
public abstract class Report
{
    /**
     * <p>
     *     Abstract method for data generation and processing. Reports must know how to generate themselves.
     * </p>
     */
    public abstract void GenerateReport();


    /**
     * <p>
     *     Display the results of this report to the screen. @Todo it's probable that this won't be implemented here
     * </p>
     */
    public abstract void DisplayReport();
}
