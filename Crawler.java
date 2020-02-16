import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler
{
  private static final int MAX_PAGES_TO_SEARCH = 10;
  private Set<String> pagesVisited = new HashSet<String>();
  private List<String> pagesToVisit = new LinkedList<String>();
	private List<String> links = new LinkedList<String>();
	private Document htmlDocument;
	private static final String USER_AGENT = "Chrome-Chrome OS";
	
	private String nextUrl()//Checks if URL has already been visited
	{
    String nextUrl;
    do{nextUrl = this.pagesToVisit.remove(0);} while(this.pagesVisited.contains(nextUrl));
    this.pagesVisited.add(nextUrl);
    return nextUrl;
  }

	public boolean crawl(String url)// Makes an HTTP request for a given url
	{
	  try
    {
      Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
      Document htmlDocument = connection.get();
      this.htmlDocument = htmlDocument;
			if(connection.response().statusCode() == 200){System.out.println("\nVisiting " + url);}
			if(!connection.response().contentType().contains("text/html"))
			{
				System.out.println("Not a valid HTML file");
        return false;
			}

      Elements linksOnPage = htmlDocument.select("a[href]");
      System.out.println("Found (" + linksOnPage.size() + ") links");
      for(Element link : linksOnPage){this.links.add(link.absUrl("href"));}
			return true;
     }catch(IOException ioe)
     {
       System.out.println("Error in out HTTP request " + ioe);
			 return false;
     }
	}
	
	public List<String> getLinks()// Returns a list of all the URLs on the page
	{
		return this.links;
	}
}
