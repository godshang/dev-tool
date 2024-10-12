/* SPDX-License-Identifier: MIT */

module godshang.devtool {

    requires atlantafx.base;

    requires java.compiler;
    requires java.desktop;
    requires java.naming;
    requires java.prefs;
    requires javafx.web;
    requires javafx.swing;
    requires jdk.zipfs;
    requires jdk.charsets;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.feather;
    requires org.kordamp.ikonli.material2;
    requires org.kordamp.ikonli.boxicons;

    requires org.slf4j;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires com.fasterxml.jackson.dataformat.javaprop;
    requires poi;
    requires poi.ooxml;
    requires poi.ooxml.schemas;
    requires com.squareup.javapoet;
    requires static lombok;
    requires com.cronutils;
    requires commons.math3;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires json.diff;

    exports com.github.godshang.devtool;
    exports com.github.godshang.devtool.layout;
    exports com.github.godshang.devtool.page;
    exports com.github.godshang.devtool.util;
    exports com.github.godshang.devtool.common;

    // resources
    opens assets.styles;
    exports com.github.godshang.devtool.page.codec;
    exports com.github.godshang.devtool.page.converter;
    exports com.github.godshang.devtool.page.json;
    exports com.github.godshang.devtool.page.generation;
    exports com.github.godshang.devtool.page.other;
}
