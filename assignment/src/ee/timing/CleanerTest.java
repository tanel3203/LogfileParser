package ee.timing;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class CleanerTest {

    @Test
    public void testResourceNameIsReturnedCorrect() {

        Cleaner cleaner = new Cleaner();

        // Testcases
        assertEquals("index",cleaner.getResourceName("/index.jsp"));
        assertEquals("main",cleaner.getResourceName("/main.do"));
        assertEquals("api",cleaner.getResourceName("/api"));
        assertEquals("api",cleaner.getResourceName("api."));
        assertEquals("api",cleaner.getResourceName("api?properties=upgradability"));
        assertEquals("getBills",cleaner.getResourceName("getBills"));
        assertEquals("mainContent",cleaner.getResourceName("/mainContent.do?action=SUBSCRIPTION&msisdn=300008321210&contentId=main_subscription"));
        assertEquals("",cleaner.getResourceName(""));

    }

    @Test
    public void testResourceIndexReturnsCorrectValue() {

        Cleaner cleaner = new Cleaner();
        ArrayList<LogFileLineStorable> array = new ArrayList<>();

        // Populate arraylist
        array.add(new LogFileLineStorable("00:00:03,388","mainContent","38"));
        array.add(new LogFileLineStorable("00:00:03,388","api","38"));
        array.add(new LogFileLineStorable("00:00:03,388","getBills","38"));

        // Testcases
        assertEquals(2,cleaner.getIndexOfExistingResource(array, "getBills"));
        assertEquals(0,cleaner.getIndexOfExistingResource(array, "mainContent"));
        assertEquals(-1,cleaner.getIndexOfExistingResource(array, "test"));

    }
}
