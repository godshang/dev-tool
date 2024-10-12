package com.github.godshang.devtool.page.crypto;

import com.github.godshang.devtool.util.CryptoUtils;

public class TripleDESPage extends AbstractCryptoPage {

    public static final String NAME = "3DES encrypt/decrypt";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected CryptoUtils.Algorithm getAlgorithm() {
        return CryptoUtils.Algorithm.DESede;
    }
}
