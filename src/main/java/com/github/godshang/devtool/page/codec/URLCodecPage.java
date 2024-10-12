package com.github.godshang.devtool.page.codec;

import javafx.scene.control.Label;

public class URLCodecPage extends AbstractCodecPage {

    public static final String NAME = "URL encode/decode";

    public URLCodecPage() {
        super();
        addNode(0, new Label("Encode text to URL-encoded format (also known as \"percent-encoded\"), or decode from it.\n"));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected CodecEnum getCodecEnum() {
        return CodecEnum.URL;
    }
}
