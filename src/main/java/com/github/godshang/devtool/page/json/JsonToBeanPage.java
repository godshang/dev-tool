package com.github.godshang.devtool.page.json;

import atlantafx.base.controls.ToggleSwitch;
import com.github.godshang.devtool.page.AbstractPage;
import com.github.godshang.devtool.util.Json2BeanUtils;
import com.github.godshang.devtool.util.StringUtils;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class JsonToBeanPage extends AbstractPage {

    public static final String NAME = "JSON to bean";
    private static final int MIN_WIDTH = 200;

    private ToggleSwitch useLombokToggleSwitch;
    private TextField packageNameTextField;
    private TextField classNameTextField;
    private Button generateButton;
    private Button clearButton;
    private TextArea jsonTextArea;
    private TextArea javaTextArea;

    public JsonToBeanPage() {
        super();
        initView();
        initListener();
    }

    @Override
    public void reset() {
        super.reset();
        generateButton.setOnAction(null);
        clearButton.setOnAction(null);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void initView() {
        useLombokToggleSwitch = new ToggleSwitch();
        useLombokToggleSwitch.setSelected(true);
        addNode(makeHBoxContainer("Use Lombok", SPACING * 10, useLombokToggleSwitch));

        packageNameTextField = new TextField();
        addNode(makeHBoxContainer("Package Name", SPACING * 10, packageNameTextField));

        classNameTextField = new TextField();
        addNode(makeHBoxContainer("Class Name", SPACING * 10, classNameTextField));

        generateButton = new Button("Generate");
        generateButton.setDefaultButton(true);
        clearButton = new Button("Clear");
        var buttonContainer = makeHBoxContainer(generateButton, clearButton);
        addNode(buttonContainer);

        jsonTextArea = new TextArea();
        jsonTextArea.setPromptText("Paste JSON here ...");
        jsonTextArea.setMinWidth(MIN_WIDTH);
        javaTextArea = new TextArea();
        var splitPane = new SplitPane(jsonTextArea, javaTextArea);
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        addNode(splitPane);
    }

    private void initListener() {
        generateButton.setOnAction(event -> generateBean());
        clearButton.setOnAction(event -> clearTextArea());
    }

    private void generateBean() {
        String json = jsonTextArea.getText();
        if (StringUtils.isBlank(json)) {
            return;
        }
        String javaCode = Json2BeanUtils.generate(
                Json2BeanUtils.Option.builder()
                        .packageName(packageNameTextField.getText())
                        .className(classNameTextField.getText())
                        .useLombok(useLombokToggleSwitch.isSelected())
                        .build(),
                json);
        javaTextArea.setText(javaCode);
    }

    private void clearTextArea() {
        jsonTextArea.clear();
        javaTextArea.clear();
    }
}
