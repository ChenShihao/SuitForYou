package com.cufe.suitforyou.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Victor on 2016-08-31.
 */
public class RegexUtil {

    public static boolean matches(String regex, String line) {
        if (line == null || regex == null)
            return false;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        return matcher.matches();
    }
}
