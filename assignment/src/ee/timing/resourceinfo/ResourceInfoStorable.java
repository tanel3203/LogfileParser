package ee.timing.resourceinfo;

/**
 * Model class for storing resource data
 */

public class ResourceInfoStorable {

    private String resourceName;
    private double requestDuration;

    public String getResourceName() {
        return resourceName;
    }

    public double getRequestDuration() {
        return requestDuration;
    }

    public int getRequestDurationInteger() {
        return (int) requestDuration;
    }

    public ResourceInfoStorable(String resourceName, double requestDuration) {
        this.resourceName = resourceName;
        this.requestDuration = requestDuration;
    }

    boolean containsName(String string) {
        return resourceName.equals(string);
    }

}
