package ee.timing;

import ee.timing.histogram.HistogramService;
import ee.timing.histogram.LogFileHistogramHourStorable;
import ee.timing.resourceinfo.ResourceDurationService;
import ee.timing.resourceinfo.ResourceDurationStorable;

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

public class CleanerService {

    // Singleton object
    private static CleanerService instance = new CleanerService();

    // Class objects
    private ArrayList<ResourceDurationStorable> logFileData = new ArrayList<>(); // contains all initial logfile data
    private ArrayList<ResourceDurationStorable> logFileDataUnique = new ArrayList<>(); // contains all cleaned logfile data
    private ArrayList<LogFileHistogramHourStorable> logFileHistogramHourData = new ArrayList<>(); // contains hourly request data
    private List<String> currentLine; // variable for scanning through logfile row by row

    HistogramService histogramService = HistogramService.getInstance();
    ResourceDurationService resourceDurationService = ResourceDurationService.getInstance();

    // Class static variables
    private static int currentLineCounter = 0;

    // Constants
    private static final String FILE_COLUMN_DELIMITER = " ";

    // Constructor disables inward instantiation
    private CleanerService() {
    }

    // Exposes the only instance
    public static CleanerService getInstance() {
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
                String currentLineResourceName = resourceDurationService.cleanResourceName(currentLine.get(4));
                double currentLineRequestDuration = Double.parseDouble(currentLine.get(currentLine.size() - 1));

                // Additional variables for logFileHistogramData
                String currentLineTimestampHour = histogramService.cleanHourStringFromTimestamp(currentLine.get(1));

                // Add new object to logFileData
                logFileData.add(new ResourceDurationStorable(currentLineResourceName, currentLineRequestDuration));

                // Find if logFileHistogramData already has data for the given hour
                int hourExistsIndex = histogramService.getIndexOfExistingHour(logFileHistogramHourData, currentLineTimestampHour);

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
                // Add new record into logFileHistogramHourData
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

        // Collect resources in logFileData and average request duration by resource name
        Map<String, Double> logFileDataMap = logFileData.stream().collect(
                Collectors.groupingBy(ResourceDurationStorable::getResourceName, Collectors.averagingInt(ResourceDurationStorable::getRequestDurationInteger))
        );

        // Move unique data into a LogFileLineStorable type object
        logFileDataMap.forEach((name,value) -> logFileDataUnique.add(new ResourceDurationStorable(name, (double) Math.round(value))));

        // Sort logFileHistogramData by hour (00-23)
        Collections.sort(logFileDataUnique, new Comparator<ResourceDurationStorable>() {
            @Override
            public int compare(ResourceDurationStorable p1, ResourceDurationStorable p2) {
                return (int) p2.getRequestDuration() - (int) p1.getRequestDuration(); // Descending
            }
        });


        resourceDurationService.sortAndOutputRequestsAndAverages(logFileDataUnique, 10);

        histogramService.sortAndOutputRequestInfoAndHistogram(logFileHistogramHourData, currentLineCounter);

    }


}
