package com.github.godshang.devtool.page.codec;

import javafx.scene.control.Label;

public class Base64CodecPage extends AbstractCodecPage {

    public static final String NAME = "Base64 encode/cecode";

    public Base64CodecPage() {
        super();
        addNode(0, new Label("Simply encode and decode strings into their base64 representation."));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected CodecEnum getCodecEnum() {
        return CodecEnum.BASE64;
    }
}
