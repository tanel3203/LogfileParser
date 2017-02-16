package ee.timing.resourceinfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *  singleton Service for creating the resource list with average durations
 */
public class ResourceDurationService {

    // Constants
    private static final String RESOURCE_NAME_PREFIX_REGEX = "(^/)";
    private static final String RESOURCE_NAME_SUFFIX_REGEX = "([.?])";

    // Singleton object instance
    private static ResourceDurationService instance = new ResourceDurationService();

    // Class objects
    private ArrayList<ResourceDurationStorable> logFileData = new ArrayList<>(); // contains all initial logfile data
    private ArrayList<ResourceDurationStorable> logFileDataUnique = new ArrayList<>(); // contains all cleaned logfile data

    // Constructor disables inward instantiation
    private ResourceDurationService() {
    }

    // Exposes the only instance
    public static ResourceDurationService getInstance() {
        return instance;
    }

    /**
     *
     */
    public void buildResourceList(String currentLineResourceName, double currentLineRequestDuration) {

        // Add new object to logFileData
        logFileData.add(new ResourceDurationStorable(currentLineResourceName, currentLineRequestDuration));

    }

    /**
     * Finds the index of existing resource name for logFileData
     * @param array                 array the resource name is searched from
     * @param searchString          resource name that is looked for
     * @return                      returns index of existing resource name
     *                              if none exists, returns -1
     */
    public int getIndexOfExistingResource(ArrayList<ResourceDurationStorable> array, String searchString) {

        int index = -1;
        boolean matchFound = false;

        // Iterate until match is found
        for (ResourceDurationStorable item : array) {
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
     * Cleans resource name string if necessary (e.g. it is a full URI+query string)
     * @param resourceString            takes a string type variable and either returns it
     *                                  if it has no additional information attached to it
     *                                  or cleans that information and returns a clean
     *                                  resource name
     *
     * @return                          returns String type resource name
     */

    public String cleanResourceName(String resourceString) {

        // Find if given string is already a resource name, then return the initial string
        if (resourceString.matches(RESOURCE_NAME_PREFIX_REGEX)
                && resourceString.matches(RESOURCE_NAME_SUFFIX_REGEX)) {
            return resourceString;
        }

        // Find and return the resource name from the resource string eliminating unnecessary URI and query string content
        return resourceString
                .replaceAll(RESOURCE_NAME_PREFIX_REGEX, "")
                .split(RESOURCE_NAME_SUFFIX_REGEX)[0];
    }

    /**
     * Sorts and outputs to commandline the highest average time-duration requests
     * @param resourceDisplayCount          int type resource display count - how many the user wants to see
     */
    public void sortAndOutputRequestsAndAverages(int resourceDisplayCount) {

        // Collect resources in logFileData and average request duration by resource name
        Map<String, Double> logFileDataMap = logFileData.stream().collect(
                Collectors.groupingBy(
                        ResourceDurationStorable::getResourceName,
                        Collectors.averagingInt(ResourceDurationStorable::getRequestDurationInteger)
                )
        );

        // Move unique data into a LogFileLineStorable type object
        logFileDataMap.forEach((name,value) ->
                logFileDataUnique.add(new ResourceDurationStorable(name, (double) Math.round(value))));

        // Sort logFileHistogramData by hour (00-23)
        logFileDataUnique.sort((d1, d2) -> {
            return d2.getRequestDurationInteger() - d1.getRequestDurationInteger(); // Descending
        });

        // Output top n resources by request duration
        System.out.println("--------------------------------------------------------------");
        System.out.format("\n%50s\n\n", "TOP REQUESTS BY AVG TIME");
        System.out.println("--------------------------------------------------------------");
        int counter = 0;
        for (ResourceDurationStorable item : logFileDataUnique) {
            if (counter == resourceDisplayCount) { break; }
            System.out.format("%40s%15d\n", item.getResourceName(), (int) item.getRequestDuration());
            counter++;
        }
        System.out.println("--------------------------------------------------------------");
    }
}
