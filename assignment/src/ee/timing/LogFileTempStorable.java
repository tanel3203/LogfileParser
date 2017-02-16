package ee.timing;

/**
 * Model class for storing resource data, used in CleanerService.java
 */

class LogFileTempStorable {

    private String resourceName;
    private double requestDuration;

    public String getResourceName() {
        return resourceName;
    }

    public double getRequestDuration() {
        return requestDuration;
    }

    LogFileTempStorable(String resourceName, double requestDuration) {
        this.resourceName = resourceName;
        this.requestDuration = requestDuration;
    }

}
