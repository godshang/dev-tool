package com.github.godshang.devtool.page.json;

import com.github.godshang.devtool.page.AbstractPage;
import com.github.godshang.devtool.util.FXUtils;
import com.github.godshang.devtool.util.Json2ExcelUtils;
import com.github.godshang.devtool.util.StringUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class JsonToExcelPage extends AbstractPage {

    private static final Logger log = LoggerFactory.getLogger(JsonToExcelPage.class);

    public static final String NAME = "JSON to excel";

    private Button convertBtn;
    private TextArea textArea;
    private Label saveToLabel;
    private TextField fileTextField;
    private Button browseBtn;

    public JsonToExcelPage() {
        super();
        initView();
        initListener();
    }

    @Override
    public void reset() {
        super.reset();
        browseBtn.setOnAction(null);
        convertBtn.setOnAction(null);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void initView() {
        textArea = new TextArea();
        textArea.setPromptText("Paste JSON here ...");
        VBox.setVgrow(textArea, Priority.ALWAYS);
        addNode(textArea);

        saveToLabel = new Label("Save To");
        fileTextField = new TextField();
        browseBtn = new Button("Browse");
        var fileBox = makeHBoxContainer(saveToLabel, fileTextField, browseBtn);
        HBox.setHgrow(fileTextField, Priority.ALWAYS);
        addNode(fileBox);

        convertBtn = new Button("Convert");
        convertBtn.setDefaultButton(true);
        addNode(new HBox(convertBtn));
    }

    private void initListener() {
        browseBtn.setOnAction(event -> openFileBrowser());
        convertBtn.setOnAction(event -> convert());
    }

    private void openFileBrowser() {
        File selectedFile = FXUtils.chooseFileToSave(new String[][]{
                new String[]{"Excel Files", "*.xlsx"},
                new String[]{"All Files", "*.*"},
        });
        if (selectedFile == null) {
            return;
        }
        fileTextField.setText(selectedFile.getAbsolutePath());
    }

    @SneakyThrows
    private void convert() {
        String json = textArea.getText();
        if (StringUtils.isBlank(json)) {
            return;
        }
        String filePath = fileTextField.getText();
        if (filePath == null || filePath.isEmpty()) {
            return;
        }

        File outputFile = new File(filePath);
        if (outputFile.exists()) {
            outputFile.createNewFile();
        }

        try {
            Json2ExcelUtils.convert(json, outputFile);
            popupNotification("Convert Success!", true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            popupNotification("Convert Failed!", false);
        }
    }
}
