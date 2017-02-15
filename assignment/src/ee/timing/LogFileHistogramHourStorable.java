package ee.timing;

/**
 * Model class for hourly data to compose the histogram, used in CleanerService.java
 */

class LogFileHistogramHourStorable {

    private String timestamp;
    private int requestCount;

    public String getTimestamp() {
        return timestamp;
    }

    public int getRequestCount() {
        return requestCount;
    }

    LogFileHistogramHourStorable(String timestamp, int requestCount) {
        this.timestamp = timestamp;
        this.requestCount = requestCount;
    }

    boolean containsTimestamp(String string) {
        return timestamp.equals(string);
    }

    public int compareTimestamp() {
        return Integer.parseInt(timestamp);
    }


}
