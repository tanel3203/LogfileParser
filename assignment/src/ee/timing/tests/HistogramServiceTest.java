package ee.timing.tests;

import ee.timing.histogram.HistogramService;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for HistogramService
 */

public class HistogramServiceTest {

    @Test
    public void testTimestampReturnsCorrectHourValue() {

        HistogramService histogramService = HistogramService.getInstance();

        // Testcases
        assertEquals("00",histogramService.cleanHourStringFromTimestamp("00:06:48,249"));
        assertEquals("23",histogramService.cleanHourStringFromTimestamp("23:06:48,249"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncompatibleHourShouldThrowException() throws IllegalArgumentException {

        HistogramService histogramService = HistogramService.getInstance();

        // Testcases
        histogramService.cleanHourStringFromTimestamp("29:06:48,249");
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void testNegativeValueShouldThrowException() throws StringIndexOutOfBoundsException {

        HistogramService histogramService = HistogramService.getInstance();

        // Testcases
        histogramService.cleanHourStringFromTimestamp("-1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLengthyValueShouldThrowException() throws IllegalArgumentException {

        HistogramService histogramService = HistogramService.getInstance();

        // Testcases
        histogramService.cleanHourStringFromTimestamp("299:06:48,249");
    }

}
