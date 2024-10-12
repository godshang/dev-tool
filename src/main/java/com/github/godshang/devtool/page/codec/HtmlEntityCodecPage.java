package com.github.godshang.devtool.page.codec;

import javafx.scene.control.Label;

public class HtmlEntityCodecPage extends AbstractCodecPage {

    public static final String NAME = "HTML entities escape";

    public HtmlEntityCodecPage() {
        super();
        addNode(0, new Label("Escape or unescape HTML entities (replace characters like <,>, &, \" and \\' with their HTML version)"));
    }

    @Override
    public String getName() {
        return NAME;
    }


    @Override
    protected CodecEnum getCodecEnum() {
        return CodecEnum.HTML_ENTITY;
    }
}
