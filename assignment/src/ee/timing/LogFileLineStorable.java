package ee.timing;

class LogFileLineStorable {

    private String timestamp;
    private String resourceName;
    private String requestDuration;

    public String getTimestamp() {
        return timestamp;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getRequestDuration() {
        return requestDuration;
    }

    public int getRequestDurationCast() {
        return Integer.parseInt(requestDuration);
    }

    LogFileLineStorable(String timestamp, String resourceName, String requestDuration) {
        this.timestamp = timestamp;
        this.resourceName = resourceName;
        this.requestDuration = requestDuration;
    }

    boolean containsName(String string) {
        return resourceName.equals(string);
    }

}
