package ee.timing;

import sun.rmi.runtime.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

class Cleaner {

    private ArrayList<LogFileLineStorable> logFileData = new ArrayList<>();
    private List<String> currentLine;
    private int currentLineCounter = 0;
    private static String FILE_COLUMN_DELIMITER = " ";
    private static String RESOURCE_NAME_REGEX = "(^/)";

    Cleaner() {
    }

    // Get the logfile and prepare an object that can be used for further manipulation
    void generateLogFileData() throws FileNotFoundException {

        // Get the file
        Scanner input = new Scanner(new File("resources/timing.log"));

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
                String currentLineTimestamp = currentLine.get(1);
                String currentLineResourceName = getResourceName(currentLine.get(4));
                String currentLineRequestDuration = currentLine.get(currentLine.size() - 1);

                // Find if logFileData already has a record of current resource
                int resourceExistsIndex = getIndexOfExistingResource(logFileData, currentLineResourceName);

                System.out.println(currentLineTimestamp + " " + currentLineResourceName + " " + currentLineRequestDuration);







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

        System.out.println("---------------");
        for (LogFileLineStorable item : logFileData) {
            System.out.println(item.getTimestamp() + " " + item.getResourceName() + " " + item.getRequestDuration());
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
}
