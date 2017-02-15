package ee.timing;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        long startTime = System.currentTimeMillis();
        Cleaner cleaner = new Cleaner();


        cleaner.generateLogFileData();
        //cleaner.getResourceName("/index.jsp");

        long endTime = System.currentTimeMillis();
        System.out.println("Program ran for " + (endTime - startTime) + " milliseconds");

    }
}
