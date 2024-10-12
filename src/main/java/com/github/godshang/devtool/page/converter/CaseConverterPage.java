package com.github.godshang.devtool.page.converter;

import com.github.godshang.devtool.page.AbstractPage;
import com.github.godshang.devtool.util.StringUtils;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class CaseConverterPage extends AbstractPage {

    public static final String NAME = "Case converter";

    private TextField inputTextField;
    private TextField lowerCaseTextField;
    private TextField upperCaseTextField;
    private TextField camelCaseTextField;
    private TextField capitalCaseTextField;
    private TextField dotCaseTextField;
    private TextField pathCaseTextField;
    private TextField snakeCaseTextField;
    private TextField pascalCaseTextField;
    private TextField constantCaseTextField;

    private ChangeListener<String> inputChangeListener = (observable, oldValue, newValue) -> convert(newValue);

    public CaseConverterPage() {
        super();
        initView();
        initListener();
        convert();
    }

    @Override
    public void reset() {
        super.reset();
        inputTextField.textProperty().removeListener(inputChangeListener);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void initView() {
        addNode(new Label("Transform the case of a string and choose between different formats"));

        inputTextField = new TextField("Viva la vida");
        lowerCaseTextField = new TextField();
        upperCaseTextField = new TextField();
        camelCaseTextField = new TextField();
        capitalCaseTextField = new TextField();
        dotCaseTextField = new TextField();
        pathCaseTextField = new TextField();
        snakeCaseTextField = new TextField();
        pascalCaseTextField = new TextField();
        constantCaseTextField = new TextField();

        var inputGridPane = new GridPane();
        inputGridPane.setHgap(SPACING);
        inputGridPane.setVgap(SPACING);
        inputGridPane.add(new Label("Your string"), 0, 0);
        inputGridPane.add(inputTextField, 1, 0);
        addNode(inputGridPane);

        addNode(new Separator());

        var gridPane = new GridPane();
        gridPane.setHgap(SPACING);
        gridPane.setVgap(SPACING);
        gridPane.add(new Label("Lower case"), 0, 0);
        gridPane.add(lowerCaseTextField, 1, 0);
        gridPane.add(new Label("Upper case"), 0, 1);
        gridPane.add(upperCaseTextField, 1, 1);
        gridPane.add(new Label("Camel case"), 0, 2);
        gridPane.add(camelCaseTextField, 1, 2);
        gridPane.add(new Label("Capital case"), 0, 3);
        gridPane.add(capitalCaseTextField, 1, 3);
        gridPane.add(new Label("Dot case"), 0, 4);
        gridPane.add(dotCaseTextField, 1, 4);
        gridPane.add(new Label("Path case"), 0, 5);
        gridPane.add(pathCaseTextField, 1, 5);
        gridPane.add(new Label("Snake case"), 0, 6);
        gridPane.add(snakeCaseTextField, 1, 6);
        gridPane.add(new Label("Pascal case"), 0, 7);
        gridPane.add(pascalCaseTextField, 1, 7);
        gridPane.add(new Label("Constant case"), 0, 8);
        gridPane.add(constantCaseTextField, 1, 8);
        addNode(gridPane);
    }

    private void initListener() {
        inputTextField.textProperty().addListener(inputChangeListener);
    }

    private void convert() {
        convert(inputTextField.getText());
    }

    private void convert(String input) {
        if (StringUtils.isBlank(input)) {
            return;
        }
        lowerCaseTextField.setText(StringUtils.lowerCase(input));
        upperCaseTextField.setText(StringUtils.upperCase(input));
        camelCaseTextField.setText(StringUtils.camelCase(input));
        capitalCaseTextField.setText(StringUtils.capitalCase(input));
        dotCaseTextField.setText(StringUtils.dotCase(input));
        pathCaseTextField.setText(StringUtils.pathCase(input));
        snakeCaseTextField.setText(StringUtils.snakeCase(input));
        pascalCaseTextField.setText(StringUtils.pascalCase(input));
        constantCaseTextField.setText(StringUtils.constantCase(input));
    }
}
