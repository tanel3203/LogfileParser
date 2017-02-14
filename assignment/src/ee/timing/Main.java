package ee.timing;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        Cleaner cleaner = new Cleaner();


        cleaner.generateLogFileData();
        //cleaner.getResourceName("/index.jsp");

    }
}
