package com.github.godshang.devtool.page;

import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import com.github.godshang.devtool.util.NodeUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED;
import static javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER;

public abstract class AbstractPage extends StackPane implements Page {

    protected final static int SPACING = 10;
//    protected final String STYLE_TEXT_AREA_COLOR = "-fx-text-fill: #a11;";
//    protected final String STYLE_TEXT_AREA_MONOSPACE = "-fx-font-family: monospace;";

    protected final VBox userContent = new VBox();
    protected final StackPane userContentArea = new StackPane(userContent);
    protected boolean isRendered = false;

    private final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);

    protected AbstractPage() {
        super();

        userContent.getStyleClass().add("user-content");
        getStyleClass().add("page");

        createPageLayout();
    }

    protected void createPageLayout() {
//        userContent.setStyle("-fx-border-color: red");
//        userContentArea.setStyle("-fx-border-color: black");

        userContentArea.setAlignment(Pos.TOP_CENTER);
        userContent.setMinWidth(Page.MAX_WIDTH);
//        userContent.setMaxWidth(Page.MAX_WIDTH);

        var scrollPane = new ScrollPane(userContentArea);
        NodeUtils.setScrollConstraints(scrollPane, AS_NEEDED, true, NEVER, true);
        scrollPane.setMaxHeight(20_000);

        getChildren().setAll(scrollPane);
    }

    @Override
    public Parent getView() {
        return this;
    }

    @Override
    public void reset() {

    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        if (isRendered) {
            return;
        }

        isRendered = true;
        onRendered();
    }

    protected void onRendered() {
    }

    protected void addNode(Node node) {
        userContent.getChildren().add(node);
    }

    protected void addNode(int index, Node node) {
        userContent.getChildren().add(index, node);
    }

    protected void popupNotification(String msg) {
        popupNotification(msg, null);
    }

    protected void popupNotification(String msg, Boolean success) {
        var notify = new Notification(msg, new FontIcon(Material2OutlinedAL.HELP_OUTLINE));
        notify.getStyleClass().addAll(Styles.ELEVATED_1);
        if (Objects.nonNull(success)) {
            notify.getStyleClass().add(success ? Styles.SUCCESS : Styles.DANGER);
        }

        notify.setPrefHeight(Region.USE_PREF_SIZE);
        notify.setMaxHeight(Region.USE_PREF_SIZE);
        StackPane.setAlignment(notify, Pos.TOP_RIGHT);
        StackPane.setMargin(notify, new Insets(10, 10, 0, 0));

        var closeHanlder = new Runnable() {
            @Override
            public void run() {
                var out = Animations.slideOutUp(notify, Duration.millis(250));
                out.setOnFinished(f -> getChildren().remove(notify));
                out.playFromStart();
            }
        };

        notify.setOnClose(e -> closeHanlder.run());
        scheduledExecutorService.schedule(closeHanlder, 2000, TimeUnit.MILLISECONDS);

        if (!getChildren().contains(notify)) {
            getChildren().add(notify);
        }
        Animations.slideInDown(notify, Duration.millis(250)).playFromStart();
    }

    protected HBox makeHBoxContainer(Node... nodes) {
        var container = new HBox(SPACING);
        container.setAlignment(Pos.BASELINE_LEFT);
        container.getChildren().addAll(nodes);
        return container;
    }

    protected HBox makeHBoxContainer(String labelText, int labelWidth, Node... nodes) {
        var label = new Label(labelText);
        label.setMinWidth(labelWidth);

        var container = new HBox(SPACING);
        container.setAlignment(Pos.BASELINE_LEFT);
        container.getChildren().add(label);
        container.getChildren().addAll(nodes);
        return container;
    }
}
