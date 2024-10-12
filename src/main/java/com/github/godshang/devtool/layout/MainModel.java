/* SPDX-License-Identifier: MIT */

package com.github.godshang.devtool.layout;

import com.github.godshang.devtool.page.Page;
import com.github.godshang.devtool.page.codec.Base64CodecPage;
import com.github.godshang.devtool.page.codec.HtmlEntityCodecPage;
import com.github.godshang.devtool.page.codec.URLCodecPage;
import com.github.godshang.devtool.page.converter.Base64ToImageConverterPage;
import com.github.godshang.devtool.page.converter.CaseConverterPage;
import com.github.godshang.devtool.page.converter.ColorConverterPage;
import com.github.godshang.devtool.page.converter.ImageToBase64ConverterPage;
import com.github.godshang.devtool.page.converter.IntegerBaseConverterPage;
import com.github.godshang.devtool.page.converter.JsonYamlConverterPage;
import com.github.godshang.devtool.page.converter.PropsYamlConverterPage;
import com.github.godshang.devtool.page.converter.TimestampConverterPage;
import com.github.godshang.devtool.page.crypto.AESPage;
import com.github.godshang.devtool.page.crypto.DESPage;
import com.github.godshang.devtool.page.crypto.HashTextPage;
import com.github.godshang.devtool.page.crypto.RSAKeyPairGeneraterPage;
import com.github.godshang.devtool.page.crypto.TripleDESPage;
import com.github.godshang.devtool.page.generation.ChineseIdGeneratePage;
import com.github.godshang.devtool.page.generation.CronGeneratePage;
import com.github.godshang.devtool.page.generation.QrCodeGeneratePage;
import com.github.godshang.devtool.page.generation.QrCodeParsePage;
import com.github.godshang.devtool.page.generation.RandomStringGeneratePage;
import com.github.godshang.devtool.page.generation.UuidGeneratePage;
import com.github.godshang.devtool.page.json.JsonDiffPage;
import com.github.godshang.devtool.page.json.JsonFormatPage;
import com.github.godshang.devtool.page.json.JsonToBeanPage;
import com.github.godshang.devtool.page.json.JsonToExcelPage;
import com.github.godshang.devtool.page.json.JsonViewPage;
import com.github.godshang.devtool.page.other.SqlFormatPage;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import org.kordamp.ikonli.boxicons.BoxiconsRegular;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainModel {

    public static final Class<? extends Page> DEFAULT_PAGE = JsonFormatPage.class;

    private static final Map<Class<? extends Page>, NavTree.Item> NAV_TREE = createNavItems();

    NavTree.Item getTreeItemForPage(Class<? extends Page> pageClass) {
        return NAV_TREE.getOrDefault(pageClass, NAV_TREE.get(DEFAULT_PAGE));
    }

//    List<NavTree.Item> findPages(String filter) {
//        return NAV_TREE.values().stream()
//            .filter(item -> item.getValue() != null && item.getValue().matches(filter))
//            .toList();
//    }

//    public MainModel() {
//        DefaultEventBus.getInstance().subscribe(NavEvent.class, e -> navigate(e.getPage()));
//    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    // ~
    private final ReadOnlyObjectWrapper<Class<? extends Page>> selectedPage = new ReadOnlyObjectWrapper<>();

    public ReadOnlyObjectProperty<Class<? extends Page>> selectedPageProperty() {
        return selectedPage.getReadOnlyProperty();
    }

    // ~
    private final ReadOnlyObjectWrapper<NavTree.Item> navTree = new ReadOnlyObjectWrapper<>(createTree());

    public ReadOnlyObjectProperty<NavTree.Item> navTreeProperty() {
        return navTree.getReadOnlyProperty();
    }

    private NavTree.Item createTree() {
        var jsonTool = NavTree.Item.group("JSON", new FontIcon(BoxiconsRegular.CODE_CURLY));
        jsonTool.getChildren().setAll(
                NAV_TREE.get(JsonFormatPage.class),
                NAV_TREE.get(JsonViewPage.class),
                NAV_TREE.get(JsonToExcelPage.class),
                NAV_TREE.get(JsonToBeanPage.class),
                NAV_TREE.get(JsonDiffPage.class)
        );
//        jsonTool.setExpanded(true);

        var converterTool = NavTree.Item.group("Converter", new FontIcon(Material2AL.APPS));
        converterTool.getChildren().setAll(
                NAV_TREE.get(JsonYamlConverterPage.class),
                NAV_TREE.get(PropsYamlConverterPage.class),
                NAV_TREE.get(TimestampConverterPage.class),
                NAV_TREE.get(IntegerBaseConverterPage.class),
                NAV_TREE.get(CaseConverterPage.class),
                NAV_TREE.get(ColorConverterPage.class),
                NAV_TREE.get(Base64ToImageConverterPage.class),
                NAV_TREE.get(ImageToBase64ConverterPage.class)
        );

        var generateTool = NavTree.Item.group("Generation", new FontIcon(Material2AL.BOOK));
        generateTool.getChildren().setAll(
                NAV_TREE.get(CronGeneratePage.class),
                NAV_TREE.get(UuidGeneratePage.class),
                NAV_TREE.get(RandomStringGeneratePage.class),
                NAV_TREE.get(ChineseIdGeneratePage.class),
                NAV_TREE.get(QrCodeGeneratePage.class),
                NAV_TREE.get(QrCodeParsePage.class)
        );

        var codecTool = NavTree.Item.group("Codec", new FontIcon(Material2AL.CODE));
        codecTool.getChildren().setAll(
                NAV_TREE.get(Base64CodecPage.class),
                NAV_TREE.get(URLCodecPage.class),
                NAV_TREE.get(HtmlEntityCodecPage.class)
        );

        var encryptTool = NavTree.Item.group("Entrypt", new FontIcon(Material2AL.ENHANCED_ENCRYPTION));
        encryptTool.getChildren().setAll(
                NAV_TREE.get(HashTextPage.class),
                NAV_TREE.get(AESPage.class),
                NAV_TREE.get(DESPage.class),
                NAV_TREE.get(TripleDESPage.class),
                NAV_TREE.get(RSAKeyPairGeneraterPage.class)
        );

        var otherTool = NavTree.Item.group("Other", new FontIcon(Material2AL.DEVELOPER_BOARD));
        otherTool.getChildren().setAll(
                // NAV_TREE.get(TestPage.class),
                NAV_TREE.get(SqlFormatPage.class)
        );

        var root = NavTree.Item.root();
        root.getChildren().setAll(
                jsonTool,
                converterTool,
                generateTool,
                codecTool,
                encryptTool,
                otherTool
        );

        return root;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Nav Tree                                                              //
    ///////////////////////////////////////////////////////////////////////////

    public static Map<Class<? extends Page>, NavTree.Item> createNavItems() {
        var map = new HashMap<Class<? extends Page>, NavTree.Item>();
        // json tool
        map.put(JsonFormatPage.class, NavTree.Item.page(JsonFormatPage.NAME, JsonFormatPage.class));
        map.put(JsonViewPage.class, NavTree.Item.page(JsonViewPage.NAME, JsonViewPage.class));
        map.put(JsonToExcelPage.class, NavTree.Item.page(JsonToExcelPage.NAME, JsonToExcelPage.class));
        map.put(JsonToBeanPage.class, NavTree.Item.page(JsonToBeanPage.NAME, JsonToBeanPage.class));
        map.put(JsonDiffPage.class, NavTree.Item.page(JsonDiffPage.NAME, JsonDiffPage.class));
        // converter tool
        map.put(JsonYamlConverterPage.class, NavTree.Item.page(JsonYamlConverterPage.NAME, JsonYamlConverterPage.class));
        map.put(PropsYamlConverterPage.class, NavTree.Item.page(PropsYamlConverterPage.NAME, PropsYamlConverterPage.class));
        map.put(TimestampConverterPage.class, NavTree.Item.page(TimestampConverterPage.NAME, TimestampConverterPage.class));
        map.put(IntegerBaseConverterPage.class, NavTree.Item.page(IntegerBaseConverterPage.NAME, IntegerBaseConverterPage.class));
        map.put(CaseConverterPage.class, NavTree.Item.page(CaseConverterPage.NAME, CaseConverterPage.class));
        map.put(ColorConverterPage.class, NavTree.Item.page(ColorConverterPage.NAME, ColorConverterPage.class));
        map.put(Base64ToImageConverterPage.class, NavTree.Item.page(Base64ToImageConverterPage.NAME, Base64ToImageConverterPage.class));
        map.put(ImageToBase64ConverterPage.class, NavTree.Item.page(ImageToBase64ConverterPage.NAME, ImageToBase64ConverterPage.class));
        // generation tool
        map.put(CronGeneratePage.class, NavTree.Item.page(CronGeneratePage.NAME, CronGeneratePage.class));
        map.put(UuidGeneratePage.class, NavTree.Item.page(UuidGeneratePage.NAME, UuidGeneratePage.class));
        map.put(RandomStringGeneratePage.class, NavTree.Item.page(RandomStringGeneratePage.NAME, RandomStringGeneratePage.class));
        map.put(ChineseIdGeneratePage.class, NavTree.Item.page(ChineseIdGeneratePage.NAME, ChineseIdGeneratePage.class));
        map.put(QrCodeGeneratePage.class, NavTree.Item.page(QrCodeGeneratePage.NAME, QrCodeGeneratePage.class));
        map.put(QrCodeParsePage.class, NavTree.Item.page(QrCodeParsePage.NAME, QrCodeParsePage.class));
        // codec tool
        map.put(Base64CodecPage.class, NavTree.Item.page(Base64CodecPage.NAME, Base64CodecPage.class));
        map.put(URLCodecPage.class, NavTree.Item.page(URLCodecPage.NAME, URLCodecPage.class));
        map.put(HtmlEntityCodecPage.class, NavTree.Item.page(HtmlEntityCodecPage.NAME, HtmlEntityCodecPage.class));
        // encrypt tool
        map.put(HashTextPage.class, NavTree.Item.page(HashTextPage.NAME, HashTextPage.class));
        map.put(AESPage.class, NavTree.Item.page(AESPage.NAME, AESPage.class));
        map.put(DESPage.class, NavTree.Item.page(DESPage.NAME, DESPage.class));
        map.put(TripleDESPage.class, NavTree.Item.page(TripleDESPage.NAME, TripleDESPage.class));
        map.put(RSAKeyPairGeneraterPage.class, NavTree.Item.page(RSAKeyPairGeneraterPage.NAME, RSAKeyPairGeneraterPage.class));
        // other tool
        map.put(SqlFormatPage.class, NavTree.Item.page(SqlFormatPage.NAME, SqlFormatPage.class));
//        map.put(TestPage.class, NavTree.Item.page(TestPage.NAME, TestPage.class));
        return map;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Commands                                                              //
    ///////////////////////////////////////////////////////////////////////////

    public void navigate(Class<? extends Page> page) {
        selectedPage.set(Objects.requireNonNull(page));
    }
}
