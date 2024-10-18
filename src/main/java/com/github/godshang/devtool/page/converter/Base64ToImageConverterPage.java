package com.github.godshang.devtool.page.converter;

import com.github.godshang.devtool.page.AbstractPage;
import com.github.godshang.devtool.util.CodecUtils;
import com.github.godshang.devtool.util.FXUtils;
import com.github.godshang.devtool.util.StringUtils;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;


public class Base64ToImageConverterPage extends AbstractPage {

    public static final String NAME = "Base64 to image converter";

    private TextArea textArea;
    private ImageView imageView;

    private ChangeListener<String> textAreaChangeListener = (observable, oldValue, newValue) -> displayImage(newValue);

    public Base64ToImageConverterPage() {
        super();
        initView();
        initListener();
    }

    @Override
    public void reset() {
        super.reset();
        textArea.textProperty().removeListener(textAreaChangeListener);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void initView() {
        addNode(new Label("Convert base64 string into an image file"));

        textArea = new TextArea();
        textArea.setPromptText("Paste Base64 string here ...");
        textArea.setPrefHeight(SPACING * 10);
        textArea.setMinHeight(SPACING * 10);
        textArea.setMaxHeight(SPACING * 10);
        addNode(textArea);
        addNode(new Separator());

        imageView = new ImageView();
        addNode(imageView);
    }

    private void initListener() {
        textArea.textProperty().addListener(textAreaChangeListener);

        FXUtils.buildImageViewContextMenu(imageView);
    }

    private void displayImage(String base64) {
        if (StringUtils.isBlank(base64)) {
            imageView.setImage(null);
            return;
        }
        try {
            imageView.setImage(CodecUtils.base64ToImage(base64));
        } catch (Exception e) {
            imageView.setImage(null);
            popupNotification(e.getMessage(), false);
        }
    }
}
