package ee.timing;

import java.io.File;
import java.util.*;

/**
 * Service class to
 *                  parse the log file
 *                  extract
 *                  aggregate and
 *                  present data from it
 */

class CleanerService {

    // Class objects
    private ArrayList<LogFileLineStorable> logFileData = new ArrayList<>();
    private ArrayList<LogFileHistogramHourStorable> logFileHistogramHourData = new ArrayList<>();
    private List<String> currentLine;

    // Class static variables
    private static int currentLineCounter = 0;

    // Constants
    private static final String FILE_COLUMN_DELIMITER = " ";
    private static final String RESOURCE_NAME_PREFIX_REGEX = "(^/)";
    private static final String RESOURCE_NAME_SUFFIX_REGEX = "([.?])";

    // Constructor
    CleanerService() {
    }

    /**
     * Gets the logfile and prepares an object that can be used for further manipulation
     * @param pathToLogfile
     * @throws Exception
     */
    //
    void generateLogFileData(String pathToLogfile) throws Exception {

        // Get the file
        Scanner input = new Scanner(new File(pathToLogfile));

        // Get every line from file and with a pre-determined delimiter prepare
        //                              1) logFileData           - outputs time-heavy resources
        //                              2) logHistogramHourData  - outputs hourly info and a histogram
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
                String currentLineTimestamp = currentLine.get(1);
                String currentLineResourceName = getResourceName(currentLine.get(4));
                String currentLineRequestDuration = currentLine.get(currentLine.size() - 1);

                // Additional variables for logFileHistogramData
                String currentLineTimestampHour = getHourStringFromTimestamp(currentLine.get(1));

                // Find if logFileData already has a record of current resource
                int resourceExistsIndex = getIndexOfExistingResource(logFileData, currentLineResourceName);

                // Find if logFileHistogramData already has data for the given hour
                int hourExistsIndex = getIndexOfExistingHour(logFileHistogramHourData, currentLineTimestampHour);

                // Update current record in existing logFileData
                if (resourceExistsIndex >= 0) {

                    // Get existing record's record duration and update it with the current duration
                    String existingRecordRequestDuration = logFileData.get(resourceExistsIndex).getRequestDuration();
                    String updatedRecordRequestDuration = String.valueOf(Integer.parseInt(existingRecordRequestDuration)
                                                                    + Integer.parseInt(currentLineRequestDuration));

                    // Set the existing record object in ArrayList to an updated object
                    logFileData.set(resourceExistsIndex,
                            new LogFileLineStorable(currentLineTimestamp, currentLineResourceName, updatedRecordRequestDuration));

                }
                // Add new record into logFileData
                else if (resourceExistsIndex == -1) {

                    // Add new object to logFileData
                    logFileData.add(new LogFileLineStorable(currentLineTimestamp, currentLineResourceName, currentLineRequestDuration));

                } else {
                    throw new IllegalArgumentException("Unexpected result with resource name on line " + currentLineCounter
                                                    + " with value " + currentLineResourceName);
                }

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

        // Sort filled logFileData object from highest request duration item to lowest
        Collections.sort(logFileData, new Comparator<LogFileLineStorable>() {
            @Override
            public int compare(LogFileLineStorable p1, LogFileLineStorable p2) {
                return p2.getRequestDurationCast() - p1.getRequestDurationCast(); // Descending
            }
        });

        // Sort logFileHistogramData by hour (00-23)
        Collections.sort(logFileHistogramHourData, new Comparator<LogFileHistogramHourStorable>() {
            @Override
            public int compare(LogFileHistogramHourStorable p1, LogFileHistogramHourStorable p2) {
                return p1.compareTimestamp() - p2.compareTimestamp(); // Ascending
            }
        });


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
