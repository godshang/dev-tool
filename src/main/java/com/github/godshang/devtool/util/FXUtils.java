package com.github.godshang.devtool.util;

import com.github.godshang.devtool.common.DevToolException;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;
import java.util.function.Consumer;

public class FXUtils {

    public static Window getWindow() {
        return Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null);
    }

    public static void putClipboard(String string) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(string);
        clipboard.setContent(content);
    }

    public static File chooseFileToOpen(String[][] extensionFilters) {
        var fileChooser = createFileChooser("Open File", extensionFilters);
        return fileChooser.showOpenDialog(getWindow());
    }

    public static File chooseFileToSave(String[][] extensionFilters) {
        var fileChooser = createFileChooser("Save As", extensionFilters);
        return fileChooser.showSaveDialog(getWindow());
    }

    private static FileChooser createFileChooser(String title, String[][] extensionFilters) {
        var fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(Paths.get(System.getProperty("user.home")).toFile());
        if (extensionFilters != null) {
            for (String[] filter : extensionFilters) {
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(filter[0], filter[1]));
            }
        }
        return fileChooser;
    }


    public static void buildImageViewContextMenu(ImageView imageView) {
        Window window = getWindow();

        var copyMenuItem = new MenuItem("Copy Image");
        copyMenuItem.setOnAction(event -> {
            if (imageView.getImage() != null) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putImage(imageView.getImage());
                clipboard.setContent(content);
            }
        });

        var saveAsMenuItem = new MenuItem("Save As File");
        saveAsMenuItem.setOnAction(event -> {
            if (imageView.getImage() == null) {
                return;
            }
            if (window == null) {
                return;
            }

            var fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");
            fileChooser.setInitialDirectory(Paths.get(System.getProperty("user.home")).toFile());
            fileChooser.setInitialFileName("snapshot.png");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png"),
                    new FileChooser.ExtensionFilter("Image Files", "*.jpg"),
                    new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            File output = fileChooser.showSaveDialog(window);
            if (output == null) {
                return;
            }
            try {
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(imageView.getImage(), null);
                ImageIO.write(bufferedImage, FileUtils.getExtensionName(output), output);
            } catch (Exception e) {
                throw new DevToolException(e.getMessage(), e);
            }
        });

        var contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(copyMenuItem, saveAsMenuItem);
        imageView.setOnContextMenuRequested(event -> {
            contextMenu.show(imageView, event.getScreenX(), event.getScreenY());
        });
    }

    public static void screenCapture(Consumer<WritableImage> consumer) {
        Window window = FXUtils.getWindow();
        if (window == null || !(window instanceof Stage)) {
            return;
        }
        Stage primaryStage = (Stage) window;
        primaryStage.setIconified(true);
        Stage stage = new Stage();
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color: #85858522");
        // 场景设置白色全透明
        Scene scene = new Scene(anchorPane);
        scene.setFill(Paint.valueOf("#ffffff00"));
        stage.setScene(scene);
        // 清除全屏中间提示文字
        stage.setFullScreenExitHint("");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setFullScreen(true);
        stage.show();

        var ref = new Object() {
            double start_x;     // 切图区域的起始位置x
            double start_y;     // 切图区域的起始位置y
            double w;           // 切图区域宽
            double h;           // 切图区域高
            HBox hBox;          // 切图区域
        };
        // 切图窗口绑定鼠标按下事件
        anchorPane.setOnMousePressed(event -> {
            // 清除锚点布局中所有子元素
            anchorPane.getChildren().clear();
            // 创建切图区域
            ref.hBox = new HBox();
            // 设置背景保证能看到切图区域桌面
            ref.hBox.setBackground(null);
            // 设置边框
            ref.hBox.setBorder(new Border(new BorderStroke(Paint.valueOf("#c03700"), BorderStrokeStyle.SOLID,
                    null, new BorderWidths(3))));
            anchorPane.getChildren().add(ref.hBox);
            // 记录并设置起始位置
            ref.start_x = event.getSceneX();
            ref.start_y = event.getSceneY();
            AnchorPane.setLeftAnchor(ref.hBox, ref.start_x);
            AnchorPane.setTopAnchor(ref.hBox, ref.start_y);
        });
        // 绑定鼠标按下拖拽的事件
        anchorPane.setOnMouseDragged(event -> {
            // 用label记录切图区域的长宽
            var label = new Label();
            label.setAlignment(Pos.CENTER);
            label.setPrefHeight(30);
            label.setPrefWidth(170);
            anchorPane.getChildren().add(label);
            AnchorPane.setLeftAnchor(label, ref.start_x + 30);
            AnchorPane.setTopAnchor(label, ref.start_y);
            label.setTextFill(Paint.valueOf("#ffffff"));// 白色填充
            label.setStyle("-fx-background-color: #000000");// 黑背景
            // 计算宽高并且完成切图区域的动态效果
            ref.w = Math.abs(event.getSceneX() - ref.start_x);
            ref.h = Math.abs(event.getSceneY() - ref.start_y);
            ref.hBox.setPrefWidth(ref.w);
            ref.hBox.setPrefHeight(ref.h);
            label.setText("w：" + ref.w + " h：" + ref.h);
        });
        // 绑定鼠标松开事件
        anchorPane.setOnMouseReleased(event -> {
            // 记录最终长宽
            ref.w = Math.abs(event.getSceneX() - ref.start_x);
            ref.h = Math.abs(event.getSceneY() - ref.start_y);
            anchorPane.setStyle("-fx-background-color: #00000000");
            // // 添加剪切按钮，并显示在切图区域的底部
            // var b = new Button("Cut");
            // ref.hBox.setBorder(new Border(new BorderStroke(Paint.valueOf("#85858544"), BorderStrokeStyle.SOLID,
            //         null, new BorderWidths(3))));
            // ref.hBox.getChildren().add(b);
            // ref.hBox.setAlignment(Pos.BOTTOM_RIGHT);
            // // 为切图按钮绑定切图事件
            // b.setOnAction(event1 -> {
            // });

            // 切图辅助舞台消失
            stage.close();
            WritableImage writableImage;
            try {
                writableImage = capterImg(ref.start_x, ref.start_y, ref.w, ref.h);
            } catch (Exception e) {
                throw new DevToolException(e.getMessage(), e);
            }
            if (writableImage != null) {
                consumer.accept(writableImage);
            }
            // 主舞台还原
            primaryStage.setIconified(false);
        });

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                stage.close();
                primaryStage.setIconified(false);
            }
        });
    }

    public static WritableImage capterImg(double start_x, double start_y, double w, double h) throws Exception {
        // 利用awt中的方法，通过记录的起始点和长宽完成屏幕截图
        Robot robot = new Robot();
        Rectangle re = new Rectangle((int) start_x, (int) start_y, (int) w, (int) h);
        BufferedImage screenCapture = robot.createScreenCapture(re);
        // 截图图片背景透明处理
        // BufferedImage bufferedImage = Picture4.transferAlpha(screenCapture);
        // 不进行背景透明处理
        BufferedImage bufferedImage = screenCapture;
        // 转换图片格式展示在主舞台的场景中
        WritableImage writableImage = SwingFXUtils.toFXImage(bufferedImage, null);
        return writableImage;
    }
}
