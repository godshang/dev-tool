package com.github.godshang.devtool.page.converter;

import com.github.godshang.devtool.page.AbstractPage;
import com.github.godshang.devtool.util.CodecUtils;
import com.github.godshang.devtool.util.FXUtils;
import com.github.godshang.devtool.util.FileUtils;
import com.github.godshang.devtool.util.StringUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.File;

public class ImageToBase64ConverterPage extends AbstractPage {

    public static final String NAME = "Image to base64 converter";

    private Button browserButton;
    private TextArea textArea;
    private ImageView imageView;
    private VBox resultContainer;

    public ImageToBase64ConverterPage() {
        super();
        initView();
        initListener();
    }

    @Override
    public void reset() {
        super.reset();
        browserButton.setOnAction(null);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void initView() {
        addNode(new Label("Convert image file into its base64 representation"));

        browserButton = new Button("Browse");
        browserButton.setDefaultButton(true);
        var buttonContainer = new HBox(SPACING);
        buttonContainer.getChildren().addAll(browserButton);
        addNode(buttonContainer);

        textArea = new TextArea();
        textArea.setPrefHeight(SPACING * 10);
        textArea.setMinHeight(SPACING * 10);
        textArea.setMaxHeight(SPACING * 10);
        addNode(textArea);

        imageView = new ImageView();
        addNode(imageView);

        resultContainer = new VBox(SPACING);
        resultContainer.getChildren().addAll(textArea, new Separator(), imageView);
        resultContainer.setVisible(false);
        addNode(resultContainer);
    }

    private void initListener() {
        browserButton.setOnAction(event -> displayImage());
    }

    private void displayImage() {
        try {
            doDisplayImage();
        } catch (Exception e) {
            popupNotification(e.getMessage(), false);
        }
    }

    @SneakyThrows
    private void doDisplayImage() {
        resultContainer.setVisible(false);
        imageView.setImage(null);
        textArea.setText(StringUtils.EMPTY);

        File file = FXUtils.chooseFileToOpen(new String[][]{
                new String[]{"Image Files", "*.png"},
                new String[]{"Image Files", "*.jpg"},
                new String[]{"All Files", "*.*"},
        });
        if (file == null) {
            return;
        }

        byte[] bytes = FileUtils.readAllBytes(file);
        if (bytes == null) {
            return;
        }

        resultContainer.setVisible(true);
        imageView.setImage(new Image(new ByteArrayInputStream(bytes)));
        textArea.setText(CodecUtils.base64EncodeToString(bytes));
    }

}
