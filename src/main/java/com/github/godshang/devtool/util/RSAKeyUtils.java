package com.github.godshang.devtool.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class RSAKeyUtils {

    public record Keys(String privateKey, String publicKey) {
    }

    public static Keys generate(int keySize) throws NoSuchAlgorithmException {
        KeyPair keyPair = generate(keySize, "RSA");
        String privateKey = "-----BEGIN RSA PRIVATE KEY-----"
                + "\n"
                + Base64.getMimeEncoder().encodeToString(keyPair.getPrivate().getEncoded())
                + "\n"
                + "-----END RSA PRIVATE KEY-----";
        String publicKey = "-----BEGIN PUBLIC KEY-----"
                + "\n"
                + Base64.getMimeEncoder().encodeToString(keyPair.getPublic().getEncoded())
                + "\n"
                + "-----END PUBLIC KEY-----";
        return new Keys(privateKey, publicKey);
    }

    public static KeyPair generate(int keySize, String algorithm) throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(algorithm);
        generator.initialize(keySize);
        return generator.generateKeyPair();
    }
}
