package ee.timing.tests;

import ee.timing.FileParserController;
import org.junit.Test;

/**
 * Test class for FileParserController
 */

public class FileParserServiceTest {

    @Test(expected = Exception.class)
    public void testBadPathShouldThrowException() throws Exception {

        FileParserController fileParserController = FileParserController.getInstance();

        // Testcases
        fileParserController.generateLogFileData("/badpath.log", "10");
    }



}
