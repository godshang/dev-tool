package com.github.godshang.devtool.page.generation;

import com.github.godshang.devtool.page.AbstractPage;
import com.github.godshang.devtool.util.FXUtils;
import com.github.godshang.devtool.util.StringUtils;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;


public class RandomStringGeneratePage extends AbstractPage {

    public static final String NAME = "Random string generater";

    private CheckBox digitCheckBox;
    private CheckBox lowerCaseCheckBox;
    private CheckBox upperCaseCheckBox;
    private TextField lengthTextField;
    private TextField numberTextField;
    private Button generateButton;
    private GridPane resultGridPane;

    public RandomStringGeneratePage() {
        super();
        initView();
        initListener();
    }

    @Override
    public void reset() {
        super.reset();
        generateButton.setOnAction(null);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void initView() {
        digitCheckBox = new CheckBox();
        digitCheckBox.setSelected(true);
        lowerCaseCheckBox = new CheckBox();
        lowerCaseCheckBox.setSelected(true);
        upperCaseCheckBox = new CheckBox();
        upperCaseCheckBox.setSelected(true);
        lengthTextField = new TextField();
        lengthTextField.setText("8");
        numberTextField = new TextField();
        numberTextField.setText("1");

        generateButton = new Button("Generate");
        generateButton.setDefaultButton(true);

        var gridPane = new GridPane();
        gridPane.setHgap(SPACING);
        gridPane.setVgap(SPACING);
        gridPane.add(new Label("Include Digit"), 0, 0);
        gridPane.add(digitCheckBox, 1, 0);
        gridPane.add(new Label("Include Lower Case Letter"), 0, 1);
        gridPane.add(lowerCaseCheckBox, 1, 1);
        gridPane.add(new Label("Include Upper Case Letter"), 0, 2);
        gridPane.add(upperCaseCheckBox, 1, 2);
        gridPane.add(new Label("Length"), 0, 3);
        gridPane.add(lengthTextField, 1, 3);
        gridPane.add(new Label("Number"), 0, 4);
        gridPane.add(numberTextField, 1, 4);
        gridPane.add(generateButton, 0, 5);
        addNode(gridPane);

        addNode(new Separator());

        resultGridPane = new GridPane();
        resultGridPane.setHgap(SPACING);
        resultGridPane.setVgap(SPACING);
        var scrollPane = new ScrollPane();
        scrollPane.setContent(resultGridPane);
        addNode(scrollPane);
    }

    private void initListener() {
        generateButton.setOnAction(event -> generate());
    }

    private void generate() {
        if (StringUtils.isBlank(lengthTextField.getText())) {
            return;
        }
        if (StringUtils.isBlank(numberTextField.getText())) {
            return;
        }
        int length = Integer.parseInt(lengthTextField.getText());
        int number = Integer.parseInt(numberTextField.getText());

        StringBuilder baseString = new StringBuilder();
        if (digitCheckBox.isSelected()) {
            for (char ch = '0'; ch <= '9'; ch++) {
                baseString.append(ch);
            }
        }
        if (lowerCaseCheckBox.isSelected()) {
            for (char ch = 'a'; ch <= 'z'; ch++) {
                baseString.append(ch);
            }
        }
        if (upperCaseCheckBox.isSelected()) {
            for (char ch = 'A'; ch <= 'Z'; ch++) {
                baseString.append(ch);
            }
        }

        List<String> randomStringList = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            randomStringList.add(StringUtils.randomString(baseString.toString(), length));
        }

        resultGridPane.getChildren().clear();
        for (int i = 0, size = randomStringList.size(); i < size; i++) {
            var label = new Label(randomStringList.get(i));
            var copyBtn = new Button("Copy");
            copyBtn.setOnAction(event -> {
                FXUtils.putClipboard(label.getText());
            });
            resultGridPane.add(label, 0, i);
            resultGridPane.add(copyBtn, 1, i);
        }
    }
}
