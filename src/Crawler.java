import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.net.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {

    private Set<String> pagesVisited = new HashSet<>();
    private List<String> pagesToVisit = new LinkedList<>();
    private List<String> links = new LinkedList<>();
    private Document htmlDocument;
    private static final String USER_AGENT = "Chrome-Chrome OS";

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
        // MAX_PAGES_TO_SEARCH testing manual input
        while (this.pagesVisited.size() < 1) {
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
    }

    public boolean crawl(String url) // Makes an HTTP request for a given url
    {
        try {
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            String htmlString = connection.get().html();
            this.htmlDocument = htmlDocument;

            if (connection.response().statusCode() == 200) {
                System.out.println("\nVisiting " + url);
            }
            if (!connection.response().contentType().contains("text/html")) {
                System.out.println("Not a valid HTML file");
                return false;
            }

            Elements linksOnPage = htmlDocument.select("a[href]");

            System.out.println("Found " + linksOnPage.size() + " links");

            // Check the language of the webpage - if it's in our language, download it.
            try {
                // Limited to the first 500 words of the website so the API doesn't get
                // overloaded
                String pageLanguage = checkLanguage(htmlString.substring(0, 500));
                if (pageLanguage.equals("English")) {
                    System.out.println("The page is in: " + pageLanguage);
                    downloadFile(htmlString);
                } else {
                    System.out.println("The language is not in the language of our choice");
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
    public String checkLanguage(String url) throws IOException, InterruptedException {
        String language = "";
        String qry = url;
        String urlString = "http://api.languagelayer.com/detect?access_key=a41003d098828a4f509e414890e40464&query="
                + encode(qry);

        // Preparing the URL request
        URL urlReq = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) urlReq.openConnection();

        // Setting the request method and headers
        con.setRequestMethod("GET");
        con.addRequestProperty("User-Agent", USER_AGENT);

        int status = con.getResponseCode();

        // Check if the response code was successful
        if (status == 200) {
            System.out.println("\nStatus code: " + status);

            // Gather the response and turn into a String object
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine + "\n");
            }
            in.close();

            // Convert String into a JSON object for parsing
            try {
                JSONObject jsonResponse = new JSONObject(content.toString());
                JSONArray results = jsonResponse.getJSONArray("results");
                language = results.getJSONObject(0).getString("language_name");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return language;
    }

    // Write to a file - Done
    public void downloadFile(String file) throws FileNotFoundException {
        System.out.println("Downloading file");
        try (PrintWriter out = new PrintWriter("filename.html")) {
            out.println(file);
        }
    }

    public static String encode(String url) {  
        try {  
                String encodeURL=URLEncoder.encode( url, "UTF-8" );  
                return encodeURL;  
        } catch (UnsupportedEncodingException e) {  
                return "Issue while encoding" +e.getMessage();  
        }  
    }

    public List<String> getLinks()// Returns a list of all the URLs on the page
    {
        return this.links;
    }
}
