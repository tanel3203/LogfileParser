package ee.timing.resourceinfo;

import ee.timing.histogram.LogFileHistogramHourStorable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *  singleton Service for creating the resource list with average durations
 */
public class ResourceDurationService {

    // Constants
    private static final String RESOURCE_NAME_PREFIX_REGEX = "(^/)";
    private static final String RESOURCE_NAME_SUFFIX_REGEX = "([.?])";

    // Singleton object
    private static ResourceDurationService instance = new ResourceDurationService();

    // Cöass objects
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
    void makeResourceList() {

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
     *
     */
    public void sortAndOutputRequestsAndAverages(ArrayList<ResourceDurationStorable> logFileDataUnique, int resourceDisplayCount) {

        // Sort logFileHistogramData by hour (00-23)
        Collections.sort(logFileDataUnique,
                Comparator.comparingInt(ResourceDurationStorable::getRequestDurationInteger)
        );

        // Output top n resources by request duration
        int counter = 0;
        for (ResourceDurationStorable item : logFileDataUnique) {

            if (counter == resourceDisplayCount) {
                break;
            }

            System.out.format("%40s%15d", item.getResourceName(), (int) item.getRequestDuration());
            System.out.println();
            counter++;
        }

    }
}
