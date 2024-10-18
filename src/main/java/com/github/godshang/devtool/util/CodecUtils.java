package com.github.godshang.devtool.util;

import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CodecUtils {

    public static String urlEncode(String url) {
        return URLEncoder.encode(url, StandardCharsets.UTF_8);
    }

    public static String urlDecode(String url) {
        return URLDecoder.decode(url, StandardCharsets.UTF_8);
    }

    public static String base64EncodeToString(String string) {
        return base64EncodeToString(string.getBytes(StandardCharsets.UTF_8));
    }

    public static String base64EncodeToString(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String base64DecodeToString(String string) {
        return base64DecodeToString(string.getBytes(StandardCharsets.UTF_8));
    }

    public static String base64DecodeToString(byte[] input) {
        byte[] bytes = base64Decode(input);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static byte[] base64Encode(byte[] input) {
        return Base64.getEncoder().encode(input);
    }

    public static byte[] base64Decode(byte[] input) {
        return Base64.getDecoder().decode(input);
    }

    public static Image base64ToImage(String string) {
        byte[] bytes = base64Decode(string.getBytes(StandardCharsets.UTF_8));
        return new Image(new ByteArrayInputStream(bytes));
    }

}
