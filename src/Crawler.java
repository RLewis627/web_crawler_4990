import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
        while (this.pagesVisited.size() < 10) {
            String currentUrl;
            if (this.pagesToVisit.isEmpty()) {
                currentUrl = url;
                this.pagesVisited.add(url);
            } else {
                currentUrl = this.nextUrl();
            }
            crawl(currentUrl);
            this.pagesToVisit.addAll(getLinks());
        }
        CSV_FILE.close();
    }

    public boolean crawl(String url) // Makes an HTTP request for a given url
    {
        try {
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            String htmlString = connection.get().text();
            if (connection.response().statusCode() == 200) {
                System.out.println("\nVisiting " + url);
            }
            if (!connection.response().contentType().contains("text/html")) {
                System.out.println("Not a valid HTML file");
                return false;
            }

            Elements linksOnPage = htmlDocument.select("a[href]");
            Element taglang = htmlDocument.select("html").first();
            String pageLanguage = taglang.attr("lang");

            // Check the language of the webpage - if it's in our language, download it.
            try {
                String pageLanguageDetect = checkLanguage(htmlString);
                String input = null;

                // check lang input from user input or cml
                if (lang.equals("en")) {
                    input = "English";
                } else if (lang.equals("es")) {
                    input = "Spanish";
                } else if (lang.equals("ru")) {
                    input = "Russian";
                }
                /**
                 * TODO: maybe use page language for adding to repository
                 * or add en-US en to pageLanguage.equals()
                 *
                 * */

                if (pageLanguageDetect.contains(lang)) {
                    System.out.println("SUCCESS! The page is in: " + pageLanguageDetect);
                    System.out.println("Found " + linksOnPage.size() + " links");
                    CSV_FILE.add(url, linksOnPage.size());
                    downloadFile(htmlString, pageLanguage);
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
        DetectLanguage.apiKey = "9883de6242b3d4347d2cb90e1e79c93f";
        DetectLanguage.ssl = true;
        String language = DetectLanguage.simpleDetect(url);
        return language;
    }

    // Write to a file - Done
    public void downloadFile(String file, String language) throws FileNotFoundException {
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