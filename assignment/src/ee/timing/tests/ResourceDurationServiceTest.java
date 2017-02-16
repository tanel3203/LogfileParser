package ee.timing.tests;

import ee.timing.resourceinfo.ResourceDurationService;
import ee.timing.resourceinfo.ResourceDurationStorable;
import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.*;

/**
 * Test class for ResourceDurationService
 */

public class ResourceDurationServiceTest {

    @Test
    public void testResourceNameIsReturnedCorrect() {

        ResourceDurationService resourceDurationService = ResourceDurationService.getInstance();

        // Testcases
        assertEquals("index",resourceDurationService.cleanResourceName("/index.jsp"));
        assertEquals("main",resourceDurationService.cleanResourceName("/main.do"));
        assertEquals("api",resourceDurationService.cleanResourceName("/api"));
        assertEquals("api",resourceDurationService.cleanResourceName("api."));
        assertEquals("api",resourceDurationService.cleanResourceName("api?properties=upgradability"));
        assertEquals("getBills",resourceDurationService.cleanResourceName("getBills"));
        assertEquals("mainContent",resourceDurationService.cleanResourceName("/mainContent.do?action=SUBSCRIPTION&msisdn=300008321210&contentId=main_subscription"));
        assertEquals("",resourceDurationService.cleanResourceName(""));

    }

    @Test
    public void testResourceIndexReturnsCorrectValue() {

        ResourceDurationService resourceDurationService = ResourceDurationService.getInstance();
        ArrayList<ResourceDurationStorable> array = new ArrayList<>();

        // Populate arraylist
        array.add(new ResourceDurationStorable("mainContent",38));
        array.add(new ResourceDurationStorable("api",38));
        array.add(new ResourceDurationStorable("getBills",38));

        // Testcases
        assertEquals(2,resourceDurationService.getIndexOfExistingResource(array, "getBills"));
        assertEquals(0,resourceDurationService.getIndexOfExistingResource(array, "mainContent"));
        assertEquals(-1,resourceDurationService.getIndexOfExistingResource(array, "test"));

    }

}
