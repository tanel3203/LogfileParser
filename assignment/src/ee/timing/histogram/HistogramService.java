package ee.timing.histogram;

import java.util.ArrayList;
import java.util.Comparator;

/**
 *  singleton Service for creating the histogram
 */
public class HistogramService {

    // Singleton object instance
    private static HistogramService instance = new HistogramService();

    // Class objects
    private ArrayList<LogFileHistogramHourStorable> logFileHistogramHourData = new ArrayList<>(); // contains hourly request data

    // Constructor disables inward instantiation
    private HistogramService() {
    }

    // Exposes the only instance
    public static HistogramService getInstance() {
        return instance;
    }

    /**
     *  Builds the hourly histogram object with request count
     * @param currentLineTimestampHour         String type hour identifier from logfile line
     * @param currentLineCounter               int type line counter, shows what line parser is currently on
     * @throws Exception
     */
    public void buildHistogram(String currentLineTimestampHour, int currentLineCounter) throws Exception {

        // Find if logFileHistogramData already has data for the given hour
        int hourExistsIndex = getIndexOfExistingHour(currentLineTimestampHour);

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
    }

    /**
     * Finds the index of existing hour object for logFileHistogramHourData
     * @param searchString              hour object identifier that is looked for
     * @return                          returns index of existing object
     *                                  if none exists, returns -1
     */
    public int getIndexOfExistingHour(String searchString) {

        int index = -1;
        boolean matchFound = false;

        // Iterate until match is found
        for (LogFileHistogramHourStorable item : logFileHistogramHourData) {
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
    public String cleanHourStringFromTimestamp(String timestamp) throws StringIndexOutOfBoundsException, IllegalArgumentException {

        String hourValue = timestamp.substring(0, timestamp.indexOf(":"));

        if ((Integer.parseInt(hourValue) > 23) || Integer.parseInt(hourValue) < 0) {
            throw new IllegalArgumentException("Value '" + timestamp
                    + "' does not include a valid hour value. 24-hour clock is used (00-23).");
        }
        return hourValue;
    }

    /**
     * Sorts and outputs to commandline the request info and histogram table
     * @param totalRequests         int type total rows (requests) in parsed file
     */
    public void sortAndOutputRequestInfoAndHistogram(int totalRequests) {

        // Sort logFileHistogramData by hour (00-23)
        logFileHistogramHourData.sort(Comparator.comparingInt(LogFileHistogramHourStorable::compareTimestamp));

        // Output hourly information with histogram
        System.out.println("--------------------------------------------------------------");
        System.out.format("\n%50s\n\n", "HOURLY REQUEST DISTRIBUTION");
        System.out.println("--------------------------------------------------------------");
        System.out.format("\n%15s%15s%15s%3s%15s\n", "Hour", "Request count", "Total requests", "   ", " Requests (%)");
        System.out.println("--------------------------------------------------------------");
        for (LogFileHistogramHourStorable item : logFileHistogramHourData) {
            System.out.format("%15s%15d%15d%3s", item.getTimestamp(), item.getRequestCount(), totalRequests, " | ");
            double percentOfTotal = 10 * (double) item.getRequestCount() / totalRequests;
            for (int i = 0; i < percentOfTotal; i++) {
                System.out.print("#");
            }
            System.out.println("\n--------------------------------------------------------------");

        }
    }
}
