import java.io.*;
import java.util.*;
import java.net.*;

import com.detectlanguage.errors.APIError;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.detectlanguage.DetectLanguage;

public class Crawler {

    private CSV CSV_FILE = new CSV();
    private Set<String> pagesVisited = new HashSet<>();
    private List<String> pagesToVisit = new LinkedList<>();
    private List<String> links = new LinkedList<>();
    private static final String USER_AGENT = "Chrome-Chrome OS";
    private int fileIndex = 0;
    private String lang;

    public Crawler(String lang) {
        this.lang = lang;
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
        while (this.pagesVisited.size() < 50) {
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
        CSV_FILE.close();
    }

    public boolean crawl(int siteNumber, String url) // Makes an HTTP request for a given url
    {
        try {
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            String htmlDocString = connection.get().html();
            String htmlString = connection.get().text();
            if (connection.response().statusCode() == 200) {
                System.out.println("\n("+siteNumber+") Visiting " + url);
            }
            if (!connection.response().contentType().contains("text/html")) {
                System.out.println("Not a valid HTML file");
                return false;
            }

            Elements linksOnPage = htmlDocument.select("a[href]");

            // Check the language of the webpage - if it's in our language, download it.
            try {
                String pageLanguageDetect = checkLanguage(htmlString);

                if (pageLanguageDetect.contains(lang)) {
                    System.out.println("SUCCESS! The page is in: " + pageLanguageDetect);
                    System.out.println("Found " + linksOnPage.size() + " links");
                    CSV_FILE.add(url, linksOnPage.size());
                    downloadFile(htmlDocString);
                    createMasterFile(htmlString);
                } else {
                    System.out.println("ERROR! The page is in: " + pageLanguageDetect);
                }
            } catch (APIError apiError) {
                apiError.printStackTrace();
            }

            for (Element link : linksOnPage) {
                this.links.add(link.absUrl("href"));
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error in out HTTP request " + e);
            return false;
        }
    }

    // Check the web page language - Done
    public String checkLanguage(String url) throws APIError {
        System.out.println("Checking site language...");
        DetectLanguage.apiKey = "9883de6242b3d4347d2cb90e1e79c93f";
        DetectLanguage.ssl = true;
        String language = DetectLanguage.simpleDetect(url);
        return language;
    }

    public void createMasterFile(String siteString) throws FileNotFoundException {
        System.out.println("Adding to master file!");
        String fileName = "masterFile.txt";
        File f = new File(fileName);

        PrintWriter out = null;
        if ( f.exists() && !f.isDirectory() ) {
            out = new PrintWriter(new FileOutputStream(new File(fileName), true));
        }
        else {
            out = new PrintWriter(fileName);
        }
        out.append("\n");
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

    public static String encode(String url) {
        try {
            String encodeURL = URLEncoder.encode(url, "UTF-8");
            return encodeURL;
        } catch (UnsupportedEncodingException e) {
            return "Issue while encoding" + e.getMessage();
        }
    }

    public List<String> getLinks()// Returns a list of all the URLs on the page
    {
        return this.links;
    }
}