import java.io.*;
import java.util.*;

import com.github.pemistahl.lingua.api.*;
import static com.github.pemistahl.lingua.api.Language.*;
import java.util.List;
import com.detectlanguage.errors.APIError;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {

    private final int maxPagesToVisit;
    private CSV CSV_FILE;
    private int pagesNotVisited;
    private Set<String> pagesVisited = new HashSet<>();
    private List<String> pagesToVisit = new LinkedList<>();
    private List<String> links = new LinkedList<>();
    private static final String WEB_BROWSER = "Chrome-Chrome OS";
    private int fileIndex = 0;
    private String lang;

    public Crawler(String lang, int max) {
        this.lang = lang;
        this.CSV_FILE = new CSV(lang);
        this.pagesNotVisited = 0;
        this.maxPagesToVisit = max;
    }

    private String nextUrl() // Checks if URL has already been visited
    {
        String nextUrl;
        do {
            nextUrl = this.pagesToVisit.remove(0);
        } while (this.pagesVisited.contains(nextUrl));
        this.pagesVisited.add(nextUrl);
        return nextUrl;
    }

    public void search(String url) // Performs the main search function
    {
        int i = 1;
        while (this.pagesVisited.size() < maxPagesToVisit) {
            String currentUrl;
            if (this.pagesToVisit.isEmpty()) {
                currentUrl = url;
                this.pagesVisited.add(url);
            } else {
                currentUrl = this.nextUrl();
            }
            crawl(i, currentUrl);
            this.pagesToVisit.addAll(getLinks());
            i++;
        }
        if (pagesNotVisited > 0) {
            System.out.println("The crawler was unable to visit " + pagesNotVisited +
                    " link(s) due to the language detected or not having a valid html file");
        }
        CSV_FILE.close();
    }

    public void crawl(int siteNumber, String url) // Makes an HTTP request for a given url
    {
        try {
            Connection connection = Jsoup.connect(url).userAgent(WEB_BROWSER);
            Document htmlDocument = connection.get();
            String htmlDocString = connection.get().html();
            String htmlString = connection.get().text();
            if (connection.response().statusCode() == 200) {
                System.out.println("\n(" + siteNumber + ") Visiting " + url);
            }
            if (!connection.response().contentType().contains("text/html")) {
                System.out.println("Not a valid HTML file");
                pagesNotVisited++;
            }

            Elements linksOnPage = htmlDocument.select("a[href]");

            // Check the language of the webpage - if it's in our language, download it.
            try {
                System.out.println("Checking site language...");
                Language pageLanguageDetect = checkLanguage(htmlString);
                Language input = null;

                switch (lang) {
                    case "en":
                        input = ENGLISH;
                        break;
                    case "es":
                        input = SPANISH;
                        break;
                    case "ru":
                        input = RUSSIAN;
                        break;
                }

                if (pageLanguageDetect == input) {
                    System.out.println("SUCCESS! The page is in: " + pageLanguageDetect);
                    System.out.println("Found " + linksOnPage.size() + " links");
                    CSV_FILE.add(url, linksOnPage.size());
                    downloadFile(htmlDocString);
                    createMasterFile(htmlString);
                } else {
                    System.out.println("ERROR! The page is in: " + pageLanguageDetect);
                    pagesNotVisited++;
                }
            } catch (APIError apiError) {
                apiError.printStackTrace();
            }

            for (Element link : linksOnPage) {
                this.links.add(link.absUrl("href"));
            }
        } catch (IOException e) {
            System.out.println("Error in out HTTP request " + e);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: Invalid URL");
        }
    }

    // Check the web page language - Done
    public Language checkLanguage(String url) throws APIError {
        Language detectedLanguage = null;

        try {
            final LanguageDetector detector = LanguageDetectorBuilder.fromLanguages(ENGLISH, FRENCH, GERMAN, RUSSIAN, SPANISH).build();
            detectedLanguage = detector.detectLanguageOf(url);
        } catch (IllegalArgumentException e) {
            System.out.println("Not a valid file");
        } finally {
            return detectedLanguage;
        }
    }

    public void createMasterFile(String siteString) throws FileNotFoundException {
        System.out.println("Adding to master file!");
        String fileName = "Master/" + lang + "_" + "masterFile.txt";
        File f = new File(fileName);

        PrintWriter out;
        if ( f.exists() && !f.isDirectory() ) {
            out = new PrintWriter(new FileOutputStream(new File(fileName), true));
        }
        else {
            out = new PrintWriter(fileName);
        }
        out.append("\n\n");
        out.append(siteString);
        out.close();
    }

    // Write to a file - Done
    public void downloadFile(String file) throws FileNotFoundException {
        try (PrintWriter out = new PrintWriter("repository/" + lang + "/filename" + fileIndex + ".html")) {
            out.println(file);
            fileIndex++;
        }
    }

    public List<String> getLinks()// Returns a list of all the URLs on the page
    {
        return this.links;
    }
}