package com.github.andreAmorimF.urlregex;

import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLRegex {

    private static final Pattern DEFAUL_START_PATTERN = Pattern.compile("^(https?://)");
    private static final Pattern URL_TOKENS_PATTERN = Pattern.compile("([^\\/]+)(\\/?)");
    private static final Pattern QUERY_SPLIT_PATTERN = Pattern.compile("&?(\\w+)(=[^&]+)?");
    private static final Pattern METACHARACTER = Pattern.compile("([.?*+^$\\[\\]\\\\(){}|\\-])");
    private static final Pattern NUMBERCHARACTER = Pattern.compile("\\d+");

    private static String NUMBER_PART = "NUM";
    private static String NUMBER_PATTERN_PART = "\\d+";
    private static String WILDCARD_PATTERN_PART(String separator, boolean optional) {
        String wildcard = "*";
        if (!optional)
            wildcard = "+";

        if (separator == null)
            return "[^/?]" + wildcard;

        return "[^" + separator + "]" + wildcard;
    }

    /**
     * @param urls
     * @return the most general regex matching all urls
     */
    public static Pattern buildPattern(List<String> urls) {

        String splitChar = "?";
        String defaultEndChar = "$";

        Map<String, List<URLSegment>> tokensByUrl = new LinkedHashMap<>();
        Map<String, String> queries = new LinkedHashMap<>();

        int queriesCount = 0;
        boolean allowsSSL = false;
        boolean allowsHttp = false;
        Matcher urlSegmentMatcher = URL_TOKENS_PATTERN.matcher("");
        Matcher queryPartMatcher = QUERY_SPLIT_PATTERN.matcher("");
        Matcher defaultStartMatcher = DEFAUL_START_PATTERN.matcher("");
        for (String url : urls) {

            // Check protocol of current URL
            defaultStartMatcher.reset(url);
            if (defaultStartMatcher.find()) {
                String protocol = defaultStartMatcher.group(1);
                if (protocol.equalsIgnoreCase("http://"))
                    allowsHttp = true;
                if (protocol.equalsIgnoreCase("https://"))
                    allowsSSL = true;
            }

            url = url.replaceFirst(DEFAUL_START_PATTERN.pattern(), "");

            // Breaks URL into two parts bettwen the '?' char
            int split = -1;
            if (url.contains(splitChar))
                split = url.indexOf(splitChar) + 1;

            String ulrPart;
            String queryPart = null;
            if (split > -1) {
                ulrPart = url.substring(0, split - 1);
                queryPart = url.substring(split);
            } else
                ulrPart = url;

            // Discover segments of urls
            List<URLSegment> elements = new ArrayList<>();
            urlSegmentMatcher.reset(ulrPart);
            boolean first = true;
            while (urlSegmentMatcher.find()) {
                String token = urlSegmentMatcher.group(1);
                String separator = urlSegmentMatcher.group(2);
                if (first) {
                    token = reverseDomainToken(token);
                    first = false;
                }

                URLSegment part = new URLSegment(token, separator);
                elements.add(part);
            }

            // For the given url, index all discovered segments
            tokensByUrl.put(url, elements);

            // Discovery of query parameters on the url
            if ( queryPart!= null ){
                queriesCount++;
                queryPartMatcher.reset(queryPart);
                while (queryPartMatcher.find()) {
                    String query = queryPartMatcher.group(1);
                    String value = queryPartMatcher.group(2);
                    queries.put(query, value);
                }
            }
        }

        boolean hasQueries = (queries.size() > 0);
        boolean queriesOptional = (urls.size() != queriesCount);

        String startPattern = "";
        String pattern = generalizeSegments(tokensByUrl);

        // Add queries patterns
        if (hasQueries) pattern += addQueryParams(queries, queriesOptional);

        // Add protocol patterns
        if (allowsHttp && allowsSSL) startPattern = "^https?://";
        else if (allowsSSL) startPattern = "^https://";
        else if (allowsHttp) startPattern = "^http://";

        return Pattern.compile(startPattern + pattern + defaultEndChar);
    }

    /**
     * Build the regex pattern by escaping and joining url segments.
     * @return the regex pattern for the discovered url segments.
     */
    private static String generalizeSegments(Map<String, List<URLSegment>> tokensByUrl) {

        List<URLSegment> generalized = new ArrayList<>();
        for (Map.Entry<String, List<URLSegment>> entry : tokensByUrl.entrySet()) {
            generalized = generalized.isEmpty() ? entry.getValue() : generalize(generalized, entry.getValue());
        }

        // Normalize result
        boolean wrapped = false;
        List<String> result = new ArrayList<>();
        ListIterator<URLSegment> it = generalized.listIterator();
        while (it.hasNext()) {
            boolean start = !(it.hasPrevious());
            URLSegment part = it.next();
            String token = part.getContent();
            if (start)
                token = reverseDomainToken(token);

            int wildcardPos = token.indexOf("*");
            int requiredWildCardPos = token.indexOf("+");
            int numberPos = token.indexOf(NUMBER_PART);
            if (wildcardPos >= 0 || numberPos >= 0 || requiredWildCardPos >= 0) {
                String[] subtokens = token.split("((?<=\\*)|(?=\\*)|(?<=\\+)|(?=\\+)|(?<=NUM)|(?=NUM))");
                for (int j = 0 ; j < subtokens.length; j++) {
                    String subtoken = subtokens[j];
                    if (subtoken.equals("+") && !it.hasNext())
                        subtokens[j] =  WILDCARD_PATTERN_PART("?", false);
                    else if (subtoken.equals("+"))
                        subtokens[j] =  WILDCARD_PATTERN_PART("/", false);
                    else if (subtoken.equals("*") && !it.hasNext())
                        subtokens[j] =  WILDCARD_PATTERN_PART("?", true);
                    else if (subtoken.equals("*"))
                        subtokens[j] =  WILDCARD_PATTERN_PART("/", true);
                    else if (subtoken.equals("NUM"))
                        subtokens[j] =  NUMBER_PATTERN_PART;
                    else if (!subtoken.isEmpty())
                        subtokens[j] = escapeToken(subtoken);
                }

                token = StringUtils.join(subtokens);
            } else {
                token = escapeToken(token);
            }

            if (part.optional && !wrapped) {
                token = "(" + token;
                wrapped = true;
            }

            boolean optionalSeparator = false;
            if (it.hasNext()) {
                URLSegment next = generalized.get(it.nextIndex());
                if (next != null && !next.optional && wrapped) {
                    token += part.getSeparator();
                    token += ")?";
                    wrapped = false;
                    optionalSeparator = true;
                }
            } else if (wrapped) {
                token += ")?";
            }

            result.add(token);
            if (!optionalSeparator) result.add(part.getSeparator());
        }

        return StringUtils.join(result, "");
    }

    /**
     * Add query parameters to the regular expression
     * @param queries
     * @return regex string matching additional query parameters
     */
    private static String addQueryParams(Map<String, String> queries, boolean queriesOptional) {
        StringBuilder builder = new StringBuilder();
        builder.append("\\??(");

        boolean hasValueQuery = false;
        boolean hasNoValueQuery = false;
        Iterator<Map.Entry<String, String>> it = queries.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            String queryKey = entry.getKey();
            String queryValue = entry.getValue();
            if (queryValue != null && !queryValue.isEmpty()) {
                hasValueQuery = true;

                builder.append("[&;]?");
                builder.append(queryKey);
                builder.append("=[^&;]+");

                if (it.hasNext()) builder.append("|");
            } else {
                hasNoValueQuery = true;
            }
        }

        if (hasNoValueQuery) {
            if (hasValueQuery) builder.append("|");
            builder.append("[^&;=]+");
        }

        builder = (queriesOptional ? builder.append(")*") : builder.append(")+"));
        return builder.toString();
    }

    /**
     * Reverse domain token.
     * @param token
     * @return
     */
    private static String reverseDomainToken(String token) {
        String[] parts = token.split("\\.");
        StringBuilder newToken = new StringBuilder();

        for (int i = parts.length-1; i >= 0; i--) {
            newToken.append(parts[i]);
            if (i > 0)
                newToken.append(".");
        }
        return newToken.toString();
    }

    /**
     * Escape special characters on input string for fitting it into regular expression.
     * @param token input string
     * @return escaped string
     */
    private static String escapeToken(String token) {
        Matcher matcher = METACHARACTER.matcher(token);
        Set<String> replaceThat = new LinkedHashSet<>();
        while (matcher.find()) {
            replaceThat.add(matcher.group(1));
        }

        for (String replace : replaceThat) {
            token = token.replaceAll("\\" + replace, "\\\\" + replace);
        }

        return token;
    }

    /**
     * Give the score of matching segment s1 against segment s2. This score is obtained by verifying the distance between those two segments.
     * @param s1 first segment
     * @param s2 second segment
     * @return match score between segment s1 and segment s2
     */
    private static double matchScore(String s1, String s2) {

        if (s1 == null && s2 == null)
            return 0.5;
        if (s1 == null || s2 == null)
            return 0.25;
        if (s1.equals(s2))
            return 1.0;

        Matcher s1NumMatcher = NUMBERCHARACTER.matcher(s1);
        Matcher s2NumMatcher = NUMBERCHARACTER.matcher(s2);
        String s1WithoutNumber = s1NumMatcher.replaceAll(NUMBER_PART);
        String s2WithoutNumber = s2NumMatcher.replaceAll(NUMBER_PART);

        if (s1WithoutNumber.equals(s2WithoutNumber))
            return 1.0;

        return (StringUtils.getLevenshteinDistance(s1WithoutNumber, s2WithoutNumber) * 0.5) /
            (s1WithoutNumber.length() > s2WithoutNumber.length() ? s1WithoutNumber.length() : s2WithoutNumber.length());
    }

    /**
     * In case segments s1 and s2 matches, use a set of heristics to find out the best generalization for both strings
     * @param s1 first segment
     * @param s2 second segment
     * @return general string that complies to s1 and s1
     */
    static String generalizeStep(String s1, String s2) {

        if (s1 == null || s2 == null)
            return null;

        // If both segments are equal, no generalization is needed (NOTE: numbers will not be generalized if segments are equal)
        if (s1.equals(s2))
            return s1;

        // Replace numbers on segments
        Matcher s1NumMatcher = NUMBERCHARACTER.matcher(s1);
        Matcher s2NumMatcher = NUMBERCHARACTER.matcher(s2);

        s1 = s1NumMatcher.replaceAll(NUMBER_PART);
        s2 = s2NumMatcher.replaceAll(NUMBER_PART);

        if (s1.equals(s2))
            return s1;

        // Compute common prefix and common suffix of input segments
        String[] tokens = {s1, s2};
        String[] reverdTokens = {StringUtils.reverse(s1), StringUtils.reverse(s2)};
        String commonPrefix = StringUtils.getCommonPrefix(tokens);
        String commonSuffix = StringUtils.getCommonPrefix(reverdTokens);
        commonSuffix = StringUtils.reverse(commonSuffix);

        // Return common suffix, in priority, if there is one
        if (!commonPrefix.isEmpty()) {
            if (commonPrefix.equals(s1) && !commonPrefix.equals(s2) ||
                commonPrefix.equals(s2) && !commonPrefix.equals(s1))
                return commonPrefix + "*";

            if (!commonSuffix.isEmpty())
                return intersecting_concatenate(commonPrefix, commonSuffix);

            return commonPrefix + "+";
        }

        // Return common suffix, if there is one
        if (!commonSuffix.isEmpty()) {
            if (commonSuffix.equals(s1) && !commonSuffix.equals(s2) ||
                commonSuffix.equals(s2) && !commonSuffix.equals(s1))
                return "*" + commonSuffix;

            return "+" + commonSuffix;
        }

        // If there is no common prefix or suffix for input segments, return the most general string
        return "+";
    }

    /**
     * Concatenate two strings, but if there is overlap at the intersection, replaces the intersection/overlap by wildcard
     * @param a
     * @param b
     * @return concatenated string
     */
    static String intersecting_concatenate(String a, String b) {
        // find length of maximum possible match
        int len_a = a.length();
        int len_b = b.length();
        int max_match = (len_a > len_b) ? len_b : len_a;

        // search down from maximum match size, to get longest possible intersection
        for (int size = max_match; size > 0; size--) {
            if (a.regionMatches(len_a - size, b, 0, size)) {
                return a.substring(0, a.length() - size) + "+" + b.substring(size, len_b);
            }
        }

        // Didn't find any intersection. Fall back to straight concatenation.
        return a + "+" + b;
    }

    private enum Direction {
        MATCH, SKIP_P1, SKIP_P2;
    }

    /**
     * Generalizes two list of url segments
     * @param urlList1
     * @param urlList2
     * @return list of url segments, each one in a more general form
     */
    public static List<URLSegment> generalize(List<URLSegment> urlList1, List<URLSegment> urlList2) {

        final int len1 = urlList1.size();
        final int len2 = urlList2.size();

        Direction dir[][] = new Direction[len1][len2];
        double score[][] = new double[len1][len2];

        // Fill matrixes
        for (int i = 0; i < len1; i++) {
            dir[i][0] = Direction.SKIP_P2;
            score[i][0] = 0.0;
        }

        for (int j = 0; j < len2; j++) {
            dir[0][j] = Direction.SKIP_P1;
            score[0][j] = 0.0;
        }

        dir[0][0] = Direction.MATCH;

        // Check scores between segments
        for (int i = 1; i < len1; i++) {
            for (int j = 1; j < len2; j++) {
                double s1 = score[i][j - 1] + (urlList1.get(i) == null ? 0.5 : 0.0);
                double s2 = score[i - 1][j] + (urlList2.get(j) == null ? 0.5 : 0.0);
                double m = score[i - 1][j - 1] + matchScore(urlList1.get(i).getContent(),
                    urlList2.get(j).getContent());

                if (m >= s1 && m >= s2) {
                    dir[i][j] = Direction.MATCH;
                    score[i][j] = m;
                } else if (s1 >= s2) {
                    dir[i][j] = Direction.SKIP_P1;
                    score[i][j] = s1;
                } else {
                    dir[i][j] = Direction.SKIP_P2;
                    score[i][j] = s2;
                }
            }
        }

        int i = len1 - 1;
        int j = len2 - 1;

        // Match and generalize segments
        List<URLSegment> urlSegments = new ArrayList<>();
        while (i >= 0 && j >= 0) {

            switch (dir[i][j]) {
                case MATCH:
                    String sg = generalizeStep(urlList1.get(i).getContent(), urlList2.get(j).getContent());
                    String separator = null;
                    if (urlList1.get(i).getSeparator().isEmpty() && !urlList2.get(j).getSeparator().isEmpty())
                        separator = urlList2.get(j).getSeparator().endsWith("?") ? urlList2.get(j).getSeparator() : urlList2.get(j).getSeparator() + "?";
                    else if (urlList2.get(j).getSeparator().isEmpty() && !urlList1.get(i).getSeparator().isEmpty())
                        separator = urlList1.get(i).getSeparator().endsWith("?") ? urlList1.get(i).getSeparator() : urlList1.get(i).getSeparator() + "?";
                    else
                        separator = urlList1.get(i).getSeparator();

                    if (sg != null) {
                        URLSegment segment = new URLSegment(sg, separator, urlList1.get(i).isOptional() || urlList2.get(j).isOptional());
                        urlSegments.add(segment);
                    }
                    i--;
                    j--;
                    break;
                case SKIP_P1:
                    URLSegment segment2 = urlList2.get(j);
                    segment2.setOptional(true);
                    urlSegments.add(segment2);
                    j--;
                    break;
                case SKIP_P2:
                    URLSegment segment1 = urlList1.get(i);
                    segment1.setOptional(true);
                    urlSegments.add(segment1);
                    i--;
                    break;
            }
        }

        Collections.reverse(urlSegments);
        return urlSegments;
    }

    /**
     * Represents one URL segment
     */
    private static class URLSegment {

        /**
         * Text content of the segment
         */
        private String content;

        /**
         * Separator character to the next segment
         */
        private String separator;

        /**
         * This segment might be optional
         */
        private boolean optional = false;

        public URLSegment(String content, String separator) {
            this(content, separator, false);
        }

        public URLSegment(String content, String separator, boolean optional) {
            this.content = content;
            this.separator = separator;
            this.optional = optional;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getSeparator() {
            return separator;
        }

        public void setSeparator(String separator) {
            this.separator = separator;
        }

        public boolean isOptional() {
            return optional;
        }

        public void setOptional(boolean optional) {
            this.optional = optional;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            URLSegment urlSegment = (URLSegment) o;

            if (optional != urlSegment.optional) return false;
            if (content != null ? !content.equals(urlSegment.content) : urlSegment.content != null) return false;
            if (separator != null ? !separator.equals(urlSegment.separator) : urlSegment.separator != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = content.hashCode();
            result = 31 * result + (separator != null ? separator.hashCode() : 0);
            result = 31 * result + (optional ? 1 : 0);
            return result;
        }

    }
}
