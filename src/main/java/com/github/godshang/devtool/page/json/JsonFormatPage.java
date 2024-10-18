package com.github.godshang.devtool.page.json;

import atlantafx.base.controls.Message;
import atlantafx.base.theme.Styles;
import com.github.godshang.devtool.common.Result;
import com.github.godshang.devtool.page.AbstractPage;
import com.github.godshang.devtool.util.MapperUtils;
import com.github.godshang.devtool.util.StringUtils;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

import java.util.Objects;

public class JsonFormatPage extends AbstractPage {

    public static final String NAME = "JSON formatter";

    private Button formatBtn;
    private Button compressBtn;
    private Button escapeBtn;
    private Button unEscapeBtn;
    private Button clearBtn;
    private TextArea textArea;
    private Message message;

    public JsonFormatPage() {
        super();
        initView();
        initListener();
    }

    @Override
    public void reset() {
        super.reset();
        formatBtn.setOnAction(null);
        compressBtn.setOnAction(null);
        escapeBtn.setOnAction(null);
        unEscapeBtn.setOnAction(null);
        clearBtn.setOnAction(null);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void initView() {
        formatBtn = new Button("Format");
        formatBtn.setDefaultButton(true);
        compressBtn = new Button("Compress");
        escapeBtn = new Button("Escape");
        unEscapeBtn = new Button("UnEscape");
        clearBtn = new Button("Clear");
        addNode(makeHBoxContainer(formatBtn, compressBtn, escapeBtn, unEscapeBtn, clearBtn));

        textArea = new TextArea();
        textArea.setPromptText("Paste JSON here ...");
        VBox.setVgrow(textArea, Priority.ALWAYS);
        addNode(textArea);

        message = new Message("Output", "", new FontIcon(Material2OutlinedAL.CHECK_CIRCLE_OUTLINE));
        message.setVisible(false);
        addNode(message);
    }

    private void initListener() {
        formatBtn.setOnAction(event -> formatJson());
        compressBtn.setOnAction(event -> compressJson());
        escapeBtn.setOnAction(event -> escapeJson());
        unEscapeBtn.setOnAction(event -> unEscapeJson());
        clearBtn.setOnAction(event -> textArea.clear());
    }

    private void formatJson() {
        String rawString = textArea.getText();
        if (StringUtils.isBlank(rawString)) {
            return;
        }

        Result<String> result = MapperUtils.prettyPrint(rawString);
        if (result.isSuccess()) {
            textArea.setText(result.getData());
            showMessage(true, "");
        } else {
            showMessage(false, result.getMsg());
        }
    }

    private void showMessage(boolean success, String errorMsg) {
        message.setVisible(true);
        if (success) {
            message.setDescription("SUCCESS");
            message.setGraphic(new FontIcon(Material2OutlinedAL.CHECK_CIRCLE_OUTLINE));
            message.getStyleClass().removeIf(e -> Objects.equals(e, Styles.DANGER));
            if (!message.getStyleClass().contains(Styles.SUCCESS)) {
                message.getStyleClass().add(Styles.SUCCESS);
            }
        } else {
            message.setDescription(errorMsg);
            message.setGraphic(new FontIcon(Material2OutlinedAL.ERROR_OUTLINE));
            message.getStyleClass().removeIf(e -> Objects.equals(e, Styles.SUCCESS));
            if (!message.getStyleClass().contains(Styles.DANGER)) {
                message.getStyleClass().add(Styles.DANGER);
            }
        }
    }

    private void compressJson() {
        String rawString = textArea.getText();
        if (StringUtils.isBlank(rawString)) {
            return;
        }
        String string = rawString.replaceAll("\\s+", "");
        textArea.setText(string);
    }

    private void escapeJson() {
        String rawString = textArea.getText();
        if (StringUtils.isBlank(rawString)) {
            return;
        }
        String string = MapperUtils.escapeJson(rawString);
        textArea.setText(string);
    }

    private void unEscapeJson() {
        String rawString = textArea.getText();
        if (StringUtils.isBlank(rawString)) {
            return;
        }
        String string = MapperUtils.unEscapeJson(rawString);
        textArea.setText(string);
    }
}
