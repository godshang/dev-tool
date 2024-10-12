package com.github.godshang.devtool.page.converter;

import com.github.godshang.devtool.page.AbstractPage;
import com.github.godshang.devtool.util.StringUtils;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class IntegerBaseConverterPage extends AbstractPage {

    public static final String NAME = "Integer base converter";

    private TextField inputNumberTextField;
    private TextField inputBaseTextField;
    private TextField binaryResultTextField;
    private TextField octalResultTextField;
    private TextField decimalResultTextField;
    private TextField hexadecimalResultTextField;
    private Spinner<Integer> customResultSpinner;
    private TextField customResultTextField;

    private ChangeListener inputChangeListener = (observable, oldValue, newValue) -> calculate();

    public IntegerBaseConverterPage() {
        super();
        initView();
        initListener();
        calculate();
    }

    @Override
    public void reset() {
        super.reset();
        inputNumberTextField.textProperty().removeListener(inputChangeListener);
        inputBaseTextField.textProperty().removeListener(inputChangeListener);
        customResultSpinner.valueProperty().removeListener(inputChangeListener);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void initView() {
        addNode(new Label("Convert a number between different bases (decimal, hexadecimal, binary, octal, base64, ...)"));

        inputNumberTextField = new TextField("42");
        inputBaseTextField = new TextField("10");
        var inputGridPane = new GridPane();
        inputGridPane.setVgap(SPACING);
        inputGridPane.setHgap(SPACING);
        inputGridPane.add(new Label("Input Number"), 0, 0);
        inputGridPane.add(inputNumberTextField, 1, 0);
        inputGridPane.add(new Label("Input Base"), 0, 1);
        inputGridPane.add(inputBaseTextField, 1, 1);
        addNode(inputGridPane);

        addNode(new Separator());

        binaryResultTextField = new TextField();
        octalResultTextField = new TextField();
        decimalResultTextField = new TextField();
        hexadecimalResultTextField = new TextField();
        customResultTextField = new TextField();
        customResultSpinner = new Spinner<>(Character.MIN_RADIX, Character.MAX_RADIX, 10);
        customResultSpinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        customResultSpinner.setEditable(true);
        var outputGridPane = new GridPane();
        outputGridPane.setVgap(SPACING);
        outputGridPane.setHgap(SPACING);
        outputGridPane.add(new Label("Binary (2)"), 0, 0);
        outputGridPane.add(binaryResultTextField, 1, 0);
        outputGridPane.add(new Label("Octal (8)"), 0, 1);
        outputGridPane.add(octalResultTextField, 1, 1);
        outputGridPane.add(new Label("Decimal (10)"), 0, 2);
        outputGridPane.add(decimalResultTextField, 1, 2);
        outputGridPane.add(new Label("Hexadecimal (16)"), 0, 3);
        outputGridPane.add(hexadecimalResultTextField, 1, 3);
        outputGridPane.add(customResultSpinner, 0, 4);
        outputGridPane.add(customResultTextField, 1, 4);
        addNode(outputGridPane);
    }

    private void initListener() {
        inputNumberTextField.textProperty().addListener(inputChangeListener);
        inputBaseTextField.textProperty().addListener(inputChangeListener);
        customResultSpinner.valueProperty().addListener(inputChangeListener);
    }

    private void calculate() {
        try {
            doCalculate();
        } catch (Exception e) {
            popupNotification(e.getMessage(), false);
        }
    }

    private void doCalculate() {
        String inputNumber = inputNumberTextField.getText();
        String inputBase = inputBaseTextField.getText();
        if (StringUtils.isBlank(inputNumber)
                || StringUtils.isBlank(inputBase)
                || !StringUtils.isNumberic(inputNumber)
                || !StringUtils.isNumberic(inputBase)) {
            return;
        }

        int base = Integer.parseInt(inputBase);
        int number = Integer.parseInt(inputNumber, base);
        binaryResultTextField.setText(Integer.toBinaryString(number));
        octalResultTextField.setText(Integer.toOctalString(number));
        decimalResultTextField.setText(Integer.toString(number, 10));
        hexadecimalResultTextField.setText(Integer.toHexString(number));
        customResultTextField.setText(Integer.toString(number, customResultSpinner.getValue()));
    }
}
