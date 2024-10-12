package com.github.godshang.devtool.layout;

import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import com.github.godshang.devtool.common.Lazy;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;

import static atlantafx.base.theme.Styles.TEXT_BOLD;
import static atlantafx.base.theme.Styles.TEXT_SMALL;
import static atlantafx.base.theme.Styles.TEXT_SUBTLE;
import static atlantafx.base.theme.Styles.TITLE_1;

public class Sidebar extends VBox {

    private final NavTree navTree;

    private final Lazy<ThemeDialog> themeDialog;

    public Sidebar(MainModel model) {
        super();

        navTree = new NavTree(model);

        themeDialog = new Lazy<>(() -> {
            var dialog = new ThemeDialog();
            dialog.setClearOnClose(true);
            return dialog;
        });

        initView();
    }

    public void begForFocus() {
        navTree.requestFocus();
    }

    private void initView() {
        VBox.setVgrow(navTree, Priority.ALWAYS);

        setId("sidebar");
        var header = new Header();
        getChildren().addAll(header, navTree, createFooter());
    }

    private HBox createFooter() {
        var versionLabel = new Label("v" + System.getProperty("app.version"));
        versionLabel.getStyleClass().addAll("version", TEXT_SMALL, TEXT_BOLD, TEXT_SUBTLE);

        var footer = new HBox();
        footer.getStyleClass().add("footer");
        footer.getChildren().add(versionLabel);
        return footer;
    }

    private void openThemeDialog() {
        var dialog = themeDialog.get();
        dialog.show(getScene());
        Platform.runLater(dialog::requestFocus);
    }

    private class Header extends VBox {
        public Header() {
            super();

            getStyleClass().add("header");
            getChildren().setAll(createLogo());
        }

        private HBox createLogo() {
            // var image = new ImageView(
            //         new Image(Resources.getResource("assets/icons/app-icon.png").toString())
            // );
            // image.setFitWidth(32);
            // image.setFitHeight(32);
            //
            // var imageBorder = new Insets(1);
            // var imageBox = new StackPane(image);
            // imageBox.getStyleClass().add("image");
            // imageBox.setPadding(imageBorder);
            // imageBox.setPrefSize(
            //         image.getFitWidth() + imageBorder.getRight() * 2,
            //         image.getFitWidth() + imageBorder.getTop() * 2
            // );
            // imageBox.setMaxSize(
            //         image.getFitHeight() + imageBorder.getTop() * 2,
            //         image.getFitHeight() + imageBorder.getRight() * 2
            // );

            var titleLabel = new Label(System.getProperty("app.name"));
            titleLabel.getStyleClass().addAll(TITLE_1);

            var themeSwitchBtn = new Button();
            themeSwitchBtn.getStyleClass().add("palette");
            themeSwitchBtn.setGraphic(new FontIcon(Material2MZ.WB_SUNNY));
            themeSwitchBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            themeSwitchBtn.getStyleClass().addAll(Styles.BUTTON_CIRCLE, Styles.FLAT);
            themeSwitchBtn.setAlignment(Pos.CENTER_RIGHT);
            themeSwitchBtn.setOnAction(e -> openThemeDialog());

            var header = new HBox();
            header.setPadding(new Insets(10));
            header.getChildren().addAll(titleLabel, new Spacer(), themeSwitchBtn);
            return header;
        }
    }
}
