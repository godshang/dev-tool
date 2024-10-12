package com.github.godshang.devtool.page.json;

import atlantafx.base.theme.Styles;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.godshang.devtool.page.AbstractPage;
import com.github.godshang.devtool.util.FXUtils;
import com.github.godshang.devtool.util.MapperUtils;
import com.github.godshang.devtool.util.StringUtils;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.kordamp.ikonli.boxicons.BoxiconsRegular;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsonViewPage extends AbstractPage {

    public static final String NAME = "JSON view";
    private static final int MIN_WIDTH = 200;

    private TextArea textArea;
    private TreeView treeView;
    private TreeItem<KV> root;
    private Button expandAllBtn;
    private Button collapseAllBtn;

    private ChangeListener<String> textAreaChangeListener = (observable, oldValue, newValue) -> buildTree(newValue);

    public JsonViewPage() {
        super();
        initView();
        initListener();
    }

    @Override
    public void reset() {
        super.reset();

        expandAllBtn.setOnAction(null);
        collapseAllBtn.setOnAction(null);
        textArea.textProperty().removeListener(textAreaChangeListener);
        treeView.setOnKeyPressed(null);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void initView() {
        textArea = new TextArea();
        textArea.setPromptText("Paste JSON here ...");
        textArea.setMinWidth(MIN_WIDTH);
        var leftContainer = new VBox();
        leftContainer.setPadding(new Insets(0, SPACING, 0, 0));
        leftContainer.getChildren().addAll(textArea);

        expandAllBtn = new Button("Expand All");
        collapseAllBtn = new Button("Fold All");
        root = new TreeItem<>(new KV("JSON"));
        treeView = new TreeView<>(root);
        treeView.getStyleClass().add(Styles.DENSE);
        treeView.setContextMenu(createContextMenu());

        var rightBtnRow = makeHBoxContainer(expandAllBtn, collapseAllBtn);
        var rightContainer = new VBox(SPACING);
        rightContainer.setPadding(new Insets(0, 0, 0, SPACING));
        rightContainer.getChildren().addAll(rightBtnRow, treeView);

        var splitPane = new SplitPane(leftContainer, rightContainer);
        VBox.setVgrow(textArea, Priority.ALWAYS);
        VBox.setVgrow(treeView, Priority.ALWAYS);
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        addNode(splitPane);
    }

    private void initListener() {
        expandAllBtn.setOnAction(event -> expandAll(root, true));
        collapseAllBtn.setOnAction(event -> expandAll(root, false));
        textArea.textProperty().addListener(textAreaChangeListener);
        treeView.setOnKeyPressed((keyEvent) -> {
            if (keyEvent.getEventType() != KeyEvent.KEY_PRESSED) {
                return;
            }
            if (keyEvent.getCode() == KeyCode.ENTER) {
                TreeItem<KV> treeItem = (TreeItem<KV>) treeView.getSelectionModel().getSelectedItem();
                treeItem.setExpanded(!treeItem.isExpanded());
            }
        });
    }

    private ContextMenu createContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem copyKeyItem = new MenuItem("Copy Key");
        MenuItem copyValueItem = new MenuItem("Copy Value");
        contextMenu.getItems().addAll(copyKeyItem, copyValueItem);

        copyKeyItem.setOnAction(event -> {
            TreeItem<KV> treeItem = (TreeItem<KV>) treeView.getSelectionModel().getSelectedItem();
            KV kv = treeItem.getValue();
            FXUtils.putClipboard(kv.getKey());
        });
        copyValueItem.setOnAction(event -> {
            TreeItem<KV> treeItem = (TreeItem<KV>) treeView.getSelectionModel().getSelectedItem();
            KV kv = treeItem.getValue();
            FXUtils.putClipboard(kv.getValue().asText());
        });
        return contextMenu;
    }

    private void buildTree(String json) {
        if (StringUtils.isBlank(json)) {
            return;
        }
        JsonNode jsonNode = MapperUtils.readTree(json);
        if (jsonNode == null) {
            return;
        }
        if (jsonNode.isArray()) {
            root.setGraphic(new FontIcon(BoxiconsRegular.BRACKET));
        } else {
            root.setGraphic(new FontIcon(BoxiconsRegular.CODE_CURLY));
        }
        buildTreeItem(jsonNode, root);
    }

    private void buildTreeItem(JsonNode jsonNode, TreeItem<KV> parent) {
        List<TreeItem<KV>> childList = new ArrayList<>();
        if (jsonNode.isArray()) {
            int i = 0;
            for (JsonNode node : jsonNode) {
                TreeItem<KV> treeItem;
                if (node.isValueNode()) {
                    treeItem = new TreeItem<>(new KV(null, node), new FontIcon(BoxiconsRegular.CUBE));
                } else {
                    treeItem = new TreeItem<>(new KV(String.valueOf(i++)), new FontIcon(BoxiconsRegular.CODE_CURLY));
                    buildTreeItem(node, treeItem);
                }
                childList.add(treeItem);
                parent.getChildren().setAll(childList);
            }
        } else if (jsonNode.isObject()) {
            Iterator<String> iter = jsonNode.fieldNames();
            while (iter.hasNext()) {
                String key = iter.next();
                JsonNode valueNode = jsonNode.get(key);
                TreeItem<KV> treeItem = null;
                if (valueNode.isValueNode()) {
                    treeItem = new TreeItem<>(new KV(key, valueNode), new FontIcon(BoxiconsRegular.CUBE));
                } else if (valueNode.isObject()) {
                    treeItem = new TreeItem<>(new KV(key), new FontIcon(BoxiconsRegular.CODE_CURLY));
                    buildTreeItem(valueNode, treeItem);
                } else if (valueNode.isArray()) {
                    treeItem = new TreeItem<>(new KV(key), new FontIcon(BoxiconsRegular.BRACKET));
                    buildTreeItem(valueNode, treeItem);
                }

                if (treeItem != null) {
                    childList.add(treeItem);
                    parent.getChildren().setAll(childList);
                }
            }
        }
    }

    private void expandAll(TreeItem<?> item, boolean expanded) {
        if (item != null && !item.isLeaf()) {
            item.setExpanded(expanded);
            for (TreeItem<?> child : item.getChildren()) {
                expandAll(child, expanded);
            }
        }
    }


    @AllArgsConstructor
    @Getter
    private static class KV {
        String key;
        JsonNode value;

        public KV(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            StringBuilder text = new StringBuilder();
            if (key != null) {
                text.append(key).append(":");
            }
            if (value != null) {
                if (value.isTextual()) {
                    text.append(" \"").append(value.asText()).append("\"");
                } else {
                    text.append(" ").append(value.asText());
                }
            }
            return text.toString();
        }
    }
}
