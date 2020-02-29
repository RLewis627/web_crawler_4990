# web_crawler_4990
## Description
Web crawler used to gather data on word frequencies and rank for 3 different languages
### Input
Ask the user for input to crawl language or
specify the argument in the command line java CrawlerTest -en .. -ru -es 
else default is an "English" website
Also ask user for # of max pages to visit
## Resources
Resources should be under the lib folder containing our third party libraries, jar files, and APIs.
https://jsoup.org/download
## Documentation
### nextUrl function
`String nextUrl()`
#### description
Checks if a URL has already been visited
#### parameters
None
#### returns
Unvisited URL
### crawl function
`public boolean crawl(String url)`
#### description
Makes an HTTP request for a given url
#### parameters
String url - the URL to be crawled
#### returns
False - page cannot be retrieved\
True - page successfully retrieved
### getLinks function
`public List<String> getLinks()`
#### description
Returns a list of all the URLs on the page
#### parameters
None
#### returns
a list of URLS
