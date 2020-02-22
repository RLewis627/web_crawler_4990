import java.util.Scanner;

public class CrawlerTest {

    //TODO: can take input from cml to specify the URL to crawl and language
    //TODO: user can set max pages visited from cml or input

    public static void main(String[] args) {
        String lang;
        Scanner sc = new Scanner(System.in);
        if(args.length == 1){
            //en - ru - es
            lang = args[1].substring(1, 2);
        }
        else {
            System.out.println("Enter a language: [es] [ru] [en] ");
            lang = sc.next();
        }

        Crawler crawler = new Crawler(lang);
        sc.close();
        
        //english default - redundant if
        String searchSite = "https://www.reddit.com/";

        if(lang.equals("en"))
            searchSite =  "https://www.reddit.com/";
        else if(lang.equals("es"))
            searchSite = "https://www.debate.com.mx/usa/";
        else if(lang.equals("ru")){
            searchSite = "https://www.mk.ru/";
        }

        //English - https://www.reddit.com/
        //Spanish - https://www.debate.com.mx/usa/
        //Russian - https://www.mk.ru/

        //Use different websites as needed. You don't have to use the ones already listed
        crawler.search(searchSite);
    }
}
