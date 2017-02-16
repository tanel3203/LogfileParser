package ee.timing.histogram;

import ee.timing.histogram.LogFileHistogramHourStorable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *  singleton Service for creating the histogram
 */
public class HistogramService {

    // Singleton object
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
     *
     * @throws Exception
     */
    void makeHistogram() throws Exception {

    }

    /**
     * Finds the index of existing hour object for logFileHistogramHourData
     * @param array                     array the hour object is searched from
     * @param searchString              hour object identifier that is looked for
     * @return                          returns index of existing object
     *                                  if none exists, returns -1
     */
    public int getIndexOfExistingHour(ArrayList<LogFileHistogramHourStorable> array, String searchString) {

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
    public String cleanHourStringFromTimestamp(String timestamp) {
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

    public void sortAndOutputRequestInfoAndHistogram(ArrayList<LogFileHistogramHourStorable> logFileHistogramHourData, int totalRequests) {

        // Sort logFileHistogramData by hour (00-23)
        Collections.sort(logFileHistogramHourData,
                Comparator.comparingInt(LogFileHistogramHourStorable::compareTimestamp)
        );

        // Output hourly information with histogram
        System.out.format("%15s%15s%15s%3s%15s", "Hour", "Request count", "Total requests", "   ", " Requests (%)");
        System.out.println("");
        System.out.println("--------------------------------------------------------------");
        for (LogFileHistogramHourStorable item : logFileHistogramHourData) {
            System.out.format("%15s%15d%15d%3s", item.getTimestamp(), item.getRequestCount(), totalRequests, " | ");
            double percentOfTotal = 10 * (double) item.getRequestCount() / totalRequests;

            for (int i = 0; i < percentOfTotal; i++) {
                System.out.print("#");
            }
            System.out.println("");
            System.out.println("--------------------------------------------------------------");

        }
    }
}
