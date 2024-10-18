package com.github.godshang.devtool.page.generation;

import atlantafx.base.theme.Styles;
import com.github.godshang.devtool.page.AbstractPage;
import com.github.godshang.devtool.util.FXUtils;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class UuidGeneratePage extends AbstractPage {

    public static final String NAME = "UUID generater";

    private TextField numberTextField;
    private ToggleGroup caseToggleGroup;
    private CheckBox removeHyphenCheckBox;
    private Button generateBtn;
    private Button copyAllBtn;
    private Pane resultContainer;
    private GridPane resultGridPane;

    public UuidGeneratePage() {
        super();
        initView();
        initListener();
    }

    @Override
    public void reset() {
        super.reset();
        generateBtn.setOnAction(null);
        copyAllBtn.setOnAction(null);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void initView() {
        addNode(createControlPane());
        addNode(new Separator());
        addNode(createResultPane());
    }

    private Node createControlPane() {
        var numberLabel = new Label("Number");
        numberTextField = new TextField();
        numberTextField.setText("50");
        var numberTipLabel = new Label("The number of UUIDs to be generated, should not exceed 500.");
        numberTipLabel.getStyleClass().add(Styles.TEXT_SUBTLE);

        var optionLabel = new Label("Option");
        var caseBox = new HBox(SPACING);
        caseToggleGroup = new ToggleGroup();
        var upperCaseRadioButton = new RadioButton("Upper Case");
        upperCaseRadioButton.setUserData("Upper");
        upperCaseRadioButton.setSelected(true);
        upperCaseRadioButton.setToggleGroup(caseToggleGroup);
        var lowerCaseRadioButton = new RadioButton("Lower Case");
        lowerCaseRadioButton.setUserData("Lower");
        lowerCaseRadioButton.setToggleGroup(caseToggleGroup);
        caseBox.getChildren().addAll(upperCaseRadioButton, lowerCaseRadioButton);

        removeHyphenCheckBox = new CheckBox("Remove Hyphen");

        generateBtn = new Button("Generate");
        generateBtn.setDefaultButton(true);

        var gridPane = new GridPane();
        gridPane.setHgap(SPACING);
        gridPane.setVgap(SPACING);
        gridPane.add(numberLabel, 0, 0);
        gridPane.add(numberTextField, 1, 0);
        gridPane.add(numberTipLabel, 2, 0);
        gridPane.add(optionLabel, 0, 1);
        gridPane.add(caseBox, 1, 1);
        gridPane.add(removeHyphenCheckBox, 1, 2);
        gridPane.add(generateBtn, 1, 3);
        return gridPane;
    }

    private Node createResultPane() {
        copyAllBtn = new Button("Copy All");
        resultGridPane = new GridPane();
        resultGridPane.setHgap(SPACING);
        resultGridPane.setVgap(SPACING);
        var scrollPane = new ScrollPane();
        scrollPane.setContent(resultGridPane);

        resultContainer = new VBox(SPACING);
        resultContainer.getChildren().addAll(copyAllBtn, scrollPane);
        resultContainer.setVisible(false);
        return resultContainer;
    }

    private void initListener() {
        generateBtn.setOnAction(event -> genetate());
        copyAllBtn.setOnAction(event -> copyAll());
    }

    private void genetate() {
        String numberStr = numberTextField.getText();
        if (numberStr == null || numberStr.isEmpty()) {
            return;
        }
        int number = Integer.parseInt(numberStr);
        boolean upperCase = Objects.equals(caseToggleGroup.getSelectedToggle().getUserData(), "Upper");
        boolean removeHyphen = removeHyphenCheckBox.isSelected();
        List<String> list = doGenerate(number, upperCase, removeHyphen);
        if (!list.isEmpty()) {
            resultContainer.setVisible(true);
            resultGridPane.getChildren().clear();
            for (int i = 0, size = list.size(); i < size; i++) {
                var label = new Label(list.get(i));
                var copyBtn = new Button("Copy");
                copyBtn.setOnAction(event -> {
                    FXUtils.putClipboard(label.getText());
                });
                resultGridPane.add(label, 0, i);
                resultGridPane.add(copyBtn, 1, i);
            }
        }
    }

    private List<String> doGenerate(int number, boolean upperCase, boolean removeHyphen) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            String uuid = UUID.randomUUID().toString();
            if (upperCase) {
                uuid = uuid.toUpperCase();
            }
            if (removeHyphen) {
                uuid = uuid.replace("-", "");
            }
            list.add(uuid);
        }
        return list;
    }

    private void copyAll() {
        List<String> list = new ArrayList<>();
        for (Node node : resultGridPane.getChildren()) {
            if (node instanceof Label) {
                list.add(((Label) node).getText());
            }
        }
        if (!list.isEmpty()) {
            FXUtils.putClipboard(String.join("\n", list));
        }
    }
}
