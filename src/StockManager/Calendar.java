package StockManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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


    /**
     * <p>
     *      Converts the internal int representation of a date back into a stringy format.
     * </p>
     *
     * @param date The date to convert to a string.
     * @return A stringy version of the date, suitable for displaying to the user, in Australian
     * date format.
     */
    public static String GetDateAsString(int date)
    {
        if (!initialised)
            __initialise();

        cal.setTime(start_date);
        cal.add(java.util.Calendar.DATE, date);
        return australian_dates.format(cal.getTime());
    }


    /**
     * <p>
     *     Parses a stringy date, in Australian date format, into an integer representation of that date
     *     as the number of days after an arbitary point in time.
     * </p>
     *
     * <p>
     *     The default 'origin' is the first of December 2015. Dates after that date are represented by positive
     *     integers, and days before it by negative integers.
     * </p>
     *
     * <p>
     *     This representation allows you to perform simple arithmetic operations on <i>dates</i>. Times are not
     *     represented in this system at all.
     * </p>
     *
     * @param unparsed_date The date, as input from the user (or other input source).
     * @return The integral representation of the date as the number of days elapsed since 01/12/2015.
     */
    public static int GetDateAsInt(String unparsed_date)
    {
        if (!initialised)
            __initialise();

        int res = -1;
        try
        {
            Date parsed_date = australian_dates.parse(unparsed_date);
            res = (int)getDateDiff(start_date, parsed_date);
        }
        catch (Exception e)
        {
            System.out.println("Error: " + unparsed_date + " is not a valid date.");
        }
        return res;
    }


    /**
     * <p>
     *     Initialise this class's static fields. Called once before the first time any public method
     *     is executed.
     * </p>
     */
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


    /**
     * <p>
     *      Get the number of days between two dates.
     * </p>
     *
     * @param date1 The oldest date
     * @param date2 The newest date
     * @return The number of days between the two dates.
     */
    private static long getDateDiff(Date date1, Date date2)
    {
        long diffInMillies = date2.getTime() - date1.getTime();
        return TimeUnit.MILLISECONDS.toDays(diffInMillies);
    }
}
