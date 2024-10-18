package com.github.godshang.devtool.page.converter;

import com.github.godshang.devtool.page.AbstractPage;
import com.github.godshang.devtool.util.ColorUtils;
import com.github.godshang.devtool.util.FXUtils;
import com.github.godshang.devtool.util.StringUtils;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ColorConverterPage extends AbstractPage {

    public static final String NAME = "Color converter";

    private ColorPicker colorPicker;
    private TextField hexTextField;
    private TextField rgbTextField;
    private TextField cmykTextField;
    private TextField hsbTextField;
    private TextField hslTextField;
    private List<TextFieldGroup> allTextFieldGroup = new ArrayList<>();

    private ChangeListener<Color> colorChangeListener = (observable, oldValue, newValue) -> display(newValue);

    record TextFieldGroup(Label label, TextField textField, Button copyButton) {

        HBox makeHBox() {
            var container = new HBox(AbstractPage.SPACING);
            container.getChildren().addAll(label, textField, copyButton);
            container.setAlignment(Pos.BASELINE_LEFT);
            return container;
        }
    }

    public ColorConverterPage() {
        super();
        initView();
        initListener();
        display();
    }

    @Override
    public void reset() {
        super.reset();
        colorPicker.valueProperty().removeListener(colorChangeListener);
        allTextFieldGroup.forEach(e -> {
            e.copyButton.setOnAction(null);
        });
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void initView() {
        addNode(new Label("Convert color between the different formats (hex, rgb, hsl and css name)"));

        colorPicker = new ColorPicker();
        var colorPickerLabel = new Label("color picker");
        colorPickerLabel.setMinWidth(SPACING * 10);
        colorPickerLabel.setAlignment(Pos.BOTTOM_RIGHT);
        var colorPickerContainer = new HBox(SPACING);
        colorPickerContainer.getChildren().addAll(colorPickerLabel, colorPicker);
        colorPickerContainer.setAlignment(Pos.BASELINE_LEFT);
        addNode(colorPickerContainer);

        addNode(new Separator());

        hexTextField = new TextField();
        rgbTextField = new TextField();
        cmykTextField = new TextField();
        hsbTextField = new TextField();
        hslTextField = new TextField();
        makeTextFieldGroup("hex", hexTextField);
        makeTextFieldGroup("rgb", rgbTextField);
        makeTextFieldGroup("cmyk", cmykTextField);
        makeTextFieldGroup("hsb/hsv", hsbTextField);
        makeTextFieldGroup("hsl", hslTextField);

        var resultContainer = new VBox(SPACING);
        resultContainer.getChildren().addAll(allTextFieldGroup.stream().map(TextFieldGroup::makeHBox).collect(Collectors.toList()));
        addNode(resultContainer);
    }

    private void makeTextFieldGroup(String title, TextField textField) {
        var label = new Label(title);
        label.setMinWidth(SPACING * 10);
        label.setAlignment(Pos.BOTTOM_RIGHT);

        HBox.setHgrow(textField, Priority.ALWAYS);
        var copyButton = new Button("", new FontIcon(Material2AL.CONTENT_COPY));

        TextFieldGroup textFieldGroup = new TextFieldGroup(label, textField, copyButton);
        allTextFieldGroup.add(textFieldGroup);
    }

    private void initListener() {
        colorPicker.valueProperty().addListener(colorChangeListener);

        allTextFieldGroup.forEach(e -> {
            e.copyButton.setOnAction(event -> {
                String text = e.textField().getText();
                if (!StringUtils.isBlank(text)) {
                    FXUtils.putClipboard(text);
                }
            });
        });
    }

    private void display() {
        display(colorPicker.getValue());
    }

    private void display(Color color) {
        if (color == null) {
            return;
        }
        updateTextFieldQuietly(hexTextField, ColorUtils.hex(color));
        updateTextFieldQuietly(rgbTextField, ColorUtils.rgb(color));
        updateTextFieldQuietly(cmykTextField, ColorUtils.cmyk(color));
        updateTextFieldQuietly(hsbTextField, ColorUtils.hsb(color));
        updateTextFieldQuietly(hslTextField, ColorUtils.hsl(color));
    }

    private void doConvert(String colorString) {
        if (StringUtils.isBlank(colorString)) {
            return;
        }
        try {
            Color color = ColorUtils.parse(colorString);
            if (color != null) {
                colorPicker.setValue(color);
                display(color);
            }
        } catch (Exception e) {
            popupNotification(e.getMessage(), false);
        }
    }

    private void updateTextFieldQuietly(TextField textField, String text) {
        textField.textProperty().removeListener(changeListener);
        textField.setText(text);
        textField.textProperty().addListener(changeListener);
    }

    private ChangeListener<String> changeListener = (observable, oldValue, newValue) -> doConvert(newValue);
}
