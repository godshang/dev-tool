package com.github.godshang.devtool.page.generation;

import atlantafx.base.theme.Styles;
import com.github.godshang.devtool.page.AbstractPage;
import com.github.godshang.devtool.util.CrontabUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class CronGeneratePage extends AbstractPage {

    private static final Logger log = LoggerFactory.getLogger(CronGeneratePage.class);

    public static final String NAME = "Cron generator";

    private static final String DEFAULT_LINUX_CRON = "0 */12 * * *";
    private static final String DEFAULT_SPRING_CRON = "0 0 18 28-31 * ?";
    private static final String DEFAULT_QUARTZ_CRON = "0 0 18 L * ?";

    private static final String LINUX_DESC = """
            Linux
            *    *    *    *    *
            -    -    -    -    -
            |    |    |    |    |
            |    |    |    |    +----- day of week (0 - 7) (Sunday=0 or 7) OR sun,mon,tue,wed,thu,fri,sat
            |    |    |    +---------- month (1 - 12) OR jan,feb,mar,apr ...
            |    |    +--------------- day of month (1 - 31)
            |    +-------------------- hour (0 - 23)
            +------------------------- minute (0 - 59)
            """;
    private static final String SPRING_DESC = """
            Java(Spring)
            *    *    *    *    *    *
            -    -    -    -    -    -
            |    |    |    |    |    |
            |    |    |    |    |    +----- day of week (0 - 7) (Sunday=0 or 7) OR sun,mon,tue,wed,thu,fri,sat
            |    |    |    |    +---------- month (1 - 12) OR jan,feb,mar,apr ...
            |    |    |    +--------------- day of month (1 - 31)
            |    |    +-------------------- hour (0 - 23)
            |    +------------------------- min (0 - 59)
            +------------------------------ second (0 - 59)
            """;
    private static final String QUARTZ_DESC = """
            Java(Quartz)
            *    *    *    *    *    *    *
            -    -    -    -    -    -    -
            |    |    |    |    |    |    |
            |    |    |    |    |    |    + year [optional]
            |    |    |    |    |    +----- day of week (1 - 7) sun,mon,tue,wed,thu,fri,sat
            |    |    |    |    +---------- month (1 - 12) OR jan,feb,mar,apr ...
            |    |    |    +--------------- day of month (1 - 31)
            |    |    +-------------------- hour (0 - 23)
            |    +------------------------- min (0 - 59)
            +------------------------------ second (0 - 59)
            """;

    public record TableData(String field, String required, String value, String special, String comment) {

        public static ObservableList<TableData> create() {
            ObservableList<TableData> list = FXCollections.observableArrayList();
            list.add(new TableData("Seconds", "Y", "0-59", "*,-", ""));
            list.add(new TableData("Minutes", "Y", "0-59", "*,-", ""));
            list.add(new TableData("Hours", "Y", "0-23", "*,-", ""));
            list.add(new TableData("Day of month", "Y", "1-31", "*,-?LW", ""));
            list.add(new TableData("month", "Y", "1–12 or JAN–DEC", "*,-", ""));
            list.add(new TableData("Day of week", "Y", "0–7 or SUN–SAT", "*,-?L#", ""));
            list.add(new TableData("Year", "N", "1970–2099", "*,-", ""));
            return list;
        }
    }

    private ToggleGroup crontabToggleGroup;
    private TextField crontabTextField;
    private Button showExecutionButton;
    private TextArea descTextArea;
    private ListView<String> resultListView;

    private ChangeListener<Toggle> crontabChangeListener = (observable, oldValue, newValue) -> {
        if (newValue == null) {
            return;
        }
        CrontabUtils.CrontabType crontabType = (CrontabUtils.CrontabType) newValue.getUserData();
        switch (crontabType) {
            case Linux -> {
                crontabTextField.setText(DEFAULT_LINUX_CRON);
                descTextArea.setText(LINUX_DESC);
            }
            case Spring -> {
                crontabTextField.setText(DEFAULT_SPRING_CRON);
                descTextArea.setText(SPRING_DESC);
            }
            case Quartz -> {
                crontabTextField.setText(DEFAULT_QUARTZ_CRON);
                descTextArea.setText(QUARTZ_DESC);
            }
        }
    };

    public CronGeneratePage() {
        super();
        initView();
        initListener();
    }

    @Override
    public void reset() {
        super.reset();
        showExecutionButton.setOnAction(null);
        crontabToggleGroup.selectedToggleProperty().removeListener(crontabChangeListener);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void initView() {
        var typeLabel = new Label("Type");
        var typeBox = new HBox();

        var linuxRadioButton = new ToggleButton("Linux");
        linuxRadioButton.getStyleClass().add(Styles.LEFT_PILL);
        linuxRadioButton.setUserData(CrontabUtils.CrontabType.Linux);
        linuxRadioButton.setSelected(true);
        var javaSpringRadioButton = new ToggleButton("Java(Srping)");
        javaSpringRadioButton.getStyleClass().add(Styles.CENTER_PILL);
        javaSpringRadioButton.setUserData(CrontabUtils.CrontabType.Spring);
        var javaQuartzRadioButton = new ToggleButton("Java(Quartz)");
        javaQuartzRadioButton.getStyleClass().add(Styles.RIGHT_PILL);
        javaQuartzRadioButton.setUserData(CrontabUtils.CrontabType.Quartz);
        crontabToggleGroup = new ToggleGroup();
        crontabToggleGroup.getToggles().addAll(linuxRadioButton, javaSpringRadioButton, javaQuartzRadioButton);
        typeBox.getChildren().addAll(linuxRadioButton, javaSpringRadioButton, javaQuartzRadioButton);

        var expressionLabel = new Label("Cron Expression");
        crontabTextField = new TextField();
        crontabTextField.setText(DEFAULT_LINUX_CRON);

        showExecutionButton = new Button("Show Execution");
        showExecutionButton.setDefaultButton(true);

        var gridPane = new GridPane();
        gridPane.setHgap(SPACING);
        gridPane.setVgap(SPACING);
        gridPane.add(typeLabel, 0, 0);
        gridPane.add(typeBox, 1, 0);
        gridPane.add(expressionLabel, 0, 1);
        gridPane.add(crontabTextField, 1, 1);
        gridPane.add(showExecutionButton, 2, 1);
        addNode(gridPane);

        resultListView = new ListView<>();
        resultListView.getStyleClass().add(Styles.BORDERED);
        resultListView.setMinHeight(SPACING * 25);
        addNode(resultListView);

        descTextArea = new TextArea(LINUX_DESC);
        descTextArea.setEditable(false);
        descTextArea.setMinHeight(SPACING * 25);
        addNode(descTextArea);

        var col1 = new TableColumn<TableData, String>("Field");
        col1.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().field())
        );
        var col2 = new TableColumn<TableData, String>("Required");
        col2.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().required())
        );
        var col3 = new TableColumn<TableData, String>("Allowed Value");
        col3.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().value())
        );
        var col4 = new TableColumn<TableData, String>("Allowed Special Value");
        col4.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().special())
        );
        var tableView = new TableView(TableData.create());
        tableView.getColumns().setAll(col1, col2, col3, col4);
        tableView.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );
        tableView.setMinHeight(SPACING * 35);
        addNode(tableView);
    }

    private void initListener() {
        showExecutionButton.setOnAction(event -> showNextExecution());
        crontabToggleGroup.selectedToggleProperty().addListener(crontabChangeListener);
    }

    private void showNextExecution() {
        String expression = crontabTextField.getText();
        if (expression == null || expression.isEmpty()) {
            return;
        }
        CrontabUtils.CrontabType crontabType = (CrontabUtils.CrontabType) crontabToggleGroup.getSelectedToggle().getUserData();
        List<String> executionList = Collections.emptyList();
        try {
            executionList = CrontabUtils.getNextExecution(expression, crontabType, 10);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            popupNotification(e.getMessage(), false);
        }
        resultListView.getItems().setAll(executionList);
    }
}
