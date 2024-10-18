package com.github.godshang.devtool.page.other;

import com.github.godshang.devtool.page.AbstractPage;
import com.github.godshang.devtool.util.SqlFormatUtils;
import com.github.godshang.devtool.util.StringUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SqlFormatPage extends AbstractPage {

    public static final String NAME = "SQL formatter";

    private TextArea textArea;
    private Button formatButton;
    private Button compressButton;

    public SqlFormatPage() {
        super();
        initView();
        initListener();
    }

    @Override
    public void reset() {
        super.reset();
        formatButton.setOnAction(null);
        compressButton.setOnAction(null);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void initView() {
        addNode(new Label("Format and prettify your SQL queries."));

        formatButton = new Button("Format");
        formatButton.setDefaultButton(true);
        compressButton = new Button("Compress");
        addNode(makeHBoxContainer(formatButton, compressButton));

        textArea = new TextArea();
        textArea.setPromptText("Paste SQL here ...");
        VBox.setVgrow(textArea, Priority.ALWAYS);
        addNode(textArea);
    }

    private void initListener() {
        formatButton.setOnAction(event -> formatSql());
        compressButton.setOnAction(event -> compressSql());
    }

    private void formatSql() {
        String sql = textArea.getText();
        if (StringUtils.isBlank(sql)) {
            return;
        }
        textArea.setText(SqlFormatUtils.format(sql));
    }

    private void compressSql() {
        String sql = textArea.getText();
        if (StringUtils.isBlank(sql)) {
            return;
        }
        textArea.setText(sql.replaceAll("\\s+", " "));
    }
}
