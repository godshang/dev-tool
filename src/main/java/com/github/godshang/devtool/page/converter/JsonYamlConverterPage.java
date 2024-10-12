package com.github.godshang.devtool.page.converter;


import com.github.godshang.devtool.common.Result;
import com.github.godshang.devtool.page.AbstractPage;
import com.github.godshang.devtool.util.MapperUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class JsonYamlConverterPage extends AbstractPage {

    public static final String NAME = "JSON-YAML converter";

    private Button clearBtn;
    private Button jsonToYamlBtn;
    private Button yamlToJsonBtn;
    private TextArea jsonTextArea;
    private TextArea yamlTextArea;

    public JsonYamlConverterPage() {
        super();
        initView();
        initListener();
    }

    @Override
    public void reset() {
        super.reset();
        clearBtn.setOnAction(null);
        jsonToYamlBtn.setOnAction(null);
        yamlToJsonBtn.setOnAction(null);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void initView() {
        addNode(new Label("Convert JSON to YAML and vice versa."));

        jsonTextArea = new TextArea();
        jsonTextArea.setPromptText("Paste JSON here ...");
        yamlTextArea = new TextArea();
        yamlTextArea.setPromptText("Paste YAML here ...");

        clearBtn = new Button("Clear");
        clearBtn.setMinWidth(SPACING * 7);
        jsonToYamlBtn = new Button("->");
        jsonToYamlBtn.setMinWidth(SPACING * 7);
        yamlToJsonBtn = new Button("<-");
        yamlToJsonBtn.setMinWidth(SPACING * 7);
        var btnContainer = new VBox(SPACING);
        btnContainer.setAlignment(Pos.CENTER);
        btnContainer.getChildren().addAll(clearBtn, jsonToYamlBtn, yamlToJsonBtn);

        var container = new HBox(SPACING);
        container.getChildren().addAll(jsonTextArea, btnContainer, yamlTextArea);
        HBox.setHgrow(jsonTextArea, Priority.ALWAYS);
        HBox.setHgrow(yamlTextArea, Priority.ALWAYS);
        HBox.setHgrow(btnContainer, Priority.ALWAYS);
        VBox.setVgrow(container, Priority.ALWAYS);
        addNode(container);
    }

    private void initListener() {
        clearBtn.setOnAction(event -> clearTextArea());
        jsonToYamlBtn.setOnAction(event -> convertJsonToYaml());
        yamlToJsonBtn.setOnAction(event -> convertYamlToJson());
    }

    private void clearTextArea() {
        jsonTextArea.clear();
        yamlTextArea.clear();
    }

    private void convertJsonToYaml() {
        String json = jsonTextArea.getText();
        if (json == null || json.isEmpty()) {
            return;
        }
        Result<String> result = MapperUtils.jsonToYaml(json);
        yamlTextArea.setText(result.isSuccess() ? result.getData() : result.getMsg());
    }

    private void convertYamlToJson() {
        String yaml = yamlTextArea.getText();
        if (yaml == null || yaml.isEmpty()) {
            return;
        }
        Result<String> result = MapperUtils.yamlToJson(yaml);
        jsonTextArea.setText(result.isSuccess() ? result.getData() : result.getMsg());
    }
}
