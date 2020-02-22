public class CrawlerTest {

    public static void main(String[] args) {
        Crawler crawler = new Crawler();
        //English - https://www.reddit.com/
        //Spanish - https://www.debate.com.mx/usa/
        //Russain - https://www.mk.ru/
        //Use different websites as needed. You don't have to use the ones already listed
        crawler.search("https://www.debate.com.mx/usa/");
    }
}
