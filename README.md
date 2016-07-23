URLRegex
========

From a set of URLs, this library builds a regular expression matching with all URLs used as inputs.

## Install

If you are using Maven, just add to the _dependencies_ section of your _pom.xml_.

```xml
<dependency>
    <groupId>com.github.andreAmorimF</groupId>
    <artifactId>urlregex</artifactId>
    <version>1.0</version>
</dependency>
```

## Some examples

Different protocols:

```java
// Given inputs
List<String> urls = new ArrayList<>();
urls.add("http://www.domain.com/forums/");
urls.add("https://www.domain.com/forums/");

// Build pattern and output
String pattern = URLRegex.buildPattern(urls).toString();
System.out.print(pattern)
"^https?://www.domain.com/forums/$"
```

Unequal number of URL segments:

```java
// Given inputs
List<String> urls = new ArrayList<>();
urls.add("http://www.domain.com/forums");
urls.add("http://www.domain.com/forums/viewforum_31.htm");
urls.add("http://www.domain.com/forums/viewforum_25.htm");

// Build pattern and output
String pattern = URLRegex.buildPattern(urls).toString();
System.out.print(pattern)
"^http://www\\.domain\\.com/forums/?(viewforum_\\d+\\.htm)?$"
```

Using query String:

```java
// Given inputs
List<String> urls = new ArrayList<>();
urls.add("http://domain.com/forum/viewforum.php?id=50");
urls.add("http://www.domain.com/forum/viewforum.php?id=1&p=2");

// Build pattern and output
String pattern = URLRegex.buildPattern(urls).toString();
System.out.print(pattern)
"^http://(www\\.)?domain\\.com/forum/viewforum\\.php\\??([&;]?id=[^&;]+|[&;]?p=[^&;]+)+$"
```

## Author

Andre Fonseca <andre.amorimfonseca@gmail.com>

## License

The MIT License
