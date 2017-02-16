package ee.timing;

/**
 * Main class for running the application from the command line.
 */

public class Main {

    // Constants
    private static final int MAX_RESOURCE_REQUEST_COUNT = 50;
    private static final int ARGS_COUNT_LOWER_BOUND = 2;
    private static final int ARGS_COUNT_UPPER_BOUND = 3;
    private static final int ARGS_COUNT_NO_HELP = 2;
    private static final int ARGS_COUNT_WITH_HELP = 3;

    /**
     * Entry point that starts the application. The purpose is to show the n most time-duration heavy resources and
     * an hourly aggregation of requests. Additionally, an hourly requests histogram is displayed alongside the table.
     * @param args          application parameters. Program takes 1-2 arguments from command-line in no specific order.
     *                      (optionally) the '-h' tag argument which displays help information and
     *                      (mandatory) logfile name (if current location) or full path.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        // Start stopwatch for program run time
        long startTime = System.currentTimeMillis();

        // Store command line argument count in variable for use in conditional logic handling
        int argumentsLength = args.length;

        // Logic for handling user input
        if (argumentsLength < ARGS_COUNT_LOWER_BOUND || argumentsLength > ARGS_COUNT_UPPER_BOUND) {
            throw new Exception("Illegal request. 2-3 arguments are allowed. One log file argument, resource count and optionally '-h' for help info\n\n"
                    + "Use the following format: '[-h] <logfilepath> <resource count>");
        } else if (argumentsLength >= ARGS_COUNT_LOWER_BOUND && argumentsLength <= ARGS_COUNT_UPPER_BOUND) {
            if (args[0].length() == 2 && args[0].contains("h") && argumentsLength == ARGS_COUNT_WITH_HELP) {
                outputHelperInfo();
                startLogFileClean(args[1], args[2]);
            }  else if (Integer.parseInt(args[1]) < MAX_RESOURCE_REQUEST_COUNT && argumentsLength == ARGS_COUNT_NO_HELP) {
                startLogFileClean(args[0], args[1]);
            } else {
                throw new Exception("Illegal request. 2-3 arguments are allowed. One log file argument, resource count and optionally '-h' for help info\n\n"
                                    + "Use the following format: '[-h] <logfilepath> <resource count>");
            }
        } else {
            throw new Exception("Illegal request. 2-3 arguments are allowed. One log file argument, resource count and optionally '-h' for help info\n\n"
                    + "Use the following format: '[-h] <logfilepath> <resource count>");        }

        // Stop stopwatch for program run time
        long endTime = System.currentTimeMillis();

        // Print out total time taken for program to run (in ms)
        System.out.println("Program ran for " + (endTime - startTime) + " milliseconds");

    }

    /**
     * Starts the CleanerService with the reference to logfile
     * @param logFile            String type logfile reference
     * @param resourceCount      String type resource count - how many lines user wants to see - highest average duration
     * @throws Exception
     */
    private static void startLogFileClean(String logFile, String resourceCount) throws Exception {

        // Start logfile cleaner
        FileParserService fileParserService = FileParserService.getInstance();

        // Run cleaner
        fileParserService.generateLogFileData(logFile, resourceCount);

    }

    /**
     * Outputs to commandline helper info when '-h' tag is used
     */
    private static void outputHelperInfo() {
        System.out.println("--------------------------------------------------------------");
        System.out.format("\n%50s\n\n", "HELP INFO");
        System.out.println("--------------------------------------------------------------");
        System.out.format("%10s%50s\n", "usage", "description");
        System.out.format("%10s%50s\n", "'-h'", "tag to access this page");
        System.out.format("%10s%50s\n", "'ant run'", "builds and runs current program with presets");
        System.out.format("%10s%50s\n", "'ant build'", "builds current program");
        System.out.format("%10s%50s\n", "'java -jar <jarpath> <logfilepath> <n>'", "\n\t\t\tbuilds logfile program w/ n rows");
        System.out.println("--------------------------------------------------------------");
        System.out.println("Example use: go to program directory (where build.xml lives),");
        System.out.println("\trun 'ant build-jar', then go to dist folder and");
        System.out.println("\tadd a timing.log logfile to dist folder,");
        System.out.println("\trun 'java -jar assignment.jar -h timing.log 10'");
        System.out.println("\t (full path for log file needed, if not in dist folder)");
        System.out.println("Results: 1) help information ");
        System.out.println("\t 2) 10 lines of most time-intensive resources ");
        System.out.println("\t 3) Table with four columns showing hourly request distribution, ");
        System.out.println("\t includes histogram showing where most of the resources are located");
        System.out.println("--------------------------------------------------------------");
    }
}
