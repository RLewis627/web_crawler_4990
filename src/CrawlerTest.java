import java.util.Scanner;

/**
 * Group: Rachel Lewis
 * Samuel Belarmino
 * Auraiporn Auksorn
 * Daniel Santana Medina
 * Josh Woolbright
 * <p>
 * Project 1: Web Crawling Zipf's Law
 * Course: CS 4990.02 Web Search & Recommender Systems
 * Professor: Dr. Ben Steichen
 * <p>
 * Description:
 * The purpose of this project is to crawl websites
 * and download them for a Zipf's law analysis. During
 * the crawling method we also detect the site's language depending
 * if the user selected the language. We also add the URL and
 * number of out links for a given website to a CSV file.
 * All of the information is gathered onto a master file for
 * further analysis.
 * <p>
 * Libraries:
 * Detect Language, Gson, Jsoup, GSon, Java-json, and OpenCSV
 */

public class CrawlerTest {

    //Can also take input from cml to specify the URL to crawl the language
    //and to specify max sites

    public static void main(String[] args) {
        String lang;
        int maxPages;
        Scanner sc = new Scanner(System.in);
        if (args.length > 1) {
            //en - ru - es
            lang = args[0].substring(1, 3);
            maxPages = Integer.parseInt(args[1].substring(1, 3));
            System.out.println("Crawling in: " + lang + "\nnumber of sites: " + maxPages);
        } else {
            System.out.println("Which language would you like to crawl in: [es] [ru] [en] ");
            lang = sc.next();
            System.out.println("Enter the number of max sites you would like to crawl: ");
            maxPages = sc.nextInt();
        }

        Crawler crawler = new Crawler(lang, maxPages);
        sc.close();

        //english default
        String searchSite;
        switch (lang) {
            case "es":
                searchSite = "https://www.debate.com.mx/usa/";
                break;
            case "ru":
                searchSite = "https://www.mk.ru/";
                break;
            default:
                searchSite = "https://www.reddit.com/";
                break;
        }

        //English - https://www.reddit.com/
        //Spanish - https://www.debate.com.mx/usa/
        //Russian - https://www.mk.ru/

        //Use different websites as needed. You don't have to use the ones already listed
        crawler.search(searchSite);
    }
}
