package com.iict.buet.customer_portal.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatchPattern {
    public static Boolean isMatchPattern(String text, String patternString) {
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }
}