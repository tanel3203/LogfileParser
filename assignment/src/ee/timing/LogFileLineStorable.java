package ee.timing;

/**
 * Model class for storing resource data, used in CleanerService.java
 */

class LogFileLineStorable {

    private String resourceName;
    private String requestDuration;

    public String getResourceName() {
        return resourceName;
    }

    public String getRequestDuration() {
        return requestDuration;
    }

    public int getRequestDurationCast() {
        return Integer.parseInt(requestDuration);
    }

    LogFileLineStorable(String resourceName, String requestDuration) {
        this.resourceName = resourceName;
        this.requestDuration = requestDuration;
    }

    boolean containsName(String string) {
        return resourceName.equals(string);
    }

}
