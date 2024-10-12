package com.github.godshang.devtool.util;

import java.io.File;

public class Test {

    public static void main(String[] args) throws Exception {
//        byte[] input = "Lorem ipsum dolor sit amet".getBytes(StandardCharsets.UTF_8);
//        byte[] secretKey = "abcdefghijklmnopqrstuvwx".getBytes(StandardCharsets.UTF_8);
//
//        byte[] encrypted = CryptoUtils.encrypt(CryptoUtils.CryptoOption.builder()
//                .input(input)
//                .secretKey(secretKey)
//                .algorithm(CryptoUtils.Algorithm.DESede)
//                .mode(CryptoUtils.Mode.ECB)
//                .padding(CryptoUtils.Padding.PKCS5Padding)
//                .build());
//        System.out.println(CodecUtils.base64Encode(encrypted));
//
//        byte[] decrypted = CryptoUtils.decrypt(CryptoUtils.CryptoOption.builder()
//                .input(encrypted)
//                .secretKey(secretKey)
//                .algorithm(CryptoUtils.Algorithm.AES)
//                .mode(CryptoUtils.Mode.ECB)
//                .padding(CryptoUtils.Padding.PKCS5Padding)
//                .build());
//        System.out.println(new String(decrypted, StandardCharsets.UTF_8));

//        double[] cmyk = ColorUtils.rgbToCmyk(156, 53, 53);
//        Arrays.stream(cmyk).forEach(System.out::println);
//
//        int[] rgb = ColorUtils.cmykToRgb(cmyk[0], cmyk[1], cmyk[2], cmyk[3]);
//        Arrays.stream(rgb).forEach(System.out::println);

//        Color color = Color.rgb(128, 255, 100);
//        System.out.println(ColorUtils.hex(color));
//        System.out.println(ColorUtils.rgb(color));
//        System.out.println(ColorUtils.cmyk(color));
//        System.out.println(ColorUtils.hsb(color));
//        System.out.println(ColorUtils.hsl(color));
//
//        System.out.println(ColorUtils.parse("hex(#80ff64)"));
//        System.out.println(ColorUtils.parse("rgb(128, 255, 100)"));
//        System.out.println(ColorUtils.parse("cmyk(49.80, 0.00, 60.78, 0.00)"));
//        System.out.println(ColorUtils.parse("hsb(109.16, 60.78, 100.00)"));
//        System.out.println(ColorUtils.parse("hsl(109.16, 100.00, 69.61)"));

//        QrCodeUtils.createQR("Hello World", new File("D:\\output.png"), 300, 300);
        try {
            System.out.println(QrCodeUtils.readQR(new File("D:\\无标题.png")));
            // System.out.println(QrCodeUtils.readQR(new File("D:\\output.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // String json1 = """
        //         {"age": 18, "classes":[123]}
        //         """;
        // String json2 = """
        //         {"name": "alan", "classes":[456]}
        //         """;
        // System.out.println(MapperUtils.diffJson(json1, json2));
    }
}
