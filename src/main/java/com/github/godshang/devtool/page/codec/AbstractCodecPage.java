package com.github.godshang.devtool.page.codec;

import atlantafx.base.theme.Styles;
import com.github.godshang.devtool.page.AbstractPage;
import com.github.godshang.devtool.util.CodecUtils;
import com.github.godshang.devtool.util.EscapeUtils;
import com.github.godshang.devtool.util.StringUtils;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;

public abstract class AbstractCodecPage extends AbstractPage {

    enum CodecEnum {
        BASE64, URL, HTML_ENTITY;
    }

    protected TextArea textArea;
    private Button encodeButton;
    private Button decodeButton;

    public AbstractCodecPage() {
        super();
        initView();
        initListener();
    }

    @Override
    public void reset() {
        super.reset();
        encodeButton.setOnAction(null);
        decodeButton.setOnAction(null);
    }

    protected abstract CodecEnum getCodecEnum();

    private void initView() {
        textArea = new TextArea();
        addNode(textArea);

        encodeButton = new Button("Encode");
        encodeButton.getStyleClass().add(Styles.ACCENT);
        decodeButton = new Button("Decode");
        decodeButton.getStyleClass().add(Styles.ACCENT);
        var buttonContainer = new HBox(SPACING);
        buttonContainer.getChildren().addAll(encodeButton, decodeButton);
        addNode(buttonContainer);
    }

    private void initListener() {
        encodeButton.setOnAction(event -> encode());
        decodeButton.setOnAction(event -> decode());
    }

    private void encode() {
        String content = textArea.getText();
        if (StringUtils.isBlank(content)) {
            return;
        }
        CodecEnum codecEnum = getCodecEnum();
        try {
            String encoded = StringUtils.EMPTY;
            switch (codecEnum) {
                case BASE64 -> encoded = CodecUtils.base64EncodeToString(content);
                case URL -> encoded = CodecUtils.urlEncode(content);
                case HTML_ENTITY -> encoded = EscapeUtils.escapeHtml(content);
            }
            textArea.setText(encoded);
        } catch (Exception e) {
            popupNotification(e.getMessage(), false);
        }
    }

    private void decode() {
        String content = textArea.getText();
        if (StringUtils.isBlank(content)) {
            return;
        }
        CodecEnum codecEnum = getCodecEnum();
        try {
            String decoded = StringUtils.EMPTY;
            switch (codecEnum) {
                case BASE64 -> decoded = CodecUtils.base64DecodeToString(content);
                case URL -> decoded = CodecUtils.urlDecode(content);
                case HTML_ENTITY -> decoded = EscapeUtils.unescapeHTML(content);
            }
            textArea.setText(decoded);
        } catch (Exception e) {
            popupNotification(e.getMessage(), false);
        }
    }
}
