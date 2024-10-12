package com.github.godshang.devtool;

import atlantafx.base.theme.CupertinoLight;
import com.github.godshang.devtool.common.DefaultExceptionHandler;
import com.github.godshang.devtool.common.Resources;
import com.github.godshang.devtool.layout.ApplicationWindow;
import com.github.godshang.devtool.theme.ThemeManager;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Launcher extends Application {

    public static final boolean IS_DEV_MODE = "DEV".equalsIgnoreCase(
            Resources.getPropertyOrEnv("atlantafx.mode", "ATLANTAFX_MODE")
    );

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws Exception {
        Thread.currentThread().setUncaughtExceptionHandler(new DefaultExceptionHandler(stage));
        // Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());

        loadApplicationProperties();

        var antialiasing = Platform.isSupported(ConditionalFeature.SCENE3D)
                ? SceneAntialiasing.BALANCED
                : SceneAntialiasing.DISABLED;
        var root = new ApplicationWindow();
        Scene scene = new Scene(root, ApplicationWindow.MIN_WIDTH + 80, 600, false, antialiasing);
        // scene.getStylesheets().addAll(Resources.resolve("assets/styles/index.css"),
        //         Resources.resolve("assets/styles/custom.css"));

        var tm = ThemeManager.getInstance();
        tm.setScene(scene);
        tm.setTheme(tm.getDefaultTheme());

        stage.setScene(scene);
        stage.setTitle(System.getProperty("app.name"));
        loadIcons(stage);
        stage.setResizable(true);
//        stage.setMaximized(true);
        stage.setOnCloseRequest(t -> Platform.exit());

        Platform.runLater(() -> {
            stage.show();
            stage.requestFocus();
        });
    }

    private void loadIcons(Stage stage) {
        int iconSize = 16;
        while (iconSize <= 1024) {
            // we could use the square icons for Windows here
            stage.getIcons().add(new Image(Resources.getResourceAsStream("assets/icons/app-icon-" + iconSize + ".png")));
            iconSize *= 2;
        }
    }

    private void loadApplicationProperties() {
        Properties properties = new Properties();
        try (InputStreamReader in = new InputStreamReader(Resources.getResourceAsStream("application.properties"), UTF_8)) {
            properties.load(in);
            properties.forEach((key, value) -> System.setProperty(String.valueOf(key), String.valueOf(value)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
