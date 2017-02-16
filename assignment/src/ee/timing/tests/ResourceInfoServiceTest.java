package ee.timing.tests;

import ee.timing.resourceinfo.ResourceInfoService;
import ee.timing.resourceinfo.ResourceInfoStorable;
import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.*;

/**
 * Test class for ResourceDurationService
 */

public class ResourceInfoServiceTest {

    @Test
    public void testResourceNameIsReturnedCorrect() {

        ResourceInfoService resourceInfoService = ResourceInfoService.getInstance();

        // Testcases
        assertEquals("index",resourceInfoService.cleanResourceName("/index.jsp"));
        assertEquals("main",resourceInfoService.cleanResourceName("/main.do"));
        assertEquals("api",resourceInfoService.cleanResourceName("/api"));
        assertEquals("api",resourceInfoService.cleanResourceName("api."));
        assertEquals("api",resourceInfoService.cleanResourceName("api?properties=upgradability"));
        assertEquals("getBills",resourceInfoService.cleanResourceName("getBills"));
        assertEquals("mainContent",resourceInfoService.cleanResourceName("/mainContent.do?action=SUBSCRIPTION&msisdn=300008321210&contentId=main_subscription"));
        assertEquals("",resourceInfoService.cleanResourceName(""));

    }

    @Test
    public void testResourceIndexReturnsCorrectValue() {

        ResourceInfoService resourceInfoService = ResourceInfoService.getInstance();
        ArrayList<ResourceInfoStorable> array = new ArrayList<>();

        // Populate arraylist
        array.add(new ResourceInfoStorable("mainContent",38));
        array.add(new ResourceInfoStorable("api",38));
        array.add(new ResourceInfoStorable("getBills",38));

        // Testcases
        assertEquals(2,resourceInfoService.getIndexOfExistingResource(array, "getBills"));
        assertEquals(0,resourceInfoService.getIndexOfExistingResource(array, "mainContent"));
        assertEquals(-1,resourceInfoService.getIndexOfExistingResource(array, "test"));

    }

}
