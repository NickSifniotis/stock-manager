package StockManager;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 *     Static class that contains functions for working with dates.
 * </p>
 *
 * <p>
 *     This system works with numbers of days only; weeks, months, and time are all non-essential.
 *
 *     Rather than using Java's Date class, dates in this system are represented as integers
 *     that represent how many days after a 'start date' a given date is.
 *
 *     This static class provides methods that facilitate conversion to and from these integers
 *     of stringy dates that can be read from the user or displayed on the console.
 * </p>
 *
 * @author Nick Sifniotis u5809912
 * @since 02/01/2016
 * @version 1.0.0
 */
public class Calendar
{
    private static SimpleDateFormat australian_dates = new SimpleDateFormat("dd/MM/yyyy");
    private static java.util.Calendar cal = java.util.Calendar.getInstance();
    private static Date start_date;
    private static boolean initialised;


    public String GetDateAsString(int date)
    {
        if (!initialised)
            __initialise();

        cal.set(2015, java.util.Calendar.DECEMBER, 1);
    }


    public int GetDateAsInt(String unparsed_date)
    {
        if (!initialised)
            __initialise();

    }


    private static void __initialise()
    {
        try
        {
            start_date = australian_dates.parse("01/12/2015");
            initialised = true;
        }
        catch (Exception e)
        {
            // this will never fail. Never.
        }
    }
}
