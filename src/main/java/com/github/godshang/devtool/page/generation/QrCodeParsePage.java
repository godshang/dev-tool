package com.github.godshang.devtool.page.generation;

import com.github.godshang.devtool.page.AbstractPage;
import com.github.godshang.devtool.util.FXUtils;
import com.github.godshang.devtool.util.FileUtils;
import com.github.godshang.devtool.util.QrCodeUtils;
import com.github.godshang.devtool.util.StringUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Objects;

public class QrCodeParsePage extends AbstractPage {

    public static final String NAME = "QR code parser";

    private Button browserButton;
    private Button screenCaptureButton;
    private ImageView imageView;
    private TextArea textArea;
    private VBox resultContainer;

    public QrCodeParsePage() {
        super();
        initView();
        initListener();
    }

    @Override
    public void reset() {
        super.reset();
        browserButton.setOnAction(null);
        screenCaptureButton.setOnAction(null);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void initView() {
        browserButton = new Button("Browse");
        browserButton.setDefaultButton(true);
        screenCaptureButton = new Button("Screen capture");
        addNode(makeHBoxContainer(browserButton, screenCaptureButton));

        addNode(new Separator());

        imageView = new ImageView();
        textArea = new TextArea();
        textArea.setPrefHeight(SPACING * 10);
        textArea.setMinHeight(SPACING * 10);
        textArea.setMaxHeight(SPACING * 10);
        resultContainer = new VBox(SPACING);
        resultContainer.getChildren().addAll(imageView, textArea);
        resultContainer.setVisible(false);
        addNode(resultContainer);
    }

    private void initListener() {
        browserButton.setOnAction(event -> parse());
        screenCaptureButton.setOnAction(event -> screenShot());
    }

    private void parse() {
        try {
            doParse();
        } catch (Exception e) {
            popupNotification(Objects.toString(e.getMessage(), "QR code parse error"), false);
            resultContainer.setVisible(false);
        }
    }

    @SneakyThrows
    private void doParse() {
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
        textArea.setText(QrCodeUtils.readQR(file));
    }

    private void screenShot() {
        resultContainer.setVisible(false);
        imageView.setImage(null);
        textArea.setText(StringUtils.EMPTY);

        FXUtils.screenCapture(this::displayScreenCapture);
    }

    private void displayScreenCapture(WritableImage writableImage) {
        try {
            doDisplayScreenCapture(writableImage);
        } catch (Exception e) {
            popupNotification(Objects.toString(e.getMessage(), "QR code parse error"), false);
            resultContainer.setVisible(false);
        }
    }

    @SneakyThrows
    private void doDisplayScreenCapture(WritableImage writableImage) {
        resultContainer.setVisible(true);
        imageView.setImage(writableImage);
        textArea.setText(QrCodeUtils.readQR(writableImage));
    }

}
