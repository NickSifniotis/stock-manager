package StockManager;

import org.apache.commons.cli.*;

/**
 * <p>
 *     Static class that parses the command-line options passed to the application.
 * </p>
 *
 * <p>
 *     The command line options are parsed into public static boolean values that can be accessed by any
 *     part of the application.
 * </p>
 *
 * @author Nick Sifniotis u5809912
 * @since 03/01/2016
 * @version 1.0.0
 */
public class SystemOptions
{
    /** True if the user requested a stocktake. **/
    public static boolean do_stocktake;

    /** True if the user requested a restock / stock adjustment. **/
    public static boolean do_restock;

    /** True if the user asked for shopping list generation. **/
    public static boolean do_shopping;

    /** True if the program is to dump as much information as possible to the screen. **/
    public static boolean verbose;

    /** True if the user simply wants a status report. **/
    public static boolean do_status;

    /** True if user wants to operate on a database. **/
    public static boolean databases_are_target;

    /** True if user wants to operate on an item. **/
    public static boolean items_are_target;

    /** True if user wants to operate on stock levels. **/
    public static boolean stocks_are_target;

    /** The name of the database that the user wants to use for this operation. **/
    public static String database_name;


    /**
     * <p>
     *     Parses the command line arguments sent by the user, and sets the values of the boolean fields.
     * </p>
     *
     * @param args The unprocessed command line arguments, sent straight from int main.
     */
    public static void ParseOptions (String[] args)
    {
        Options options = __get_options();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try
        {
            cmd = parser.parse(options, args);
        }
        catch (ParseException e)
        {
            System.out.println("Error parsing command line arguments.");
            System.exit(1);
        }

        do_stocktake = cmd.hasOption("stocktake");
        do_restock = cmd.hasOption("restock");
        do_shopping = cmd.hasOption("shopping");

        do_status = cmd.hasOption("status");
        if (do_status)
            switch(cmd.getOptionValue("status"))
            {
                case "databases":
                    databases_are_target = true;
                    break;
                case "items":
                    items_are_target = true;
                    break;
                case "stocks":
                    stocks_are_target = true;
                    break;
            }

        database_name = (cmd.getOptionValue("database") == null) ? "default" : cmd.getOptionValue("database");
        verbose = cmd.hasOption("verbose");

        if (cmd.hasOption("h"))
            __show_help(options);
    }


    /**
     * <p>
     *     Displays the help text for this application's command line options.
     * </p>
     *
     * @param options The options that this application recognises.
     */
    private static void __show_help(Options options)
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("StockManager", options );
    }


    /**
     * <p>
     *     A private helper method that creates the command line options that this application recognises.
     * </p>
     *
     * @return The CLI options, in the form that the apache.commons CLI library understands.
     */
    private static Options __get_options()
    {
        Options res = new Options();
        res.addOption(Option.builder("v")
                .desc("Display as much information as possible.")
                .longOpt("verbose")
                .build());

        res.addOption(Option.builder("s")
                .desc("Display the current status. 'database' will show all databases, and 'item' will show all items.")
                .longOpt("status")
                .numberOfArgs(1)
                .build());

        res.addOption(Option.builder("d")
                .desc("Selects the database to use.")
                .longOpt("database")
                .numberOfArgs(1)
                .build());

        res.addOption(Option.builder("stocktake")
                .desc("Perform a stocktaking operation.")
                .build());

        res.addOption(Option.builder("restock")
                .desc("Perform a restocking.")
                .build());

        res.addOption(Option.builder("shopping")
                .desc("Generate a shopping list based on current stock levels.")
                .build());

        res.addOption(Option.builder("h")
                .desc("Show list of command line options.")
                .longOpt("help")
                .build());

        return res;
    }
}
