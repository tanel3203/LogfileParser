package ee.timing;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * singleton Service class used in Main.java to
 *                  parse the log file
 *                  extract
 *                  aggregate and
 *                  present data from it
 */

class CleanerService {

    // Singleton object
    private static CleanerService instance = new CleanerService();

    // Class objects
    private ArrayList<LogFileLineStorable> logFileDataPrelim = new ArrayList<>(); // contains all needed logfile data
    private ArrayList<LogFileTempStorable> logFileDataTemp = new ArrayList<>(); // contains all needed logfile data
    private ArrayList<LogFileHistogramHourStorable> logFileHistogramHourData = new ArrayList<>(); // contains hourly request data
    private List<String> currentLine; // variable for scanning through logfile row by row

    // Class static variables
    private static int currentLineCounter = 0;

    // Constants
    private static final String FILE_COLUMN_DELIMITER = " ";
    private static final String RESOURCE_NAME_PREFIX_REGEX = "(^/)";
    private static final String RESOURCE_NAME_SUFFIX_REGEX = "([.?])";

    // Constructor disables inward instantiation
    private CleanerService() {
    }

    // Exposes the only instance
    static CleanerService getInstance() {
        return instance;
    }

    /**
     * Gets the logfile and prepares an object that can be used for further manipulation
     * @param pathToLogfile             String type logfield name/path
     * @throws Exception
     */
    //
    void generateLogFileData(String pathToLogfile) throws Exception {

        // Get the file
        Scanner input = new Scanner(new File(pathToLogfile));

        // Get every line from file and with a pre-determined delimiter prepare
        //                              1) logFileData           - object for time-heavy resources
        //                              2) logHistogramHourData  - object for hourly info and a histogram
        while (input.hasNextLine()) {

            // Set counter to current line;
            currentLineCounter++;

            // Get data line
            String currentInputLine = input.nextLine();

            // Use delimiter to separate line content and add to currentLine variable
            currentLine = Arrays.asList(currentInputLine.split(FILE_COLUMN_DELIMITER));

            // Get the item count of current line
            int currentLineSize = currentLine.size();

            // Get columns from logfile line
            if (currentLineSize == 7 || currentLineSize == 8 || currentLineSize == 9) {

                // Variables for logFileData
                String currentLineResourceName = getResourceName(currentLine.get(4));
                String currentLineRequestDuration = currentLine.get(currentLine.size() - 1);

                // Additional variables for logFileHistogramData
                String currentLineTimestampHour = getHourStringFromTimestamp(currentLine.get(1));

                // Add new object to logFileDataPrelim
                logFileDataPrelim.add(new LogFileLineStorable(currentLineResourceName, currentLineRequestDuration));

                // Find if logFileHistogramData already has data for the given hour
                int hourExistsIndex = getIndexOfExistingHour(logFileHistogramHourData, currentLineTimestampHour);

                // Update current record in existing logFileHistogramData
                if (hourExistsIndex >= 0) {

                    // Get the existing hour request count so far
                    int existingHourDataCount = logFileHistogramHourData.get(hourExistsIndex).getRequestCount();

                    // Update the request count
                    existingHourDataCount++;

                    // Update the hour object with updated object
                    logFileHistogramHourData.set(hourExistsIndex,
                            new LogFileHistogramHourStorable(currentLineTimestampHour, existingHourDataCount));
                }
                // Add new record into logFileData
                else if (hourExistsIndex == -1) {
                    logFileHistogramHourData.add(new LogFileHistogramHourStorable(currentLineTimestampHour, 1));
                } else {
                    throw new IllegalArgumentException("Unexpected result on line " + currentLineCounter
                            + " with value " + currentLineTimestampHour);
                }
            } else {
                throw new IllegalArgumentException("Current line in logfile does not conform to predefined requirements on line " + currentLineCounter
                                        +   ". It is most likely due to the line having either more or less arguments than in specification, it has " + currentLineSize
                                        +   " arguments.");
            }
        }

        // logFileDataPrelim
        // Find if logFileDataPrelim already has a record of current resource
        Map<String, Double> logFileDataUnique = logFileDataPrelim.stream().collect(
                Collectors.groupingBy(LogFileLineStorable::getResourceName, Collectors.averagingInt(LogFileLineStorable::getRequestDurationCast))
        );



        System.out.println("##################################################");
        logFileDataUnique.forEach((name,value) -> logFileDataTemp.add(new LogFileTempStorable(name, (double) Math.round(value))));

        // Sort logFileHistogramData by hour (00-23)
        Collections.sort(logFileDataTemp, new Comparator<LogFileTempStorable>() {
            @Override
            public int compare(LogFileTempStorable p1, LogFileTempStorable p2) {
                return (int) p2.getRequestDuration() - (int) p1.getRequestDuration(); // Descending
            }
        });

        //logFileDataTemp.forEach((name,value) -> System.out.println(name + " " + value));
        System.out.println("##################################################");

        // Sort logFileHistogramData by hour (00-23)
        Collections.sort(logFileHistogramHourData, new Comparator<LogFileHistogramHourStorable>() {
            @Override
            public int compare(LogFileHistogramHourStorable p1, LogFileHistogramHourStorable p2) {
                return p1.compareTimestamp() - p2.compareTimestamp(); // Ascending
            }
        });

        // Output top n resources by request duration
        int n = 10;
        int counter = 0;
        for (LogFileTempStorable item : logFileDataTemp) {

            if (counter == n) {
                break;
            }

            System.out.format("%40s%15d", item.getResourceName(), (int) item.getRequestDuration());
            System.out.println();
            counter++;
        }

        // Output hourly information with histogram
        System.out.format("%15s%15s%15s%3s%15s", "Hour", "Request count", "Total requests", "   ", " Requests (%)");
        System.out.println("");
        System.out.println("--------------------------------------------------------------");
        for (LogFileHistogramHourStorable item : logFileHistogramHourData) {
            System.out.format("%15s%15d%15d%3s", item.getTimestamp(), item.getRequestCount(), currentLineCounter, " | ");
            double percentOfTotal = 10 * (double) item.getRequestCount() / currentLineCounter;

            for (int i = 0; i < percentOfTotal; i++) {
                System.out.print("#");
            }
            System.out.println("");
            System.out.println("--------------------------------------------------------------");

        }

    }

    /**
     * Cleans resource name string if necessary (e.g. it is a full URI+query string)
     * @param resourceString            takes a string type variable and either returns it
     *                                  if it has no additional information attached to it
     *                                  or cleans that information and returns a clean
     *                                  resource name
     *
     * @return                          returns String type resource name
     */
    String getResourceName(String resourceString) {

        // Find if given string is already a resource name, then return the initial string
        if ((!resourceString.contains("/"))
                && (!resourceString.contains("."))
                && (!resourceString.contains("?"))) {
            return resourceString;
        }

        // Find the resource name from the resource string eliminating unnecessary URI and query string content
        String resourceName = resourceString.replaceAll(RESOURCE_NAME_PREFIX_REGEX, "").split(RESOURCE_NAME_SUFFIX_REGEX)[0];

        return resourceName;
    }

    /**
     * Finds the index of existing resource name for logFileData
     * @param array                 array the resource name is searched from
     * @param searchString          resource name that is looked for
     * @return                      returns index of existing resource name
     *                              if none exists, returns -1
     */
    int getIndexOfExistingResource(ArrayList<LogFileLineStorable> array, String searchString) {

        int index = -1;
        boolean matchFound = false;

        // Iterate until match is found
        for (LogFileLineStorable item : array) {
            index++;
            if (item.containsName(searchString)) {
                matchFound = true;
                break;
            }
        }

        // If no match found, set index to initial value
        if (!matchFound) {
            index = -1;
        }

        return index;
    }

    /**
     * Finds the index of existing hour object for logFileHistogramHourData
     * @param array                     array the hour object is searched from
     * @param searchString              hour object identifier that is looked for
     * @return                          returns index of existing object
     *                                  if none exists, returns -1
     */
    int getIndexOfExistingHour(ArrayList<LogFileHistogramHourStorable> array, String searchString) {

        int index = -1;
        boolean matchFound = false;

        // Iterate until match is found
        for (LogFileHistogramHourStorable item : array) {
            index++;
            if (item.containsTimestamp(searchString)) {
                matchFound = true;
                break;
            }
        }

        // If no match found, set index to initial value
        if (!matchFound) {
            index = -1;
        }

        return index;
    }

    /**
     * Gets the hour string from full timestamp (i.e. '00' from '00:01:23,794')
     * @param timestamp             full timestamp String value (e.g. '00:01:23,794')
     * @return                      returns hour String value (e.g. '00')
     */
    String getHourStringFromTimestamp(String timestamp) {
        String hourValue = timestamp.substring(0, timestamp.indexOf(":"));
        if (hourValue.length() != 2) {
            /*throw new IllegalFormatException("Value '" + hourValue + "' does not conform to expected value length");*/
            // add test case
        }
        if ((Integer.parseInt(hourValue) <= 24) && Integer.parseInt(hourValue) >= 0) {
            /* throw ... */
            // add test case
        }
        return hourValue;
    }
}
