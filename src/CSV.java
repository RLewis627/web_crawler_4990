import java.io.*;
import com.opencsv.CSVWriter;
/**
 * Creates a CSV file for the websites visited which contains
 * a header of "URL" & "Number of out links."
 * <p>
 * This uses the OpenCSV third-party library jar file
 * We can add more data to the CSV if needed under the add
 * method.
 */
public class CSV {
    //Writes to CSV
    private CSVWriter csv_writer;

    //Constructor creates the file based on the lang
    public CSV(String lang) {
        createFile(lang);
    }

    // Create the file report with header
    private void createFile(String lang) {
        try {
            csv_writer = new CSVWriter(new FileWriter("CSVReports/" + lang + "_" + "report.csv"));
            csv_writer.writeNext(new String[]{"URL", "Number of out links"});
        } catch (IOException e) {
            System.err.println("File could not be created");
        }
    }

    // Print data to new line
    public void add(String url, int size) {
        csv_writer.writeNext(new String[] { url, Integer.toString(size) });
    }
    //Close CSV file
    public void close() {
        try {
            csv_writer.flush();
            csv_writer.close();
        } catch (IOException e) {
            System.err.println("Failed to close file!");
        }
    }
}