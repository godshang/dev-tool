package com.github.godshang.devtool.page.crypto;

import com.github.godshang.devtool.util.CryptoUtils;

public class DESPage extends AbstractCryptoPage {

    public static final String NAME = "DES encrypt/decrypt";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected CryptoUtils.Algorithm getAlgorithm() {
        return CryptoUtils.Algorithm.DES;
    }
}
