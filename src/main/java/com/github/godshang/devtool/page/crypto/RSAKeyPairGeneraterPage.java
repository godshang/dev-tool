package com.github.godshang.devtool.page.crypto;

import com.github.godshang.devtool.page.AbstractPage;
import com.github.godshang.devtool.util.RSAKeyUtils;
import com.github.godshang.devtool.util.StringUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.security.NoSuchAlgorithmException;

public class RSAKeyPairGeneraterPage extends AbstractPage {

    public static final String NAME = "RSA key pair generater";

    private Button generateButton;
    private TextField bitsTextField;
    private TextArea privateKeyTextArea;
    private TextArea publicKeyTextArea;
    private HBox container;

    public RSAKeyPairGeneraterPage() {
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
        addNode(new Label("Generate a new random RSA private and public pem certificate key pair."));

        bitsTextField = new TextField("2048");
        generateButton = new Button("Generate");
        generateButton.setDefaultButton(true);
        var optionContainer = new HBox(SPACING);
        optionContainer.getChildren().addAll(new Label("Bits : "), bitsTextField, generateButton);
        optionContainer.setAlignment(Pos.BASELINE_LEFT);
        addNode(optionContainer);

        privateKeyTextArea = new TextArea();
        publicKeyTextArea = new TextArea();
        var publicPane = new VBox(SPACING);
        publicPane.getChildren().addAll(new Label("Public Key"), publicKeyTextArea);
        var privatePane = new VBox(SPACING);
        privatePane.getChildren().addAll(new Label("Private Key"), privateKeyTextArea);
        container = new HBox(SPACING);
        container.getChildren().addAll(publicPane, privatePane);
        addNode(container);

        VBox.setVgrow(privateKeyTextArea, Priority.ALWAYS);
        VBox.setVgrow(publicKeyTextArea, Priority.ALWAYS);
        VBox.setVgrow(container, Priority.ALWAYS);
    }

    private void initListener() {
        container.widthProperty().addListener((observable, oldValue, newValue) -> {
            privateKeyTextArea.setPrefWidth(newValue.doubleValue() / 2);
            publicKeyTextArea.setPrefWidth(newValue.doubleValue() / 2);
        });
        generateButton.setOnAction(event -> genereateRSAKey());
    }

    private void genereateRSAKey() {
        String bits = bitsTextField.getText();
        if (StringUtils.isBlank(bits) || !StringUtils.isNumberic(bits)) {

        }
        int keySize = Integer.parseInt(bits);
        try {
            var keys = RSAKeyUtils.generate(keySize);
            privateKeyTextArea.setText(keys.privateKey());
            publicKeyTextArea.setText(keys.publicKey());
        } catch (NoSuchAlgorithmException e) {
            popupNotification(e.getMessage(), false);
        }
    }
}
