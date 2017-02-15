package ee.timing;

import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.*;

/**
 * Test class for CleanerService.java
 */

public class CleanerServiceTest {

    @Test
    public void testResourceNameIsReturnedCorrect() {

        CleanerService cleanerService = new CleanerService();

        // Testcases
        assertEquals("index",cleanerService.getResourceName("/index.jsp"));
        assertEquals("main",cleanerService.getResourceName("/main.do"));
        assertEquals("api",cleanerService.getResourceName("/api"));
        assertEquals("api",cleanerService.getResourceName("api."));
        assertEquals("api",cleanerService.getResourceName("api?properties=upgradability"));
        assertEquals("getBills",cleanerService.getResourceName("getBills"));
        assertEquals("mainContent",cleanerService.getResourceName("/mainContent.do?action=SUBSCRIPTION&msisdn=300008321210&contentId=main_subscription"));
        assertEquals("",cleanerService.getResourceName(""));

    }

    @Test
    public void testResourceIndexReturnsCorrectValue() {

        CleanerService cleanerService = new CleanerService();
        ArrayList<LogFileLineStorable> array = new ArrayList<>();

        // Populate arraylist
        array.add(new LogFileLineStorable("00:00:03,388","mainContent","38"));
        array.add(new LogFileLineStorable("00:00:03,388","api","38"));
        array.add(new LogFileLineStorable("00:00:03,388","getBills","38"));

        // Testcases
        assertEquals(2,cleanerService.getIndexOfExistingResource(array, "getBills"));
        assertEquals(0,cleanerService.getIndexOfExistingResource(array, "mainContent"));
        assertEquals(-1,cleanerService.getIndexOfExistingResource(array, "test"));

    }

    @Test
    public void testTimestampReturnsCorrectHourValue() {

        CleanerService cleanerService = new CleanerService();

        // Testcases
        assertEquals("00",cleanerService.getHourStringFromTimestamp("00:06:48,249"));
        assertEquals("23",cleanerService.getHourStringFromTimestamp("23:06:48,249"));
        //assertEquals("?",cleanerService.getHourStringFromTimestamp("29:06:48,249"));
        //assertEquals("?",cleanerService.getHourStringFromTimestamp("299:06:48,249"));
        //assertEquals("?",cleanerService.getHourStringFromTimestamp("-1"));
        //assertEquals("?",cleanerService.getHourStringFromTimestamp("22"));
    }
}
