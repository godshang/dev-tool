package com.github.godshang.devtool.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtils {

    public static String getExtensionName(File file) {
        if (file == null) {
            return StringUtils.EMPTY;
        }
        return getExtensionName(file.getName());
    }

    public static String getExtensionName(String fileName) {
        int idx = fileName.indexOf(".");
        if (idx > 0) {
            return fileName.substring(idx + 1);
        }
        return StringUtils.EMPTY;
    }

    public static byte[] readAllBytes(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }
}
