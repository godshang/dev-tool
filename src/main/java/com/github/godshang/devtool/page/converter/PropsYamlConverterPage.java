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

public class PropsYamlConverterPage extends AbstractPage {

    public static final String NAME = "Properties-YAML converter";

    private Button clearBtn;
    private Button propsToYamlBtn;
    private Button yamlToPropsBtn;
    private TextArea propsTextArea;
    private TextArea yamlTextArea;

    public PropsYamlConverterPage() {
        super();
        initView();
        initListener();
    }

    @Override
    public void reset() {
        super.reset();
        clearBtn.setOnAction(null);
        propsToYamlBtn.setOnAction(null);
        yamlToPropsBtn.setOnAction(null);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void initView() {
        addNode(new Label("Convert Properteis to YAML and vice versa."));

        propsTextArea = new TextArea();
        propsTextArea.setPromptText("Paste Properties here ...");
        yamlTextArea = new TextArea();
        yamlTextArea.setPromptText("Paste YAML here ...");

        clearBtn = new Button("Clear");
        clearBtn.setMinWidth(SPACING * 7);
        propsToYamlBtn = new Button("->");
        propsToYamlBtn.setMinWidth(SPACING * 7);
        yamlToPropsBtn = new Button("<-");
        yamlToPropsBtn.setMinWidth(SPACING * 7);
        var btnContainer = new VBox(SPACING);
        btnContainer.setAlignment(Pos.CENTER);
        btnContainer.getChildren().addAll(clearBtn, propsToYamlBtn, yamlToPropsBtn);

        var container = new HBox(SPACING);
        container.getChildren().addAll(propsTextArea, btnContainer, yamlTextArea);
        HBox.setHgrow(propsTextArea, Priority.ALWAYS);
        HBox.setHgrow(yamlTextArea, Priority.ALWAYS);
        HBox.setHgrow(btnContainer, Priority.ALWAYS);
        VBox.setVgrow(container, Priority.ALWAYS);
        addNode(container);
    }

    private void initListener() {
        clearBtn.setOnAction(event -> clearTextArea());
        propsToYamlBtn.setOnAction(event -> convertPropsToYaml());
        yamlToPropsBtn.setOnAction(event -> convertYamlToProps());
    }

    private void clearTextArea() {
        propsTextArea.clear();
        yamlTextArea.clear();
    }

    private void convertPropsToYaml() {
        String props = propsTextArea.getText();
        if (props == null || props.isEmpty()) {
            return;
        }
        Result<String> result = MapperUtils.propsToYaml(props);
        yamlTextArea.setText(result.isSuccess() ? result.getData() : result.getMsg());
    }

    private void convertYamlToProps() {
        String yaml = yamlTextArea.getText();
        if (yaml == null || yaml.isEmpty()) {
            return;
        }
        Result<String> result = MapperUtils.yamlToProps(yaml);
        propsTextArea.setText(result.isSuccess() ? result.getData() : result.getMsg());
    }
}
