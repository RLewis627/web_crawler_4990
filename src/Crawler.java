import java.io.BufferedReader;
import java.io.FileNotFoundException;
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

    private CSV CSV_FILE = new CSV();
    private Set<String> pagesVisited = new HashSet<>();
    private List<String> pagesToVisit = new LinkedList<>();
    private List<String> links = new LinkedList<>();
    private static final String USER_AGENT = "Chrome-Chrome OS";
    private int i,j,k = 0;

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
            String htmlString = connection.get().html();
            if (connection.response().statusCode() == 200) {
                System.out.println("\nVisiting " + url);
            }
            if (!connection.response().contentType().contains("text/html")) {
                System.out.println("Not a valid HTML file");
                return false;
            }

            Elements linksOnPage = htmlDocument.select("a[href]"); 
            Document htmlDoc = Jsoup.parse(htmlString);
            Element taglang = htmlDoc.select("html").first();
			String pageLanguage = taglang.attr("lang");
			
			if (pageLanguage.equals("en")|pageLanguage.equals("es-ES")|pageLanguage.equals("ru")) {
				System.out.println("Found " + linksOnPage.size() + " links");
			    System.out.println("The has the following language attribute: " + pageLanguage);
			    CSV_FILE.add(url,linksOnPage.size());
			    downloadFile(htmlString, pageLanguage);
			} else {
			    System.out.println("The language is not in the language of our choice");
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
                //May throw org.json.JSONException: JSONObject["results"] not found.
                System.err.println("Failed to retrieve results for language.");
            }
        }
        return language;
    }

    // Write to a file - Done
    public void downloadFile(String file, String language) throws FileNotFoundException {
        if(language.equals("en")) { //en = English
        	try (PrintWriter out = new PrintWriter("repository/English/filename"+i+".html")) {
        		out.println(file);
        		i++;
        	}
        }
        else if(language.equals("es-ES")){ //es-ES = Espanol = Spanish
        	 try (PrintWriter out = new PrintWriter("repository/Spanish/filename"+j+".html")) {
                 out.println(file);
                 j++;
        	 }
        }
        else if(language.equals("ru")) { //ru = Russian
        	try (PrintWriter out = new PrintWriter("repository/Russain/filename"+k+".html")) {
                out.println(file);
                k++;
        	}
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