package cn.wtyoha.miaosha.validator.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IsMobileValidatorUtil {
    public static final Pattern pattern = Pattern.compile("1\\d{10}");

    public static boolean isMobile(String s) {
        if (s == null || s.length() == 0) {
            return false;
        }
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
    }
}
