package com.github.godshang.devtool.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 公民身份号码是特征组合码，由十七位数字本体码和一位校验码组成。排列顺序从左至右依次为：六位数字地址码，八位数字出生日期码，三位数字顺序码和一位数字校验码。
 */
public class ChineseIdUtils {

    public static List<String> generate(String code, String birthday, int sex, int num) {

        List<String> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < num; i++) {
            builder.setLength(0);
            // 行政区划代码
            builder.append(code);
            // 出生日期
            builder.append(birthday != null ? birthday : generateBirthday());
            // 顺序吗
            builder.append(generateSequenceCode(sex));
            // 校验码
            builder.append(calculateCheckCode(builder.toString()));
            list.add(builder.toString());
        }
        return list;
    }

    // 生成随机日期
    private static String generateBirthday() {
        Random rand = new Random();
        int year = rand.nextInt(60) + 1960; // 生成1960到2020年的随机年份
        int month = rand.nextInt(12) + 1; // 生成1到12月的随机月份
        int day = rand.nextInt(28) + 1; // 生成1到28日的随机日期
        return String.format("%04d%02d%02d", year, month, day);
    }

    private static String generateSequenceCode(int sex) {
        if (sex == 0) { // 女性
            return generateRandomOdd();
        } else if (sex == 1) { // 男性
            return generateRandomEven();
        } else {
            return generateSequenceCode();
        }
    }

    // 生成顺序码
    private static String generateSequenceCode() {
        Random rand = new Random();
        int sequenceCode = rand.nextInt(1000); // 生成0到999的随机数
        return String.format("%03d", sequenceCode);
    }

    // 生成0到999的随机偶数
    public static String generateRandomEven() {
        Random rand = new Random();
        int randomNumber = rand.nextInt(500) * 2; // 生成0到499的随机整数，并乘以2确保是偶数
        return String.format("%03d", randomNumber);
    }

    // 生成0到999的随机奇数
    public static String generateRandomOdd() {
        Random rand = new Random();
        int randomNumber = rand.nextInt(500) * 2 + 1; // 生成0到499的随机整数，乘以2并加1确保是奇数
        return String.format("%03d", randomNumber);
    }

    // 计算校验码
    private static char calculateCheckCode(String idWithoutCheckCode) {
        char[] chars = idWithoutCheckCode.toCharArray();
        int[] weight = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] checkCode = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

        int sum = 0;
        for (int i = 0; i < chars.length; i++) {
            sum += (chars[i] - '0') * weight[i];
        }

        return checkCode[sum % 11];
    }
}
