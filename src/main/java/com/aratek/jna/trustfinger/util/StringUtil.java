package com.aratek.jna.trustfinger.util;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shijinlong
 * @date 2018-05-01
 * @description String帮助类
 */
public class StringUtil {

    public static final int INDEX_NOT_FOUND = -1;

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static String replace(String text, String searchString, String replacement) {
        return replace(text, searchString, replacement, -1);
    }

    public static String replace(String text, String searchString, String replacement, int max) {
        if (isEmpty(text) || isEmpty(searchString) || replacement == null || max == 0) {
            return text;
        }
        int start = 0;
        int end = text.indexOf(searchString, start);
        if (end == INDEX_NOT_FOUND) {
            return text;
        }
        int replLength = searchString.length();
        int increase = replacement.length() - replLength;
        increase = (increase < 0 ? 0 : increase);
        increase *= (max < 0 ? 16 : (max > 64 ? 64 : max));
        StringBuffer buf = new StringBuffer(text.length() + increase);
        while (end != INDEX_NOT_FOUND) {
            buf.append(text.substring(start, end)).append(replacement);
            start = end + replLength;
            if (--max == 0) {
                break;
            }
            end = text.indexOf(searchString, start);
        }
        buf.append(text.substring(start));
        return buf.toString();
    }

    /**
     * 匹配是否为数字
     *
     * @param str 可能为中文，也可能是-19162431.1254，不使用BigDecimal的话，变成-1.91624311254E7
     * @return
     * @author yutao
     * @date 2016年11月14日下午7:41:22
     */
    public static boolean isNumeric(String str) {
        // 该正则表达式可以匹配所有的数字 包括负数
        String regex = "-?[0-9]+(\\.[0-9]+)?";
        Pattern pattern = Pattern.compile(regex);
        String bigStr;
        try {
            bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            //异常 说明包含非数字。
            return false;
        }
        // matcher是全匹配
        Matcher isNum = pattern.matcher(bigStr);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * @param str
     * @return String
     * @throws
     * @Description: 首字母小写
     * @author JiangJY
     * @date 2016-1-13
     */
    public static String toLowerCaseFirstOne(String str) {
        if (Character.isLowerCase(str.charAt(0)))
            return str;
        char[] cs = str.toCharArray();
        if (91 > cs[0] && 64 < cs[0]) {
            cs[0] += 32;
        }
        return String.valueOf(cs);
    }

    /**
     * @param str
     * @return String
     * @throws
     * @Description: 首字母大写
     * @author JiangJY
     * @date 2016-1-13
     */
    public static String toUpperCaseFirstOne(String str) {
        if (Character.isUpperCase(str.charAt(0)))
            return str;
        char[] cs = str.toCharArray();
        if (123 > cs[0] && 96 < cs[0]) {
            cs[0] -= 32;
        }
        return String.valueOf(cs);
    }
}
