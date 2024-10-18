package com.github.godshang.devtool.page.crypto;

import atlantafx.base.layout.InputGroup;
import atlantafx.base.theme.Styles;
import com.github.godshang.devtool.page.AbstractPage;
import com.github.godshang.devtool.util.CodecUtils;
import com.github.godshang.devtool.util.CryptoUtils;
import com.github.godshang.devtool.util.HashUtils;
import com.github.godshang.devtool.util.StringUtils;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

public abstract class AbstractCryptoPage extends AbstractPage {

    enum Format {
        Base64, Hex
    }

    protected TextArea inputTextArea;
    protected TextArea outputTextArea;
    private Button encryptButton;
    private Button decryptButton;

    private TextField secretKeyTextField;
    private TextField ivTextField;
    private ChoiceBox<CryptoUtils.Mode> modeChoiceBox;
    private ChoiceBox<CryptoUtils.Padding> paddingChoiceBox;
    private ChoiceBox<Format> formatChoiceBox;

    public AbstractCryptoPage() {
        super();
        initView();
        initListener();
    }

    @Override
    public void reset() {
        super.reset();
        encryptButton.setOnAction(null);
        decryptButton.setOnAction(null);
    }

    protected abstract CryptoUtils.Algorithm getAlgorithm();

    private void initView() {
        inputTextArea = new TextArea();
        inputTextArea.setPromptText("Input ...");
        addNode(inputTextArea);

        secretKeyTextField = new TextField();
        ivTextField = new TextField();
        modeChoiceBox = new ChoiceBox<>();
        modeChoiceBox.getItems().addAll(CryptoUtils.Mode.values());
        modeChoiceBox.getSelectionModel().selectFirst();
        paddingChoiceBox = new ChoiceBox<>();
        paddingChoiceBox.getItems().addAll(CryptoUtils.Padding.values());
        paddingChoiceBox.getSelectionModel().select(CryptoUtils.Padding.PKCS5Padding);
        formatChoiceBox = new ChoiceBox<>();
        formatChoiceBox.getItems().addAll(Format.values());
        formatChoiceBox.getSelectionModel().selectFirst();
        var inputContainer = new HBox(SPACING);
        inputContainer.getChildren().addAll(
                makeInputGroup("Secret", secretKeyTextField),
                makeInputGroup("IV", ivTextField),
                makeInputGroup("Format", formatChoiceBox)
        );
        addNode(inputContainer);
        var optionContainer = new HBox(SPACING);
        optionContainer.getChildren().addAll(
                makeInputGroup("Mode", modeChoiceBox),
                makeInputGroup("Padding", paddingChoiceBox)
        );
        addNode(optionContainer);

        encryptButton = new Button("Encrypt");
        encryptButton.getStyleClass().add(Styles.ACCENT);
        decryptButton = new Button("Decrypt");
        decryptButton.getStyleClass().add(Styles.ACCENT);
        var buttonContainer = new HBox(SPACING);
        buttonContainer.getChildren().addAll(encryptButton, decryptButton);
        addNode(buttonContainer);

        addNode(new Separator());

        outputTextArea = new TextArea();
        outputTextArea.setPromptText("Output ...");
        addNode(outputTextArea);
    }

    private InputGroup makeInputGroup(String labelText, Control control) {
        var label = new Label(labelText);
        label.setMinWidth(SPACING * 10);
        return new InputGroup(label, control);
    }

    private void initListener() {
        encryptButton.setOnAction(event -> encrypt());
        decryptButton.setOnAction(event -> decrypt());
    }

    private void encrypt() {
        String content = inputTextArea.getText();
        String secret = secretKeyTextField.getText();
        if (StringUtils.isBlank(content) || StringUtils.isBlank(secret)) {
            return;
        }

        byte[] input = content.getBytes(StandardCharsets.UTF_8);
        byte[] secretKey = secret.getBytes(StandardCharsets.UTF_8);
        byte[] iv = null;
        if (!StringUtils.isBlank(ivTextField.getText())) {
            iv = ivTextField.getText().getBytes(StandardCharsets.UTF_8);
        }
        CryptoUtils.Algorithm algorithm = getAlgorithm();
        CryptoUtils.Mode mode = modeChoiceBox.getSelectionModel().getSelectedItem();
        CryptoUtils.Padding padding = paddingChoiceBox.getSelectionModel().getSelectedItem();
        byte[] encrypted = null;
        try {
            encrypted = CryptoUtils.encrypt(CryptoUtils.CryptoOption.builder()
                    .input(input).secretKey(secretKey).iv(iv)
                    .algorithm(algorithm).mode(mode).padding(padding)
                    .build());
        } catch (Exception e) {
            popupNotification(e.getMessage(), false);
        }

        if (encrypted != null) {
            Format format = formatChoiceBox.getSelectionModel().getSelectedItem();
            switch (format) {
                case Base64 -> outputTextArea.setText(CodecUtils.base64EncodeToString(encrypted));
                case Hex -> outputTextArea.setText(HashUtils.printHexBinary(encrypted));
            }
        }
    }

    private void decrypt() {
        String content = inputTextArea.getText();
        String secret = secretKeyTextField.getText();
        if (StringUtils.isBlank(content) || StringUtils.isBlank(secret)) {
            return;
        }

        byte[] secretKey = secret.getBytes(StandardCharsets.UTF_8);
        byte[] input = null;
        byte[] iv = null;
        if (!StringUtils.isBlank(ivTextField.getText())) {
            iv = ivTextField.getText().getBytes(StandardCharsets.UTF_8);
        }
        Format format = formatChoiceBox.getSelectionModel().getSelectedItem();
        switch (format) {
            case Base64 -> input = CodecUtils.base64Decode(content.getBytes(StandardCharsets.UTF_8));
            case Hex -> input = HexFormat.of().parseHex(content);
        }

        CryptoUtils.Algorithm algorithm = getAlgorithm();
        CryptoUtils.Mode mode = modeChoiceBox.getSelectionModel().getSelectedItem();
        CryptoUtils.Padding padding = paddingChoiceBox.getSelectionModel().getSelectedItem();
        byte[] decrypted = null;
        try {
            decrypted = CryptoUtils.decrypt(CryptoUtils.CryptoOption.builder()
                    .input(input).secretKey(secretKey)
                    .algorithm(algorithm).mode(mode).padding(padding)
                    .build());
        } catch (Exception e) {
            popupNotification(e.getMessage(), false);
        }

        if (decrypted != null) {
            outputTextArea.setText(new String(decrypted, StandardCharsets.UTF_8));
        }
    }
}
