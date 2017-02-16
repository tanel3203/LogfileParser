package ee.timing.histogram;

import java.util.ArrayList;
import java.util.Comparator;

/**
 *  singleton View for displaying the histogram to the command line for the user
 */
public class HistogramView {

    // Singleton object instance
    private static HistogramView instance = new HistogramView();

    // Constructor disables inward instantiation
    private HistogramView() {
    }

    // Exposes the only instance
    public static HistogramView getInstance() {
        return instance;
    }

    /**
     * Sorts and outputs to commandline the request info and histogram table
     * @param totalRequests         int type total rows (requests) in parsed file
     */
    public void sortAndOutputRequestInfoAndHistogram(ArrayList<HistogramStorable> logFileHistogramHourData, int totalRequests) {

        // Sort logFileHistogramData by hour (00-23)
        logFileHistogramHourData.sort(Comparator.comparingInt(HistogramStorable::compareTimestamp));

        // Output hourly information with histogram
        System.out.println("--------------------------------------------------------------");
        System.out.format("\n%50s\n\n", "HOURLY REQUEST DISTRIBUTION");
        System.out.println("--------------------------------------------------------------");
        System.out.format("\n%15s%15s%15s%3s%15s\n", "Hour", "Request count", "Total requests", "   ", " Requests (%)");
        System.out.println("--------------------------------------------------------------");
        for (HistogramStorable item : logFileHistogramHourData) {
            System.out.format("%15s%15d%15d%3s", item.getTimestamp(), item.getRequestCount(), totalRequests, " | ");
            double percentOfTotal = 10 * (double) item.getRequestCount() / totalRequests;
            for (int i = 0; i < percentOfTotal; i++) {
                System.out.print("#");
            }
            System.out.println("\n--------------------------------------------------------------");

        }
    }
}
