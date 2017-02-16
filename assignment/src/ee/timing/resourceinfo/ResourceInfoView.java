package ee.timing.resourceinfo;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *  singleton View for displaying resource information to user on command line
 */
public class ResourceInfoView {

    // Singleton object instance
    private static ResourceInfoView instance = new ResourceInfoView();

    // Class objects
    private ArrayList<ResourceInfoStorable> logFileDataUnique = new ArrayList<>(); // contains all cleaned logfile data

    // Constructor disables inward instantiation
    private ResourceInfoView() {
    }

    // Exposes the only instance
    public static ResourceInfoView getInstance() {
        return instance;
    }

    /**
     * Sorts and outputs to commandline the highest average time-duration requests
     * @param resourceDisplayCount          int type resource display count - how many the user wants to see
     */
    public void sortAndOutputRequestsAndAverages(ArrayList<ResourceInfoStorable> logFileData, int resourceDisplayCount) {

        // Collect resources in logFileData and average request duration by resource name
        Map<String, Double> logFileDataMap = logFileData.stream().collect(
                Collectors.groupingBy(
                        ResourceInfoStorable::getResourceName,
                        Collectors.averagingInt(ResourceInfoStorable::getRequestDurationInteger)
                )
        );

        // Move unique data into a LogFileLineStorable type object
        logFileDataMap.forEach((name,value) ->
                logFileDataUnique.add(new ResourceInfoStorable(name, (double) Math.round(value))));

        // Sort logFileHistogramData by hour (00-23)
        logFileDataUnique.sort((d1, d2) -> {
            return d2.getRequestDurationInteger() - d1.getRequestDurationInteger(); // Descending
        });

        // Output top n resources by request duration
        System.out.println("--------------------------------------------------------------");
        System.out.format("\n%50s\n\n", "TOP REQUESTS BY AVG TIME");
        System.out.println("--------------------------------------------------------------");
        int counter = 0;
        for (ResourceInfoStorable item : logFileDataUnique) {
            if (counter == resourceDisplayCount) { break; }
            System.out.format("%40s%15d\n", item.getResourceName(), (int) item.getRequestDuration());
            counter++;
        }
        System.out.println("--------------------------------------------------------------");
    }
}
