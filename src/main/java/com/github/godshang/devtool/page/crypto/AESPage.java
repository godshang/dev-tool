package com.github.godshang.devtool.page.crypto;

import com.github.godshang.devtool.util.CryptoUtils;

public class AESPage extends AbstractCryptoPage {

    public static final String NAME = "AES encrypt/decrypt";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected CryptoUtils.Algorithm getAlgorithm() {
        return CryptoUtils.Algorithm.AES;
    }
}
