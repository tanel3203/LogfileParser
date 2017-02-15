package ee.timing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

class Cleaner {

    // Class objects
    private ArrayList<LogFileLineStorable> logFileData = new ArrayList<>();
    private ArrayList<LogFileHistogramHourStorable> logFileHistogramHourData = new ArrayList<>();
    private List<String> currentLine;

    // Class static variables
    private static int currentLineCounter = 0;

    // Constants
    private static final String PATH_NAME = "resources/timing.log";
    private static final String FILE_COLUMN_DELIMITER = " ";
    private static final String RESOURCE_NAME_REGEX = "(^/)";

    // Constructor
    Cleaner() {
    }

    // Get the logfile and prepare an object that can be used for further manipulation
    void generateLogFileData() throws FileNotFoundException {

        // Get the file
        Scanner input = new Scanner(new File(PATH_NAME));

        // Get every line from file and prepare the object with a pre-determined delimiter
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
            if (currentLineSize == 7 || currentLineSize == 8) {

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

                //System.out.println(currentLineTimestamp + " " + currentLineResourceName + " " + currentLineRequestDuration);
                //System.out.println(currentLineTimestampHour + " " + currentLineRequestDuration);






                // Update current record in existing logFileData
                if (resourceExistsIndex >= 0) {

                    String existingRecordRequestDuration = logFileData.get(resourceExistsIndex).getRequestDuration();
                    int exRec = Integer.parseInt(existingRecordRequestDuration);
                    int currRec = Integer.parseInt(currentLineRequestDuration);
                    int newRec = exRec + currRec;
                    String newRecString = String.valueOf(newRec);

                    logFileData.set(resourceExistsIndex, new LogFileLineStorable(currentLineTimestamp, currentLineResourceName, newRecString));


                }
                // Add new record into logFileData
                else if (resourceExistsIndex == -1) {

                    logFileData.add(new LogFileLineStorable(currentLineTimestamp, currentLineResourceName, currentLineRequestDuration));




                } else {
                    throw new IllegalArgumentException("Unexpected result with resource name on line " + currentLineCounter
                                                    + " with value " + currentLineResourceName);
                }




                // Update current record in existing logFileHistogramData
                if (hourExistsIndex >= 0) {

                    int existingHourDataCount = logFileHistogramHourData.get(hourExistsIndex).getRequestCount();
                    existingHourDataCount++;

                    logFileHistogramHourData.set(hourExistsIndex, new LogFileHistogramHourStorable(currentLineTimestampHour, existingHourDataCount));


                }
                // Add new record into logFileData
                else if (hourExistsIndex == -1) {

                    logFileHistogramHourData.add(new LogFileHistogramHourStorable(currentLineTimestampHour, 1));




                } else {
                    throw new IllegalArgumentException("Unexpected result on line " + currentLineCounter
                            + " with value " + currentLineTimestampHour);
                }





            } else {
                /*throw new IllegalArgumentException("Current line in logfile does not conform to predefined requirements on line " + currentLineCounter
                                        +   ". It is most likely due to the line having either more or less arguments than in specification, it has " + currentLineSize
                                        +   " arguments.");*/
            }




        }

        Collections.sort(logFileData, new Comparator<LogFileLineStorable>() {
            @Override
            public int compare(LogFileLineStorable p1, LogFileLineStorable p2) {
                return p2.getRequestDurationCast() - p1.getRequestDurationCast(); // Descending
            }
        });

        Collections.sort(logFileHistogramHourData, new Comparator<LogFileHistogramHourStorable>() {
            @Override
            public int compare(LogFileHistogramHourStorable p1, LogFileHistogramHourStorable p2) {
                return p2.getRequestCount() - p1.getRequestCount(); // Descending
            }
        });

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

    // Clean resource name string if necessary (e.g. it is a full URI+query string)
    String getResourceName(String resourceString) {

        // Find if given string is already a resource name, then return the initial string
        if ((resourceString.indexOf("/") == -1) && (resourceString.indexOf(".") == -1) && (resourceString.indexOf("?") == -1)) {
            return resourceString;
        }

        // Find the resource name from the resource string eliminating unnecessary URI and query string content
        String resourceName = resourceString.replaceAll(RESOURCE_NAME_REGEX, "").split("([.?])")[0];

        return resourceName;
    }

    int getIndexOfExistingResource(ArrayList<LogFileLineStorable> array, String searchString) {
        int index = -1;
        boolean matchFound = false;
        for (LogFileLineStorable item : array) {

            index++;

            if (item.containsName(searchString)) {
                matchFound = true;
                break;
            }
        }

        if (!matchFound) {
            index = -1;
        }

        return index;
    }

    int getIndexOfExistingHour(ArrayList<LogFileHistogramHourStorable> array, String searchString) {
        int index = -1;
        boolean matchFound = false;
        for (LogFileHistogramHourStorable item : array) {

            index++;

            if (item.containsTimestamp(searchString)) {
                matchFound = true;
                break;
            }
        }

        if (!matchFound) {
            index = -1;
        }

        return index;
    }

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
