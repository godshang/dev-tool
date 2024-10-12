package com.github.godshang.devtool.page.json;

import com.github.godshang.devtool.page.AbstractPage;
import com.github.godshang.devtool.util.MapperUtils;
import com.github.godshang.devtool.util.StringUtils;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class JsonDiffPage extends AbstractPage {

    public static final String NAME = "JSON diff";

    private TextArea leftTextArea;
    private TextArea rightTextArea;
    private TextArea resultTextArea;
    private Button compareButton;

    public JsonDiffPage() {
        super();
        initView();
        initListener();
    }

    @Override
    public void reset() {
        super.reset();
        compareButton.setOnAction(null);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void initView() {
        leftTextArea = new TextArea();
        leftTextArea.setPromptText("Your JSON ...");
        rightTextArea = new TextArea();
        rightTextArea.setPromptText("Your JSON to compare ...");
        resultTextArea = new TextArea();

        HBox.setHgrow(leftTextArea, Priority.ALWAYS);
        HBox.setHgrow(rightTextArea, Priority.ALWAYS);
        VBox.setVgrow(leftTextArea, Priority.ALWAYS);
        VBox.setVgrow(rightTextArea, Priority.ALWAYS);
        VBox.setVgrow(resultTextArea, Priority.ALWAYS);

        var jsonContainer = new HBox(SPACING);
        jsonContainer.getChildren().addAll(leftTextArea, rightTextArea);
        addNode(jsonContainer);

        compareButton = new Button("Compare");
        compareButton.setDefaultButton(true);
        addNode(compareButton);

        addNode(resultTextArea);
    }

    private void initListener() {
        compareButton.setOnAction(event -> compare());
    }

    private void compare() {
        String actual = leftTextArea.getText();
        String expected = rightTextArea.getText();
        if (StringUtils.isBlank(expected) || StringUtils.isBlank(actual)
                || !MapperUtils.isValidJson(expected) || !MapperUtils.isValidJson(actual)) {
            return;
        }

        String diff = MapperUtils.diffJson(expected, actual);
        resultTextArea.setText(diff);
    }
}
