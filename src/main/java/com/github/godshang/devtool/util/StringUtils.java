package com.github.godshang.devtool.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class StringUtils {

    public static final String EMPTY = "";
    public static final String WHITESPACE = " \n\r\f\t";
    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isBlank(String string) {
        return string == null || string.isBlank();
    }

    public static boolean isNumberic(String string) {
        if (isEmpty(string)) {
            return false;
        } else {
            for (int i = 0, sz = string.length(); i < sz; ++i) {
                if (!Character.isDigit(string.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
    }

    public static String randomString(String baseString, int length) {
        if (isEmpty(baseString)) {
            return EMPTY;
        }
        final StringBuilder sb = new StringBuilder(length);

        if (length < 1) {
            length = 1;
        }
        int baseLength = baseString.length();
        for (int i = 0; i < length; i++) {
            int number = ThreadLocalRandom.current().nextInt(baseLength);
            sb.append(baseString.charAt(number));
        }
        return sb.toString();
    }

    public static String upperCase(String input) {
        return input.toUpperCase();
    }

    public static String lowerCase(String input) {
        return input.toLowerCase();
    }

    public static String camelCase(String input) {
        String[] words = input.split("[\\W_]+");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (i == 0) {
                word = word.isEmpty() ? word : word.toLowerCase();
            } else {
                word = word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
            }
            builder.append(word);
        }
        return builder.toString();
    }

    public static String capitalCase(String input) {
        return caseConvert(input, s -> capitalize(s), " ");
    }

    public static String dotCase(String input) {
        return caseConvert(input, s -> s, ".");
    }

    public static String pathCase(String input) {
        return caseConvert(input, s -> lowerCase(s), "/");
    }

    public static String snakeCase(String input) {
        return caseConvert(input, s -> lowerCase(s), "_");
    }

    public static String pascalCase(String input) {
        return caseConvert(input, s -> capitalize(s), "");
    }

    public static String constantCase(String input) {
        return caseConvert(input, s -> upperCase(s), "_");
    }

    public static String caseConvert(String input, Function<String, String> mapper, String delimiter) {
        String[] words = input.split("[\\W_]+");
        StringBuilder builder = new StringBuilder();
        Iterator<String> iter = Arrays.stream(words).iterator();
        if (iter.hasNext()) {
            builder.append(mapper.apply(iter.next()));
        }
        while (iter.hasNext()) {
            builder.append(delimiter);
            builder.append(mapper.apply(iter.next()));
        }
        return builder.toString().trim();
    }

    public static String capitalize(String inputString) {
        char firstLetter = inputString.charAt(0);
        char capitalFirstLetter = Character.toUpperCase(firstLetter);
        return capitalFirstLetter + inputString.substring(1);
    }


}
