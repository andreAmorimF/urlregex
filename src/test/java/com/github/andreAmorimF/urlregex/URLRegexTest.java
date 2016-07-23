package com.github.andreAmorimF.urlregex;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class URLRegexTest {

    @Test
    public void testStartEnd() {
        List<String> urls = new ArrayList<>();
        urls.add("http://www.domain.com/forums/");
        urls.add("https://www.domain.com/forums/");

        String pattern = URLRegex.buildPattern(urls).toString();
        assertTrue(pattern.startsWith("^https?://"));
        assertTrue(pattern.endsWith("$"));
    }

    @Test
    public void testStartEnd2() {
        List<String> urls = new ArrayList<>();
        urls.add("http://www.domain.com/forums/");
        urls.add("http://www.domain.com/forums/");

        String pattern = URLRegex.buildPattern(urls).toString();
        assertTrue(pattern.startsWith("^http://"));

        Pattern compiled = Pattern.compile(pattern);
        Matcher matcher = compiled.matcher("");

        List<String> badurls = new ArrayList<>();
        badurls.add("https://www.domain.co.uk/forums");
        for (String url : badurls) {
            matcher.reset(url);
            System.out.println("Testing bad match with : " + url);
            assertFalse(matcher.matches());
        }
    }

    @Test
    public void testStartEn3() {
        List<String> urls = new ArrayList<>();
        urls.add("https://www.domain.com/forums/");
        urls.add("https://www.domain.com/forums/");

        String pattern = URLRegex.buildPattern(urls).toString();
        assertTrue(pattern.startsWith("^https://"));

        Pattern compiled = Pattern.compile(pattern);
        Matcher matcher = compiled.matcher("");

        List<String> badurls = new ArrayList<>();
        badurls.add("http://www.domain.co.uk/forums");
        for (String url : badurls) {
            matcher.reset(url);
            System.out.println("Testing bad match with : " + url);
            assertFalse(matcher.matches());

        }
    }

    @Test
    public void testLastElement() {
        List<String> urls = new ArrayList<>();
        urls.add("http://www.domain.com/forums/viewforum_31.htm");
        urls.add("http://www.domain.com/forums/viewforum_32.htm");
        urls.add("http://www.domain.com/forums/viewforum_25.htm");

        String pattern = URLRegex.buildPattern(urls).toString();
        assertEquals("^http://www\\.domain\\.com/forums/viewforum_\\d+\\.htm$", pattern);

        Pattern compiled = Pattern.compile(pattern);
        Matcher matcher = compiled.matcher("");
        for (String url : urls) {
            matcher.reset(url);
            System.out.println("Testing match with : " + url);
            assertTrue(matcher.matches());
        }
    }

    @Test
    public void testLastElement2() {
        List<String> urls = new ArrayList<>();
        urls.add("http://www.domain.com/forums/viewforum_10-1.htm");
        urls.add("http://www.domain.com/forums/viewforum_10-10.htm");
        urls.add("http://www.domain.com/forums/viewforum_10-100.htm");
        urls.add("http://www.domain.com/forums/viewforum_10-101.htm");
        urls.add("http://www.domain.com/forums/viewforum_4767.htm");
        urls.add("http://www.domain.com/forums/viewtopic_49097-1.htm");
        urls.add("http://www.domain.com/forums/viewforum_49702-77.htm");
        urls.add("http://www.domain.com/forums/viewtopic_50051-2.htm");

        String pattern = URLRegex.buildPattern(urls).toString();
        assertEquals("^http://www\\.domain\\.com/forums/view[^?]+\\.htm$", pattern);

        Pattern compiled = Pattern.compile(pattern);
        Matcher matcher = compiled.matcher("");
        for (String url : urls) {
            matcher.reset(url);
            System.out.println("Testing match with : " + url);
            assertTrue(matcher.matches());
        }
    }

    @Test
    public void testLastElement3() {
        List<String> urls = new ArrayList<>();
        urls.add("http://www.domain.co.uk/test/forum/what-gives-away-your-travel-obsession_4732");
        urls.add("http://www.domain.co.uk/test/forum");
        urls.add("http://www.domain.co.uk/test/forum/san-jose-costa-rica-city-tours_4731");
        urls.add("http://www.domain.co.uk/test/forum/five-highlights-from-your--2014-travels_4730");

        String pattern = URLRegex.buildPattern(urls).toString();
        assertEquals("^http://www\\.domain\\.co\\.uk/test/forum/?([^?]+_\\d+)?$", pattern);

        Pattern compiled = Pattern.compile(pattern);
        Matcher matcher = compiled.matcher("");
        for (String url : urls) {
            matcher.reset(url);
            System.out.println("Testing match with : " + url);
            assertTrue(matcher.matches());
        }

        List<String> badurls = new ArrayList<>();
        badurls.add("http://www.domain.co.uk/test/forum/what-gives-away-your-travel-obsession_4732?query=x");
        for (String url : badurls) {
            matcher.reset(url);
            System.out.println("Testing bad match with : " + url);
            assertFalse(matcher.matches());
        }
    }

    @Test
    public void testLastElement4(){
        List<String> urls = new ArrayList<>();
        urls.add("http://www.domain.com/campaign/286");
        urls.add("http://www.domain.com/products/computers-printers-scanners");
        urls.add("http://www.domain.com/offers/best-discounts");

        String pattern = URLRegex.buildPattern(urls).toString();
        assertEquals("^http://www\\.domain\\.com/[^/]+/[^?]+$", pattern);

        Pattern compiled = Pattern.compile(pattern);
        Matcher matcher = compiled.matcher("");
        for (String url : urls) {
            matcher.reset(url);
            System.out.println("Testing match with : " + url);
            assertTrue(matcher.matches());
        }
    }

    @Test
    public void testLastElement5(){
        List<String> urls = new ArrayList<>();
        urls.add("http://www.domain.fr/visage/exfoliant.aspx");
        urls.add("http://www.domain.fr/visage/toniques.aspx");
        urls.add("http://www.domain.fr/search.aspx#?fh_location=categories%3C%7Buniversefr_frc_makeup%7D%2Ffacet_product_type_fr%3E%7Bfr_fr_product_type_foundation%7D");

        String pattern = URLRegex.buildPattern(urls).toString();
        assertEquals("^http://www\\.domain\\.fr/[^/]+/?([^?]+\\.aspx)?\\??([&;]?fh_location=[^&;]+)*$", pattern);

        Pattern compiled = Pattern.compile(pattern);
        Matcher matcher = compiled.matcher("");
        for (String url : urls) {
            matcher.reset(url);
            System.out.println("Testing match with : " + url);
            assertTrue(matcher.matches());
        }
    }

    @Test
    public void testLastElement6() {
        List<String> urls = new ArrayList<>();
        urls.add("http://a/b/list");
        urls.add("http://a/b/c/d/list");

        String pattern = URLRegex.buildPattern(urls).toString();
        assertEquals("^http://a/b/(c/d/)?list$", pattern);

        Pattern compiled = Pattern.compile(pattern);
        Matcher matcher = compiled.matcher("");
        for (String url : urls) {
            matcher.reset(url);
            System.out.println("Testing match with : " + url);
            assertTrue(matcher.matches());
        }

        List<String> badurls = new ArrayList<>();
        badurls.add("http://a/b//list");
        for (String url : badurls) {
            matcher.reset(url);
            System.out.println("Testing bad match with : " + url);
            assertFalse(matcher.matches());
        }
    }

    @Test
    public void testLastElement7() {
        List<String> urls = new ArrayList<>();
        urls.add("http://www.domain.co.uk/test/forum/what-gives-away-your-travel-obsession_4732");
        urls.add("http://www.domain.co.uk/test/forum/");
        urls.add("http://www.domain.co.uk/test/forum/san-jose-costa-rica-city-tours_4731");
        urls.add("http://www.domain.co.uk/test/forum/five-highlights-from-your--2014-travels_4730");

        String pattern = URLRegex.buildPattern(urls).toString();
        assertEquals("^http://www\\.domain\\.co\\.uk/test/forum/([^?]+_\\d+)?$", pattern);

        Pattern compiled = Pattern.compile(pattern);
        Matcher matcher = compiled.matcher("");
        for (String url : urls) {
            matcher.reset(url);
            System.out.println("Testing match with : " + url);
            assertTrue(matcher.matches());
        }
    }

    @Test
    public void testLastElement8() {
        List<String> urls = new ArrayList<>();
        urls.add("http://www.domain.com/forums/viewforum_10-1.htm");
        urls.add("http://www.domain.com/forums/viewforum_10-10.htm");
        urls.add("http://www.domain.com/forums/viewforum_10-100.htm");
        urls.add("http://www.domain.com/forums/viewforum_10-101.htm");
        urls.add("http://www.domain.com/forums/viewforum_4767.htm");
        urls.add("http://www.domain.com/forums/viewforum_1.htm");
        //urls.add("http://www.domain.com/forums/viewtopic_49097-1.htm");
        urls.add("http://www.domain.com/forums/viewforum_49702-77.htm");
        //urls.add("http://www.domain.com/forums/viewtopic_50051-2.htm");

        String pattern = URLRegex.buildPattern(urls).toString();
        assertEquals("^http://www\\.domain\\.com/forums/viewforum_[^?]+\\.htm$", pattern);

        Pattern compiled = Pattern.compile(pattern);
        Matcher matcher = compiled.matcher("");
        for (String url : urls) {
            matcher.reset(url);
            System.out.println("Testing match with : " + url);
            assertTrue(matcher.matches());
        }
    }

    @Test
    public void testLastElement9() {
        List<String> urls = new ArrayList<>();
        urls.add("http://www.domain.com/forums/viewforum_10-1.htm");
        urls.add("http://www.domain.com/forums/viewforum_10-10.htm");
        urls.add("http://www.domain.com/forums/viewforum_10-100.htm");
        urls.add("http://www.domain.com/forums/viewforum_10-101.htm");
        urls.add("http://www.domain.com/forums/viewforum_49702-77.htm");

        String pattern = URLRegex.buildPattern(urls).toString();
        assertEquals("^http://www\\.domain\\.com/forums/viewforum_\\d+\\-\\d+\\.htm$", pattern);

        Pattern compiled = Pattern.compile(pattern);
        Matcher matcher = compiled.matcher("");
        for (String url : urls) {
            matcher.reset(url);
            System.out.println("Testing match with : " + url);
            assertTrue(matcher.matches());
        }
    }

    @Test
    public void testNotRequiredElement() {
        List<String> urls = new ArrayList<>();
        urls.add("http://www.letempledelaforme.com/forums/");
        urls.add("http://www.letempledelaforme.com/forums/16/forum-musculation/view/page/3");
        urls.add("http://www.letempledelaforme.com/forums/22/forum-cardio/view/page/6");
        urls.add("http://www.letempledelaforme.com/forums/34/test-cardio/view/page/23");

        String pattern = URLRegex.buildPattern(urls).toString();
        assertEquals("^http://www\\.letempledelaforme\\.com/forums/(\\d+/[^/]+/view/page/\\d+)?$", pattern);

        Pattern compiled = Pattern.compile(pattern);
        Matcher matcher = compiled.matcher("");
        for (String url : urls) {
            matcher.reset(url);
            System.out.println("Testing match with : " + url);
            assertTrue(matcher.matches());
        }
    }

    @Test
    public void testNotRequiredElement2() {
        List<String> urls = new ArrayList<>();
        urls.add("http://www.domain.com/forums");
        urls.add("http://www.domain.com/forums/");
        urls.add("http://www.domain.com/forums/viewforum_31.htm");
        urls.add("http://www.domain.com/forums/viewforum_32.htm");
        urls.add("http://www.domain.com/forums/viewforum_25.htm");

        String pattern = URLRegex.buildPattern(urls).toString();
        assertEquals("^http://www\\.domain\\.com/forums/?(viewforum_\\d+\\.htm)?$", pattern);

        Pattern compiled = Pattern.compile(pattern);
        Matcher matcher = compiled.matcher("");
        for (String url : urls) {
            matcher.reset(url);
            System.out.println("Testing match with : " + url);
            assertTrue(matcher.matches());
        }
    }

    @Test
    public void testLastSlash() {
        List<String> urls = new ArrayList<>();
        urls.add("http://forum.cultureco.com/informatique-assistance-et");
        urls.add("http://forum.cultureco.com/informatique-assistance-et-conseils/");
        urls.add("http://forum.cultureco.com/bli/");

        String pattern = URLRegex.buildPattern(urls).toString();
        assertEquals("^http://forum\\.cultureco\\.com/[^?]+/?$", pattern);

        Pattern compiled = Pattern.compile(pattern);
        Matcher matcher = compiled.matcher("");
        for (String url : urls) {
            matcher.reset(url);
            System.out.println("Testing match with : " + url);
            assertTrue(matcher.matches());
        }
    }

    @Test
    public void testDifferentDomain() {
        List<String> urls = new ArrayList<>();
        urls.add("http://forum.domain.com/forums/viewforum_31.htm");
        urls.add("http://forum.domain.com/forums/viewforum_32.htm");
        urls.add("http://www.domain.com/forums/viewforum_25.htm");

        String pattern = URLRegex.buildPattern(urls).toString();
        assertEquals("^http://[^/]+\\.domain\\.com/forums/viewforum_\\d+\\.htm$", pattern);

        Pattern compiled = Pattern.compile(pattern);
        Matcher matcher = compiled.matcher("");
        for (String url : urls) {
            matcher.reset(url);
            System.out.println("Testing match with : " + url);
            assertTrue(matcher.matches());
        }

        List<String> badurls = new ArrayList<>();
        badurls.add("http://forum.domain.com/forums/viewforum_31.htm?query=x");
        for (String url : badurls) {
            matcher.reset(url);
            System.out.println("Testing bad match with : " + url);
            assertFalse(matcher.matches());
        }
    }

    @Test
    public void testQuery() {
        List<String> urls = new ArrayList<>();
        urls.add("http://forum.domain.com/forums/viewforum_31.htm?query=value");
        urls.add("http://forum.domain.com/forums/viewforum_32.htm?query=value&query2=value2");
        urls.add("http://forum.domain.com/forums/viewforum_25.htm?query3=value3");
        urls.add("http://forum.domain.com/forums/viewforum_25.htm");
        urls.add("http://forum.domain.com/forums/viewforum_25/");

        String pattern = URLRegex.buildPattern(urls).toString();
        assertEquals("^http://forum\\.domain\\.com/forums/viewforum_\\d+[^?]*/?\\??([&;]?query=[^&;]+|[&;]?query2=[^&;]+|[&;]?query3=[^&;]+)*$", pattern);

        Pattern compiled = Pattern.compile(pattern);
        Matcher matcher = compiled.matcher("");
        for (String url : urls) {
            matcher.reset(url);
            System.out.println("Testing match with : " + url);
            assertTrue(matcher.matches());
        }
    }

    @Test
    public void testQuery2() {
        List<String> urls = new ArrayList<>();
        urls.add("http://georezo.net/forum/viewforum.php?id=50");
        urls.add("http://georezo.net/forum/viewforum.php?id=1&p=2");

        String pattern = URLRegex.buildPattern(urls).toString();
        assertEquals("^http://georezo\\.net/forum/viewforum\\.php\\??([&;]?id=[^&;]+|[&;]?p=[^&;]+)+$", pattern);

        Pattern compiled = Pattern.compile(pattern);
        Matcher matcher = compiled.matcher("");
        for (String url : urls) {
            matcher.reset(url);
            System.out.println("Testing match with : " + url);
            assertTrue(matcher.matches());
        }
    }

    @Test
    public void testQuery3(){
        List<String> urls = new ArrayList<>();
        urls.add("http://forum.domain.com/some-controller/some-action?=baz&foo=bar&edit&spam=eggs=ham&==&");

        String pattern = URLRegex.buildPattern(urls).toString();
        assertEquals("^http://forum\\.domain\\.com/some\\-controller/some\\-action\\??([&;]?foo=[^&;]+|[&;]?spam=[^&;]+|[^&;=]+)+$", pattern);
    }

    @Test
    public void testQuery4(){
        List<String> urls = new ArrayList<>();
        urls.add("http://forum.domain.com/some-controller/some-action?baz");
        urls.add("http://forum.domain.com/some-controller/some-action?foo");
        urls.add("http://forum.domain.com/some-controller/some-action?edit");
        urls.add("http://forum.domain.com/some-controller/some-action?spam");
        urls.add("http://forum.domain.com/some-controller/some-action?eggs");
        urls.add("http://forum.domain.com/some-controller/some-action?ham");
        urls.add("http://forum.domain.com/some-controller/some-action?test=2");

        String pattern = URLRegex.buildPattern(urls).toString();
        assertEquals("^http://forum\\.domain\\.com/some\\-controller/some\\-action\\??([&;]?test=[^&;]+|[^&;=]+)+$", pattern);
        Pattern compiled = Pattern.compile(pattern);
        Matcher matcher = compiled.matcher("");
        for (String url : urls) {
            matcher.reset(url);
            System.out.println("Testing match with : " + url);
            assertTrue(matcher.matches());
        }

        List<String> badurls = new ArrayList<>();
        badurls.add("http://forum.domain.com/some-controller/some-action?baz&sid=89289829479749449894");

        for (String url : badurls) {
            matcher.reset(url);
            System.out.println("Testing bad match with : " + url);
            assertFalse(matcher.matches());
        }
    }

    @Test
    public void testQuery5(){
        List<String> urls = new ArrayList<>();
        urls.add("http://forum.domain.com/some-controller/some-action"); //optional query
        urls.add("http://forum.domain.com/some-controller/some-action?baz");
        urls.add("http://forum.domain.com/some-controller/some-action?foo");
        urls.add("http://forum.domain.com/some-controller/some-action?edit");
        urls.add("http://forum.domain.com/some-controller/some-action?spam");
        urls.add("http://forum.domain.com/some-controller/some-action?eggs");
        urls.add("http://forum.domain.com/some-controller/some-action?ham");
        urls.add("http://forum.domain.com/some-controller/some-action?test=2");

        String pattern = URLRegex.buildPattern(urls).toString();
        assertEquals("^http://forum\\.domain\\.com/some\\-controller/some\\-action\\??([&;]?test=[^&;]+|[^&;=]+)*$", pattern);
        Pattern compiled = Pattern.compile(pattern);
        Matcher matcher = compiled.matcher("");
        for (String url : urls) {
            matcher.reset(url);
            System.out.println("Testing match with : " + url);
            assertTrue(matcher.matches());
        }
    }

    @Test
    public void testFragments() {
        List<String> urls = new ArrayList<>();
        urls.add("http://forum.domain.com/forums/viewforum_31.htm?query=value");
        urls.add("http://forum.domain.com/forums/viewforum_31.htm#test");
        urls.add("http://forum.domain.com/forums/viewforum_25.htm");
        urls.add("http://forum.domain.com/forums/viewforum_25/");

        String pattern = URLRegex.buildPattern(urls).toString();
        assertEquals("^http://forum\\.domain\\.com/forums/viewforum_\\d+[^?]*/?\\??([&;]?query=[^&;]+)*$", pattern);

        Pattern compiled = Pattern.compile(pattern);
        Matcher matcher = compiled.matcher("");
        for (String url : urls) {
            matcher.reset(url);
            System.out.println("Testing match with : " + url);
            assertTrue(matcher.matches());
        }
    }

    @Test
    public void testFragments2() {
        List<String> urls = new ArrayList<>();
        urls.add("http://www.domain.fr/corps-bain/savon.aspx#/savon.aspx");
        urls.add("http://www.domain.fr/corps-bain/deodorants.aspx#/deodorants.aspx");

        String pattern = URLRegex.buildPattern(urls).toString();
        assertEquals("^http://www\\.domain\\.fr/corps\\-bain/[^/]+\\.aspx#/[^?]+\\.aspx$", pattern);

        Pattern compiled = Pattern.compile(pattern);
        Matcher matcher = compiled.matcher("");
        for (String url : urls) {
            matcher.reset(url);
            System.out.println("Testing match with : " + url);
            assertTrue(matcher.matches());
        }
    }

    @Test
    public void testAllTogether() {
        List<String> urls = new ArrayList<>();
        urls.add("http://www.domain.com/forums");
        urls.add("http://www.domain.com/forums/");
        urls.add("https://www.domain.com/forums/");
        urls.add("http://forum.domain.com/forums/viewforum_31.htm");
        urls.add("http://forum.domain.com/test/viewforum_32.htm");
        urls.add("http://www.domain.com/forums/viewforum_25.htm");
        urls.add("http://forum.domain.com/forums/viewforum_31.htm?query=value");
        urls.add("http://forum.domain.com/forums/viewforum_31.htm?query=value&query2=value2");
        urls.add("http://forum.domain.com/forums/viewforum_31.htm?query=value;query2=value2");
        urls.add("http://forum.domain.com/forums/viewforum_31.htm#test");

        String pattern = URLRegex.buildPattern(urls).toString();
        assertEquals("^https?://[^/]+\\.domain\\.com/[^/]+/?(viewforum_\\d+\\.htm[^?]*)?\\??([&;]?query=[^&;]+|[&;]?query2=[^&;]+)*$", pattern);

        Pattern compiled = Pattern.compile(pattern);
        Matcher matcher = compiled.matcher("");
        for (String url : urls) {
            matcher.reset(url);
            System.out.println("Testing match with : " + url);
            assertTrue(matcher.matches());
        }

        List<String> badurls = new ArrayList<>();
        badurls.add("http://domain.com/forums/other");
        badurls.add("http://www.domain.uk/forums/other");
        badurls.add("http://www.domain.com/forums/other");
        badurls.add("http://www.notdomain.com/forums/");
        badurls.add("http://www.notdomain.com/more/parts/than/necessary");
        badurls.add("http://forum.domain.com/");
        badurls.add("http://forum.domain.com/forums/viewforum_31.htm?query3=value");
        badurls.add("http://www.domain.com/forums/viewforum.htm");

        for (String url : badurls) {
            matcher.reset(url);
            System.out.println("Testing bad match with : " + url);
            assertFalse(matcher.matches());
        }
    }

    @Test
    public void testHifen() {
        List<String> urls = new ArrayList<>();
        urls.add("http://www.vectra-gts.com/f17p50-rencontres-de-vectra-signum");
        urls.add("http://www.vectra-gts.com/f1p100-presentation-des-membres");
        urls.add("http://www.vectra-gts.com/f2p1950-presentation-des-membres");

        String pattern = URLRegex.buildPattern(urls).toString();
        assertEquals("^http://www\\.vectra\\-gts\\.com/f\\d+p\\d+\\-[^?]+$", pattern);
    }

    @Test
    public void testBadMatch() {
        List<String> urls = new ArrayList<>();
        urls.add("http://forum.trader-finance.fr/actions-us/index2.html");
        urls.add("http://forum.trader-finance.fr/actions-us/index3.html");
        urls.add("http://forum.trader-finance.fr/actions-us/index4.html");
        urls.add("http://forum.trader-finance.fr/indices-boursier/index2.html");
        urls.add("http://forum.trader-finance.fr/indices-boursier/index3.html");
        urls.add("http://forum.trader-finance.fr/indices-boursier/index9.html");

        String pattern = URLRegex.buildPattern(urls).toString();
        assertEquals("^http://forum\\.trader\\-finance\\.fr/[^/]+/index\\d+\\.html$", pattern);

        Pattern compiled = Pattern.compile(pattern);
        Matcher matcher = compiled.matcher("");
        for (String url : urls) {
            matcher.reset(url);
            System.out.println("Testing match with : " + url);
            assertTrue(matcher.matches());
        }
    }

    @Test
    public void testBadMatch2() {
        List<String> urls = new ArrayList<>();
        urls.add("http://www.tomshardware.co.uk/forum/forum-4/page-2.html");
        urls.add("http://www.tomshardware.co.uk/forum/forum-4/page-20.html");
        urls.add("http://www.tomshardware.co.uk/forum/forum-4/page-3.html");
        urls.add("http://www.tomshardware.co.uk/forum/forum-74/page-100.html");
        urls.add("http://www.tomshardware.co.uk/forum/forum-74/page-1000.html");
        urls.add("http://www.tomshardware.co.uk/forum/forum-74/page-2.html");
        urls.add("http://www.tomshardware.co.uk/forum/forum-74/page-200.html");
        urls.add("http://www.tomshardware.co.uk/forum/forum-74/page-3.html");

        String pattern = URLRegex.buildPattern(urls).toString();
        assertEquals("^http://www\\.tomshardware\\.co\\.uk/forum/forum\\-\\d+/page\\-\\d+\\.html$", pattern);

        Pattern compiled = Pattern.compile(pattern);
        Matcher matcher = compiled.matcher("");
        for (String url : urls) {
            matcher.reset(url);
            System.out.println("Testing match with : " + url);
            assertTrue(matcher.matches());
        }
    }

    @Test
    public void testGeneralizeStep() {
        String result = URLRegex.generalizeStep("general_2", "general_23");

        assertEquals("general_NUM", result);
    }

    @Test
    public void testGeneralizeStep2() {
        String result = URLRegex.generalizeStep("2", "23");

        assertEquals("NUM", result);
    }

    @Test
    public void testGeneralizeStep3() {
        String result = URLRegex.generalizeStep("2_test", "23_test");

        assertEquals("NUM_test", result);
    }

    @Test
    public void testGeneralizeStep4() {
        String result = URLRegex.generalizeStep("viewforum_31/", "viewforum_25");

        assertEquals("viewforum_NUM*", result);
    }

    @Test
    public void testGeneralizeStep5() {
        String result = URLRegex.generalizeStep("viewforum_31.php", "viewforum_25.htm");

        assertEquals("viewforum_NUM.+", result);
    }

    @Test
    public void testGeneralizeStep6() {
        String result = URLRegex.generalizeStep("viewforum_25.htm", "viewforum_25/");

        assertEquals("viewforum_NUM+", result);
    }

    @Test
    public void testGeneralizeStep7() {
        String result = URLRegex.generalizeStep("b-coeur-ouvert-f152.html", "achats-et-puericulture-f185.html");

        assertEquals("+-fNUM.html", result);
    }

    @Test
    public void testGeneralizeStep8() {
        String result = URLRegex.generalizeStep("84299-parliez-vous-cantonais-avant-de-partir.html",
            "98966-applications-utiles-pour-le-mandarin-et-le-cantonais.html");

        assertEquals("NUM-+.html", result);
    }

    @Test
    public void testGeneralizeStep9() {
        String result = URLRegex.generalizeStep("f112.html", "f12.html");

        assertEquals("fNUM.html", result);
    }

}
