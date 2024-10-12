package com.github.godshang.devtool.page.converter;

import com.github.godshang.devtool.page.AbstractPage;
import com.github.godshang.devtool.util.DateTimeUtils;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class TimestampConverterPage extends AbstractPage {

    public static final String NAME = "Timestamp converter";

    private TextField timestampText;
    private TextField timestampConvertResultText;
    private Button timestampConvertBtn;
    private ChoiceBox timestampUnit;

    private TextField dateTimeText;
    private TextField dateTimeConvertResultText;
    private Button dateTimeConvertBtn;
    private ChoiceBox dateTimeUnit;

    public TimestampConverterPage() {
        super();
        initView();
        initListener();
    }

    @Override
    public void reset() {
        super.reset();
        timestampConvertBtn.setOnAction(null);
        dateTimeConvertBtn.setOnAction(null);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void initView() {
        addNode(new Label("Convert timestamp to date and time"));

        long now = System.currentTimeMillis();
        addNode(new Label("System Default ZoneId: " + DateTimeUtils.getSystemZone()));

        timestampText = new TextField();
        timestampText.setText(String.valueOf(now));
        timestampUnit = new ChoiceBox();
        timestampUnit.getItems().addAll("ms", "s");
        timestampUnit.getSelectionModel().select(0);
        timestampConvertBtn = new Button("Convert");
        timestampConvertBtn.setDefaultButton(true);
        timestampConvertResultText = new TextField();
        var timestampBox = makeHBoxContainer("Timestamp", SPACING * 8,
                timestampText, timestampUnit, timestampConvertBtn, timestampConvertResultText);
        addNode(timestampBox);

        dateTimeText = new TextField();
        dateTimeText.setText(DateTimeUtils.getDateTimeStr(now));
        dateTimeUnit = new ChoiceBox();
        dateTimeUnit.getItems().addAll("ms", "s");
        dateTimeUnit.getSelectionModel().select(0);
        dateTimeConvertBtn = new Button("Convert");
        dateTimeConvertBtn.setDefaultButton(true);
        dateTimeConvertResultText = new TextField();
        var dateTimeBox = makeHBoxContainer("Date Time", SPACING * 8,
                dateTimeText, dateTimeConvertBtn, dateTimeConvertResultText, dateTimeUnit);
        addNode(dateTimeBox);
    }

    private void initListener() {
        timestampConvertBtn.setOnAction(event -> convertTimestampToDateTime());
        dateTimeConvertBtn.setOnAction(event -> converDateTimeToTimestamp());
    }

    private void convertTimestampToDateTime() {
        String timestampStr = timestampText.getText();
        if (timestampStr == null || timestampStr.isBlank()) {
            return;
        }
        long timestamp = Long.parseLong(timestampStr);
        if (timestampUnit.getSelectionModel().getSelectedIndex() == 1) { // in seconds
            timestamp = timestamp * 1000;
        }
        timestampConvertResultText.setText(DateTimeUtils.getDateTimeStr(timestamp));
    }

    private void converDateTimeToTimestamp() {
        String dateTimeStr = dateTimeText.getText();
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return;
        }
        long timestamp = DateTimeUtils.getTimestamp(dateTimeStr);
        if (dateTimeUnit.getSelectionModel().getSelectedIndex() == 1) { // in seconds
            timestamp = timestamp / 1000;
        }
        dateTimeConvertResultText.setText(String.valueOf(timestamp));
    }


}
