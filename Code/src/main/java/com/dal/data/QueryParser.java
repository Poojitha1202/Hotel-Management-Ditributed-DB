package com.dal.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryParser {
    public static String extractTableName(String query) {
        Pattern pattern = Pattern.compile("\\b(?:FROM|UPDATE|INTO)\\s+(\\w+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}