/* SPDX-License-Identifier: MIT */

package com.github.godshang.devtool.layout;

import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Tweaks;
import com.github.godshang.devtool.page.Page;
import com.github.godshang.devtool.util.NodeUtils;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class NavTree extends TreeView<Nav> {

    public NavTree(MainModel model) {
        super();

        getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (!(val instanceof Item item)) {
                return;
            }
            if (!item.isGroup()) {
                model.navigate(item.pageClass());
            }
        });

        model.selectedPageProperty().addListener((obs, old, val) -> {
            if (val != null) {
                getSelectionModel().select(model.getTreeItemForPage(val));
            }
        });

        getStyleClass().addAll(Tweaks.EDGE_TO_EDGE);
        setShowRoot(false);
        rootProperty().bind(model.navTreeProperty());
        setCellFactory(p -> new NavTreeCell());
    }

    ///////////////////////////////////////////////////////////////////////////

    public static final class NavTreeCell extends TreeCell<Nav> {

        private final HBox root;
        private final Label titleLabel;
        private final Node arrowIcon;
        private final Label tagLabel;

        public NavTreeCell() {
            super();

            titleLabel = new Label();
            titleLabel.setGraphicTextGap(10);
            titleLabel.getStyleClass().add("title");

            arrowIcon = new FontIcon();
            arrowIcon.getStyleClass().add("arrow");

            tagLabel = new Label("new");
            tagLabel.getStyleClass().add("tag");

            root = new HBox();
            root.setAlignment(Pos.CENTER_LEFT);
            root.getChildren().setAll(titleLabel, new Spacer(), arrowIcon, tagLabel);
            root.setCursor(Cursor.HAND);
            root.getStyleClass().add("container");
            root.setMaxWidth(ApplicationWindow.SIDEBAR_WIDTH - 10);

            root.setOnMouseClicked(e -> {
                if (!(getTreeItem() instanceof Item item)) {
                    return;
                }

                if (item.isGroup() && e.getButton() == MouseButton.PRIMARY) {
                    item.setExpanded(!item.isExpanded());
                    // scroll slightly above the target
                    getTreeView().scrollTo(getTreeView().getRow(item) - 10);
                }
            });

            getStyleClass().add("nav-tree-cell");
        }

        @Override
        protected void updateItem(Nav nav, boolean empty) {
            super.updateItem(nav, empty);

            if (nav == null || empty) {
                setGraphic(null);
                titleLabel.setText(null);
                titleLabel.setGraphic(null);
            } else {
                setGraphic(root);

                titleLabel.setText(nav.title());
                titleLabel.setGraphic(nav.graphic());

                if (nav.isGroup()) {
                    getStyleClass().add("group");
                } else {
                    getStyleClass().removeIf(e -> Objects.equals(e, "group"));
                }
                NodeUtils.toggleVisibility(arrowIcon, nav.isGroup());
                NodeUtils.toggleVisibility(tagLabel, nav.isTagged());
            }
        }
    }

    public static final class Item extends TreeItem<Nav> {

        private final Nav nav;

        private Item(Nav nav) {
            this.nav = Objects.requireNonNull(nav, "nav");
            setValue(nav);
        }

        public boolean isGroup() {
            return nav.isGroup();
        }

        public Class<? extends Page> pageClass() {
            return nav.pageClass();
        }

        public static Item root() {
            return new Item(Nav.ROOT);
        }

        public static Item group(String title, Node graphic) {
            return new Item(new Nav(title, graphic, null, null));
        }

        public static Item page(String title,
                                Class<? extends Page> pageClass) {
            Objects.requireNonNull(pageClass, "pageClass");
            return new Item(new Nav(title, null, pageClass, Collections.emptyList()));
        }

        public static Item page(String title,
                                Class<? extends Page> pageClass,
                                String... searchKeywords) {
            Objects.requireNonNull(pageClass, "pageClass");
            return new Item(new Nav(title, null, pageClass, List.of(searchKeywords)));
        }
    }
}
