package ee.timing;

import com.sun.org.apache.xpath.internal.operations.Mult;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.averagingInt;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.junit.Assert.*;

/**
 * Test class for CleanerService.java
 */

public class CleanerServiceTest {

    @Test
    public void testResourceNameIsReturnedCorrect() {

        CleanerService cleanerService = CleanerService.getInstance();

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

        CleanerService cleanerService = CleanerService.getInstance();
        ArrayList<LogFileLineStorable> array = new ArrayList<>();

        // Populate arraylist
        array.add(new LogFileLineStorable("mainContent","38"));
        array.add(new LogFileLineStorable("api","38"));
        array.add(new LogFileLineStorable("getBills","38"));

        // Testcases
        assertEquals(2,cleanerService.getIndexOfExistingResource(array, "getBills"));
        assertEquals(0,cleanerService.getIndexOfExistingResource(array, "mainContent"));
        assertEquals(-1,cleanerService.getIndexOfExistingResource(array, "test"));

    }

    @Test
    public void testTimestampReturnsCorrectHourValue() {

        CleanerService cleanerService = CleanerService.getInstance();

        // Testcases
        assertEquals("00",cleanerService.getHourStringFromTimestamp("00:06:48,249"));
        assertEquals("23",cleanerService.getHourStringFromTimestamp("23:06:48,249"));
        //assertEquals("?",cleanerService.getHourStringFromTimestamp("29:06:48,249"));
        //assertEquals("?",cleanerService.getHourStringFromTimestamp("299:06:48,249"));
        //assertEquals("?",cleanerService.getHourStringFromTimestamp("-1"));
        //assertEquals("?",cleanerService.getHourStringFromTimestamp("22"));
    }

    @Test
    public void testJava8Streams() {

        ArrayList<Integer> newlist = new ArrayList<Integer>();
        newlist.add(0);
        newlist.add(2);
        newlist.add(4);
        newlist.add(6);
        newlist.add(8);

        double avg = newlist.stream().collect(averagingInt(Integer::intValue));
        //System.out.println(avg);
        assertTrue(avg == 4);
    }

    @Test
    public void testJava8StreamsOnMultiItem() {

        class MultiColumn {
            private String nameString;
            private int valueInt;

            public String getNameString() {
                return nameString;
            }

            public int getValueInt() {
                return valueInt;
            }

            public MultiColumn(String name, int value) {
                this.nameString = name;
                this.valueInt = value;
            }
        }

        ArrayList<MultiColumn> newlist = new ArrayList<>();
        newlist.add(new MultiColumn("name1", 0));
        newlist.add(new MultiColumn("name2", 2));
        newlist.add(new MultiColumn("name3", 4));
        newlist.add(new MultiColumn("name4", 6));
        newlist.add(new MultiColumn("name5", 8));

        double avg = newlist.stream().collect(averagingInt(MultiColumn::getValueInt));
        //System.out.println(avg);
        assertTrue(avg == 4);
    }

    @Test
    public void testJava8StreamsOnMultiItemConditionally() {

        class MultiColumn {
            private String nameString;
            private int valueInt;

            public String getNameString() {
                return nameString;
            }

            public int getValueInt() {
                return valueInt;
            }

            public MultiColumn() {

            }

            public MultiColumn(String name, int value) {
                this.nameString = name;
                this.valueInt = value;
            }
        }

        /*List<MultiColumn> newlist = Arrays.asList(
            new MultiColumn("name1", 0),
            new MultiColumn("name1", 2),
            new MultiColumn("name2", 2),
            new MultiColumn("name2", 6),
            new MultiColumn("name1", 4),
            new MultiColumn("name1", 6),
            new MultiColumn("name1", 8));*/

        ArrayList<MultiColumn> newlist = new ArrayList<>();
        newlist.add(new MultiColumn("name1", 0));
        newlist.add(new MultiColumn("name1", 2));
        newlist.add(new MultiColumn("name1", 4));
        newlist.add(new MultiColumn("name1", 6));
        newlist.add(new MultiColumn("name1", 8));
        newlist.add(new MultiColumn("name2", 4));
        newlist.add(new MultiColumn("name2", 8));

        Map<String, Double> avg = newlist.stream().collect(
                Collectors.groupingBy(MultiColumn::getNameString, Collectors.averagingInt(MultiColumn::getValueInt))
        );

        System.out.println("avg: " + avg);
        //assertTrue(avg == 4);
    }
}
