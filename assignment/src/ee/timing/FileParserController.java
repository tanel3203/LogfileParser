package ee.timing;

import ee.timing.histogram.HistogramService;
import ee.timing.resourceinfo.ResourceInfoService;
import java.io.File;
import java.util.*;

/**
 * singleton Controller class to
 *                  parse the log file
 *                  extract
 *                  aggregate and
 *                  present data from it
 */

public class FileParserController {

    // Singleton object instance
    private static FileParserController instance = new FileParserController();

    // Class objects
    private List<String> currentLine; // variable for scanning through logfile row by row

    // Class static variables
    private static int currentLineCounter = 0;

    // Constants
    private static final String FILE_COLUMN_DELIMITER = " ";

    // Constructor disables inward instantiation
    private FileParserController() {
    }

    // Exposes the only instance
    public static FileParserController getInstance() {
        return instance;
    }

    /**
     * Gets the logfile and prepares an object that can be used for further manipulation
     * @param pathToLogfile             String type logfield name/path
     * @throws Exception
     */
    //
    public void generateLogFileData(String pathToLogfile, String resourceCount) throws Exception {

        // Start services
        HistogramService histogramService = HistogramService.getInstance();
        ResourceInfoService resourceDurationService = ResourceInfoService.getInstance();

        // Get the file
        Scanner input = new Scanner(new File(pathToLogfile));

        // Get every line from file and with a pre-determined delimiter prepare resource and histogram objects
        while (input.hasNextLine()) {

            // Set counter to current line;
            currentLineCounter++;

            // Get data line and use delimiter to separate line content and add to currentLine variable
            currentLine = Arrays.asList(input.nextLine().split(FILE_COLUMN_DELIMITER));

            // Get the item count of current line
            int currentLineSize = currentLine.size();

            // Get columns from logfile line
            if (currentLineSize == 7 || currentLineSize == 8 || currentLineSize == 9) {

                // Prepare variables for logFileData and logFileHistogramData
                String currentLineTimestampHour = histogramService.cleanHourStringFromTimestamp(currentLine.get(1));
                String currentLineResourceName = resourceDurationService.cleanResourceName(currentLine.get(4));
                double currentLineRequestDuration = Double.parseDouble(currentLine.get(currentLine.size() - 1));

                // Build resource list and histogram
                resourceDurationService.buildResourceList(currentLineResourceName, currentLineRequestDuration);
                histogramService.buildHistogram(currentLineTimestampHour, currentLineCounter);

            } else {
                throw new IllegalArgumentException("Current line in logfile does not conform to predefined requirements on line " + currentLineCounter
                                        +   ". It is most likely due to the line having either more or less arguments than in specification, it has " + currentLineSize
                                        +   " arguments.");
            }
        }

        // Run services
        resourceDurationService.sortAndOutputRequestsAndAverages(Integer.parseInt(resourceCount));
        histogramService.sortAndOutputRequestInfoAndHistogram(currentLineCounter);

    }
}
