package com.github.godshang.devtool.page.other;

import com.github.godshang.devtool.page.AbstractPage;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class TestPage extends AbstractPage {

    public static final String NAME = "TEST";

    public TestPage() {
        super();
        initView();
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void initView() {
        for (int i = 0; i < 10; i++) {
            addNode(create());
        }
    }

    private HBox create() {
        var label = new Label();
        label.setMinSize(120, 80);
        label.setAlignment(Pos.CENTER);
        label.setStyle("-fx-background-color:-color-accent-subtle;");

        var box = new HBox(label);
        box.setAlignment(Pos.CENTER);
        return box;
    }
}
