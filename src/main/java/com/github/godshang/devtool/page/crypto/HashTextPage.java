package com.github.godshang.devtool.page.crypto;

import atlantafx.base.layout.InputGroup;
import com.github.godshang.devtool.page.AbstractPage;
import com.github.godshang.devtool.util.FXUtils;
import com.github.godshang.devtool.util.HashUtils;
import com.github.godshang.devtool.util.StringUtils;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HashTextPage extends AbstractPage {

    public static final String NAME = "Hash text";

    enum Algorithm {
        MD5, SHA1, SHA256, SHA224, SHA384, SHA512;

    }

    record NodeWrapper(Algorithm algorithm, Label label, TextField textField, Button copyButton) {

        InputGroup makeInputGroup() {
            return new InputGroup(label, textField, copyButton);
        }

    }

    private TextField inputTextField;
    private List<NodeWrapper> nodeList;

    private ChangeListener<String> inputChangeListener = (observable, oldValue, newValue) -> hash();

    public HashTextPage() {
        super();
        initView();
        initListener();
        hash();
    }

    @Override
    public void reset() {
        super.reset();
        inputTextField.textProperty().removeListener(inputChangeListener);
        nodeList.forEach(e -> {
            e.copyButton().setOnAction(null);
        });
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void initView() {
        addNode(new Label("Hash a text string using the function you need : MD5, SHA1, SHA256, SHA224, SHA384 or SHA512."));

        var inputLabel = new Label("Your text to hash");
        inputLabel.setMinWidth(SPACING * 10);
        inputTextField = new TextField("Hello World");
        HBox.setHgrow(inputTextField, Priority.ALWAYS);
        var inputGroup = new InputGroup(inputLabel, inputTextField);
        addNode(inputGroup);

        addNode(new Separator());

        nodeList = makeAllAlgorithmNode();
        var resultContainer = new VBox(SPACING);
        resultContainer.getChildren().addAll(nodeList.stream().map(NodeWrapper::makeInputGroup).collect(Collectors.toList()));
        addNode(resultContainer);
    }

    private List<NodeWrapper> makeAllAlgorithmNode() {
        List<NodeWrapper> resultList = new ArrayList<>();
        for (Algorithm algorithm : Algorithm.values()) {
            var label = new Label(algorithm.name());
            label.setMinWidth(SPACING * 10);

            var textField = new TextField();
            HBox.setHgrow(textField, Priority.ALWAYS);

            var copyButton = new Button("", new FontIcon(Material2AL.CONTENT_COPY));
            resultList.add(new NodeWrapper(algorithm, label, textField, copyButton));
        }
        return resultList;
    }

    private void initListener() {
        inputTextField.textProperty().addListener(inputChangeListener);

        nodeList.forEach(e -> {
            e.copyButton().setOnAction(event -> {
                String text = e.textField().getText();
                if (!StringUtils.isBlank(text)) {
                    FXUtils.putClipboard(text);
                }
            });
        });
    }

    private void hash() {
        String input = inputTextField.getText();
        if (StringUtils.isBlank(input)) {
            return;
        }
        for (NodeWrapper nodeWrapper : nodeList) {
            try {
                String output = doHash(input, nodeWrapper.algorithm());
                nodeWrapper.textField().setText(output);
            } catch (NoSuchAlgorithmException e) {
                popupNotification(e.getMessage(), false);
            }
        }
    }

    private String doHash(String input, Algorithm algorithm) throws NoSuchAlgorithmException {
        String output = null;
        switch (algorithm) {
            case MD5 -> output = HashUtils.md5(input);
            case SHA1 -> output = HashUtils.sha1(input);
            case SHA256 -> output = HashUtils.sha256(input);
            case SHA224 -> output = HashUtils.sha224(input);
            case SHA384 -> output = HashUtils.sha384(input);
            case SHA512 -> output = HashUtils.sha512(input);
        }
        return output;
    }
}
