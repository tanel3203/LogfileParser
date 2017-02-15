package ee.timing;

/**
 * Main class for running the application from the command line.
 */

public class Main {

    //  ----------------------------------- TEMPORARY
    private static final String PATH_NAME = "resources/timing.log";

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
/*
        // Store command line argument count in variable for use in conditional logic handling
        int argumentsLength = args.length;

        // Logic for handling user input
        if (argumentsLength < 1 || argumentsLength > 2) {
            throw new Exception("Illegal number of arguments. 1-2 arguments are allowed. One log file argument and optionally '-h' for help info");
        } else if (argumentsLength >= 1 && argumentsLength <= 2) {
            if (args[0] == "-h") {
                outputHelperInfo();
                // startLogFileClean(args[1]);
            } else if (args[1] == "-h") {
                outputHelperInfo();
                // startLogFileClean(args[0]);
            } else if (argumentsLength == 1) {
                // startLogFileClean(args[0]);
            } else {
                throw new Exception("Illegal number of arguments. 1-2 arguments are allowed. One log file argument and optionally '-h' for help info");
            }
        } else {
            throw new Exception("Unexpected outcome during command input. Please try again. 1-2 arguments are allowed. One log file argument and optionally '-h' for help info");
        }
*/
        // TEMPORARY
        outputHelperInfo();
        startLogFileClean(PATH_NAME);

        // Stop stopwatch for program run time
        long endTime = System.currentTimeMillis();

        // Print out total time taken for program to run (in ms)
        System.out.println("Program ran for " + (endTime - startTime) + " milliseconds");

    }

    private static void startLogFileClean(String logFile) throws Exception {

        // Initialize logfile cleaner
        CleanerService cleanerService = new CleanerService();

        // Run cleaner
        cleanerService.generateLogFileData(logFile);

    }

    private static void outputHelperInfo() {
        System.out.println("--------------------------------------------------------------");
        System.out.format("%10s%50s", "usage", "description");
        System.out.println();
        System.out.format("%10s%50s", "'-h'", "tag to access this page");
        System.out.println();
        System.out.format("%10s%50s", "'ant run'", "builds and runs current program with presets");
        System.out.println();
        System.out.format("%10s%50s", "'ant build'", "builds current program");
        System.out.println();
        System.out.format("%10s%50s", "'java -jar <jarpath> <logfilepath> <n>'", "\n\t\t\t\t\t\tbuilds logfile program w/ n rows");
        System.out.println();
        System.out.println("--------------------------------------------------------------");
        System.out.println("Example use: go to program directory (where build.xml lives), run 'ant build'");
        System.out.println("\tthen go to dist folder and run 'java -jar assignment.jar timing.log 10'");
        System.out.println("Results: 1) 10 lines of most time-intensive resources ");
        System.out.println("\t\t 2) Table with four columns showing hourly request distribution, ");
        System.out.println("\t\t includes histogram showing where most of the resources are located");
        System.out.println("--------------------------------------------------------------");
    }
}
