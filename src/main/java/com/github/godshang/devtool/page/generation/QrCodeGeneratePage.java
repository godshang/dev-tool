package com.github.godshang.devtool.page.generation;

import com.github.godshang.devtool.page.AbstractPage;
import com.github.godshang.devtool.util.FXUtils;
import com.github.godshang.devtool.util.QrCodeUtils;
import com.github.godshang.devtool.util.StringUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class QrCodeGeneratePage extends AbstractPage {

    public static final String NAME = "QR code generater";

    private TextArea textArea;
    private Spinner<Integer> widthSpinner;
    private Spinner<Integer> heightSpinner;
    private Button generateButton;
    private ImageView imageView;

    public QrCodeGeneratePage() {
        super();
        initView();
        initListener();
    }

    @Override
    public void reset() {
        super.reset();
        generateButton.setOnAction(null);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void initView() {
        textArea = new TextArea();
        textArea.setPrefHeight(SPACING * 10);
        textArea.setMinHeight(SPACING * 10);
        textArea.setMaxHeight(SPACING * 10);
        HBox.setHgrow(textArea, Priority.ALWAYS);
        addNode(makeHBoxContainer("Text", SPACING * 10, textArea));

        widthSpinner = new Spinner<>(1, Integer.MAX_VALUE, 300);
        widthSpinner.setEditable(true);
        addNode(makeHBoxContainer("Width", SPACING * 10, widthSpinner));

        heightSpinner = new Spinner<>(1, Integer.MAX_VALUE, 300);
        heightSpinner.setEditable(true);
        addNode(makeHBoxContainer("Height", SPACING * 10, heightSpinner));

        generateButton = new Button("Generate");
        generateButton.setDefaultButton(true);
        addNode(makeHBoxContainer(generateButton));

        addNode(new Separator());

        imageView = new ImageView();
        addNode(imageView);
    }

    private void initListener() {
        generateButton.setOnAction(event -> generateQR());

        FXUtils.buildImageViewContextMenu(imageView);
    }

    private void generateQR() {
        String text = textArea.getText();
        if (StringUtils.isBlank(text)) {
            return;
        }
        int width = widthSpinner.getValue();
        int height = heightSpinner.getValue();

        try {
            WritableImage writableImage = QrCodeUtils.createQR(text, width, height);
            imageView.setImage(writableImage);
        } catch (Exception e) {
            popupNotification(e.getMessage(), false);
        }
    }
}
