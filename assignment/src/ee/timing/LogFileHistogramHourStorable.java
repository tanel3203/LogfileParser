package ee.timing;

import java.util.ArrayList;

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


}
