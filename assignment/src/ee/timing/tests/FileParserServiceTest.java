package ee.timing.tests;

import ee.timing.FileParserService;
import org.junit.Test;

/**
 * Test class for FileParserService
 */

public class FileParserServiceTest {

    @Test(expected = Exception.class)
    public void testBadPathShouldThrowException() throws Exception {

        FileParserService fileParserService = FileParserService.getInstance();

        // Testcases
        fileParserService.generateLogFileData("/badpath.log", "10");
    }



}
