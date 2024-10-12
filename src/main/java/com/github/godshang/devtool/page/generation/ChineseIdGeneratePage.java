package com.github.godshang.devtool.page.generation;

import com.github.godshang.devtool.page.AbstractPage;
import com.github.godshang.devtool.util.ChineseIdUtils;
import com.github.godshang.devtool.util.FXUtils;
import com.github.godshang.devtool.util.GB2260;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChineseIdGeneratePage extends AbstractPage {

    public static final String NAME = "Chinese ID generater";

    private ComboBox<GB2260.Region> provinceBox;
    private ComboBox<GB2260.Region> cityBox;
    private ComboBox<GB2260.Region> prefectureBox;
    private Spinner<Integer> yearSpinner;
    private Spinner<Integer> monthSpinner;
    private Spinner<Integer> daySpinner;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private ToggleGroup sexToggleGroup;
    private Spinner<Integer> numSpinner;
    private Button generateButton;
    private GridPane resultGridPane;

    private ChangeListener<GB2260.Region> provinceChangeListener = (observable, oldValue, newValue) -> {
        cityBox.getItems().clear();
        prefectureBox.getItems().clear();

        if (newValue == null) {
            return;
        }
        String provinceCode = newValue.getCode();
        cityBox.getItems().setAll(GB2260.getAllCity(provinceCode));
    };

    private ChangeListener<GB2260.Region> cityChangeListener = (observable, oldValue, newValue) -> {
        prefectureBox.getItems().clear();

        if (newValue == null) {
            return;
        }
        String cityCode = newValue.getCode();
        prefectureBox.getItems().setAll(GB2260.getAllPrefecture(cityCode));
    };

    public ChineseIdGeneratePage() {
        super();
        initView();
        initListener();
        initData();
    }

    @Override
    public void reset() {
        super.reset();
        generateButton.setOnAction(null);
        provinceBox.getSelectionModel().selectedItemProperty().removeListener(provinceChangeListener);
        cityBox.getSelectionModel().selectedItemProperty().removeListener(cityChangeListener);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void initView() {
        addNode(new Label("Generate faked chinese ID card number."));

        provinceBox = new ComboBox<>();
        provinceBox.setPrefWidth(SPACING * 10);
        cityBox = new ComboBox<>();
        cityBox.setPrefWidth(SPACING * 10);
        prefectureBox = new ComboBox<>();
        prefectureBox.setPrefWidth(SPACING * 10);
        var region = new HBox(SPACING);
        region.getChildren().addAll(provinceBox, cityBox, prefectureBox);

        yearSpinner = new Spinner(1960, 2020, 2000);
        yearSpinner.setEditable(true);
        monthSpinner = new Spinner(1, 12, 1);
        monthSpinner.setEditable(true);
        daySpinner = new Spinner(1, 31, 1);
        daySpinner.setEditable(true);
        var birthday = new HBox(SPACING);
        birthday.getChildren().addAll(yearSpinner, monthSpinner, daySpinner);

        maleRadioButton = new RadioButton("Male");
        maleRadioButton.setUserData(1);
        maleRadioButton.setSelected(true);
        femaleRadioButton = new RadioButton("Female");
        femaleRadioButton.setUserData(0);
        sexToggleGroup = new ToggleGroup();
        sexToggleGroup.getToggles().addAll(maleRadioButton, femaleRadioButton);
        var sex = new HBox(SPACING);
        sex.getChildren().addAll(maleRadioButton, femaleRadioButton);

        numSpinner = new Spinner(1, 100, 10);
        numSpinner.setEditable(true);

        generateButton = new Button("Generate");
        generateButton.setDefaultButton(true);

        var gridPane = new GridPane();
        gridPane.setHgap(SPACING);
        gridPane.setVgap(SPACING);
        gridPane.add(new Label("Region"), 0, 0);
        gridPane.add(region, 1, 0);
        gridPane.add(new Label("Sex"), 0, 1);
        gridPane.add(sex, 1, 1);
        gridPane.add(new Label("Birthday"), 0, 2);
        gridPane.add(birthday, 1, 2);
        gridPane.add(new Label("Number"), 0, 3);
        gridPane.add(numSpinner, 1, 3);
        gridPane.add(generateButton, 0, 4);

        var container = new VBox(SPACING);
        container.getChildren().add(gridPane);
        addNode(container);

        addNode(new Separator());

        resultGridPane = new GridPane();
        resultGridPane.setHgap(SPACING);
        resultGridPane.setVgap(SPACING);
        var scrollPane = new ScrollPane();
        scrollPane.setContent(resultGridPane);
        addNode(scrollPane);
    }

    private void initListener() {
        provinceBox.getSelectionModel().selectedItemProperty().addListener(provinceChangeListener);
        cityBox.getSelectionModel().selectedItemProperty().addListener(cityChangeListener);
        generateButton.setOnAction(event -> generate());
    }

    private void initData() {
        provinceBox.getItems().setAll(GB2260.getAllProvinces());
        provinceBox.getSelectionModel().select(GB2260.getProvince("110000"));

        cityBox.getItems().setAll(GB2260.getAllCity("110000"));
        cityBox.getSelectionModel().select(GB2260.getCity("110100"));

        prefectureBox.getItems().setAll(GB2260.getAllPrefecture("110100"));
        prefectureBox.getSelectionModel().select(GB2260.getPrefecture("110101"));
    }

    private void generate() {
        if (prefectureBox.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        String code = prefectureBox.getSelectionModel().getSelectedItem().getCode();
        Integer sex = (Integer) sexToggleGroup.getSelectedToggle().getUserData();

        Integer year = yearSpinner.getValue(), month = monthSpinner.getValue(), day = daySpinner.getValue();
        Integer num = numSpinner.getValue();
        if (year == null || month == null || day == null || num == null || num <= 0) {
            return;
        }
        LocalDateTime localDateTime = LocalDateTime.of(year, month, day, 0, 0, 0);
        String birthday = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        List<String> list = ChineseIdUtils.generate(code, birthday, sex, num);
        if (!list.isEmpty()) {
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
}
