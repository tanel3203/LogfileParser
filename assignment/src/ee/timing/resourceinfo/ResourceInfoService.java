package ee.timing.resourceinfo;

import java.util.ArrayList;

/**
 *  singleton Service for creating the resource list with average durations
 */
public class ResourceInfoService {

    // Constants
    private static final String RESOURCE_NAME_PREFIX_REGEX = "(^/)";
    private static final String RESOURCE_NAME_SUFFIX_REGEX = "([.?])";

    // Singleton object instance
    private static ResourceInfoService instance = new ResourceInfoService();

    // Class objects
    private ArrayList<ResourceInfoStorable> logFileData = new ArrayList<>(); // contains all initial logfile data

    // Constructor disables inward instantiation
    private ResourceInfoService() {
    }

    // Exposes the only instance
    public static ResourceInfoService getInstance() {
        return instance;
    }

    /**
     *  Builds the request log object
     * @param currentLineResourceName       String type current line resource name
     * @param currentLineRequestDuration    double type current line request duration - how long the request took
     */
    public void buildResourceList(String currentLineResourceName, double currentLineRequestDuration) {

        // Add new object to logFileData
        logFileData.add(new ResourceInfoStorable(currentLineResourceName, currentLineRequestDuration));

    }

    /**
     * Finds the index of existing resource name for logFileData
     * @param array                 array the resource name is searched from
     * @param searchString          resource name that is looked for
     * @return                      returns index of existing resource name
     *                              if none exists, returns -1
     */
    public int getIndexOfExistingResource(ArrayList<ResourceInfoStorable> array, String searchString) {

        int index = -1;
        boolean matchFound = false;

        // Iterate until match is found
        for (ResourceInfoStorable item : array) {
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
     * Starts and runs view that sorts and outputs to commandline the highest average time-duration requests
     * @param resourceDisplayCount          int type resource display count - how many the user wants to see
     */
    public void sortAndOutputRequestsAndAverages(int resourceDisplayCount) {

        // Start view instance
        ResourceInfoView resourceInfoView = ResourceInfoView.getInstance();

        // Run view instance
        resourceInfoView.sortAndOutputRequestsAndAverages(logFileData, resourceDisplayCount);
    }
}
