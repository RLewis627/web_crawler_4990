# web_crawler_4990
## Description
Web crawler used to gather data on word frequencies and rank for 3 different languages
## Resources
Resources should be under the lib folder containing our third party libraries, jar files, and APIs.
https://jsoup.org/download
## Documentation
### nextUrl function
`String nextUrl()`
#### description
Checks if a URL has already been visited
#### parameters
None, maybe args to specify max visited (command line input or read)
#### returns
Unvisited URL
### crawl function
`public boolean crawl(String url)`
#### description
Makes an HTTP request for a given url
#### parameters
String url - the URL to be crawled
#### returns
False - page cannot be retreived\
True - page successfully retreived
### getLinks function
`public List<String> getLinks()`
#### description
Returns a list of all the URLs on the page
#### parameters
None
#### returns
a list of URLS
