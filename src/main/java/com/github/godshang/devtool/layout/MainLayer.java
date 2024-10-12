package com.github.godshang.devtool.layout;

import com.github.godshang.devtool.page.Page;
import com.github.godshang.devtool.page.PageFactory;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Optional;

public class MainLayer extends BorderPane {

    private static final Logger log = LoggerFactory.getLogger(MainLayer.class);

    static final int PAGE_TRANSITION_DURATION = 500; // ms
    static final int SPACING = 10;

    private final MainModel mainModel = new MainModel();
    private final Sidebar sidebar = new Sidebar(mainModel);
    private final StackPane subLayerPane = new StackPane();
    private final TabPane tabPane = new TabPane();

    public MainLayer() {
        super();

        initView();
        initListener();

//        mainModel.navigate(MainModel.DEFAULT_PAGE);

        // keyboard navigation won't work without focus
        Platform.runLater(sidebar::begForFocus);
    }

    private void initView() {
        sidebar.setMinWidth(ApplicationWindow.SIDEBAR_WIDTH);
        sidebar.setMaxWidth(ApplicationWindow.SIDEBAR_WIDTH);

        setId("main");
        setLeft(sidebar);
//        setCenter(subLayerPane);
        setCenter(tabPane);
    }

    private void initListener() {
        mainModel.selectedPageProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
//                    loadPage(newValue);
                    openTab(newValue);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        });

        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                mainModel.navigate((Class<? extends Page>) newValue.getUserData());
            }
        });
    }

    private void loadPage(Class<? extends Page> pageClass) throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        final Page prevPage = (Page) subLayerPane.getChildren().stream()
                .filter(e -> e instanceof Page)
                .findFirst()
                .orElse(null);
        final Page nextPage = pageClass.getDeclaredConstructor().newInstance();
//        final Page nextPage = SingletonFactory.getWeakReferenceInstance(pageClass);

        // startup, no prev page, no animation
        if (getScene() == null) {
            subLayerPane.getChildren().add(nextPage.getView());
            return;
        }

        Objects.requireNonNull(prevPage);

        // reset previous page, e.g. to free resources
        prevPage.reset();

        // animate switching between pages
        subLayerPane.getChildren().add(nextPage.getView());
        subLayerPane.getChildren().remove(prevPage.getView());
        var transition = new FadeTransition(Duration.millis(PAGE_TRANSITION_DURATION), nextPage.getView());
        transition.setFromValue(0.0);
        transition.setToValue(1.0);
        transition.setOnFinished(t -> {
            if (nextPage instanceof Pane nextPane) {
                nextPane.toFront();
            }
        });
        transition.play();
    }

    private void openTab(Class<? extends Page> pageClass) throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        String id = pageClass.getSimpleName();
        Optional<Tab> targetTab = getTabByPageClass(pageClass);
        if (targetTab.isPresent()) {
            tabPane.getSelectionModel().select(targetTab.get());
            return;
        }

//        final Page newPage = pageClass.getDeclaredConstructor().newInstance();
        final Page newPage = PageFactory.getWeakReferenceInstance(pageClass);

        Tab newTab = new Tab(newPage.getName(), newPage.getView());
        newTab.setUserData(pageClass);
        newTab.setClosable(true);
        newTab.setOnClosed(event -> {
            newPage.reset();
            PageFactory.removeWeakReferenceInstance(newPage.getClass());
        });
        // context menu
        var contextMenu = new ContextMenu();
        contextMenu.setUserData(pageClass);
        var closeItem = new MenuItem("Close");
        closeItem.setOnAction(event -> {
            MenuItem item = (MenuItem) event.getSource();
            getTabByPageClass((Class<? extends Page>) item.getParentPopup().getUserData()).ifPresent(tab -> {
                tabPane.getTabs().remove(tab);
            });
        });
        var closeOtherItem = new MenuItem("Close Other Tabs");
        closeOtherItem.setOnAction(event -> {
            MenuItem item = (MenuItem) event.getSource();
            getTabByPageClass((Class<? extends Page>) item.getParentPopup().getUserData()).ifPresent(tab -> {
                tabPane.getTabs().removeIf(e -> e != tab);
            });
        });
        var closeAllItem = new MenuItem("Close All Tabs");
        closeAllItem.setOnAction(event -> {
            tabPane.getTabs().clear();
        });
        contextMenu.getItems().addAll(closeItem, closeOtherItem, closeAllItem);
        newTab.setContextMenu(contextMenu);
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(newTab);
    }

    private Optional<Tab> getTabByPageClass(Class<? extends Page> pageClass) {
        return tabPane.getTabs().stream().filter(e -> e.getUserData().equals(pageClass)).findAny();
    }
}
