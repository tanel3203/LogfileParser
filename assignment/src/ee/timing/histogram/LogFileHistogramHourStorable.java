package ee.timing.histogram;

/**
 * Model class for hourly data to compose the histogram
 */

public class LogFileHistogramHourStorable {

    private String timestamp;
    private int requestCount;

    public String getTimestamp() {
        return timestamp;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public LogFileHistogramHourStorable(String timestamp, int requestCount) {
        this.timestamp = timestamp;
        this.requestCount = requestCount;
    }

    public boolean containsTimestamp(String string) {
        return timestamp.equals(string);
    }

    public int compareTimestamp() {
        return Integer.parseInt(timestamp);
    }


}
