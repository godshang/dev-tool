package com.github.godshang.devtool.util;

import lombok.Builder;
import lombok.Getter;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class CryptoUtils {

    @Getter
    @Builder
    public static class CryptoOption {
        private byte[] input;
        private byte[] secretKey;
        private byte[] iv;
        private Algorithm algorithm;
        private Mode mode;
        private Padding padding;

        public String getTransformation() {
            return algorithm.name() + "/" + mode.name() + "/" + padding.name();
        }
    }

    /**
     * https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html
     */
    public enum Algorithm {
        AES, DES, DESede;
    }

    public enum Mode {
        //        NONE, // 无模式
        ECB, // 电子密码本模式（Electronic CodeBook）
        CBC, // 密码分组连接模式（Cipher Block Chaining）
        CFB, // 密文反馈模式（Cipher Feedback）
        CTR, // 计数器模式（A simplification of OFB）
        OFB, // 输出反馈模式（Output Feedback）
        CTS, // Cipher Text Stealing
//        PCBC, // Propagating Cipher Block
        ;
    }

    public enum Padding {
        NoPadding,
        ZeroPadding,
        ISO10126Padding,
        OAEPPadding,
        PKCS1Padding,
        PKCS5Padding,
        SSL3Padding
    }

    private static final String DEFAULT_3DES_ALGORITHM = "DESede/ECB/PKCS5Padding";

    public static byte[] encrypt3DES(byte[] input, byte[] secretKey, byte[] iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        return encrypt(CryptoOption.builder()
                .input(input).secretKey(secretKey).iv(iv)
                .algorithm(Algorithm.DESede).mode(Mode.ECB).padding(Padding.PKCS5Padding)
                .build());
    }

    public static byte[] decrypt3DES(byte[] input, byte[] secretKey, byte[] iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        return decrypt(CryptoOption.builder()
                .input(input).secretKey(secretKey).iv(iv)
                .algorithm(Algorithm.DESede).mode(Mode.ECB).padding(Padding.PKCS5Padding)
                .build());
    }

    public static byte[] encrypt(CryptoOption option) throws NoSuchAlgorithmException, InvalidKeyException,
            NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        IvParameterSpec ivSpec = null;
        if (option.getIv() != null) {
            ivSpec = new IvParameterSpec(option.getIv());
        }
        SecretKey key = generateSecretKey(option.getAlgorithm().name(), option.getSecretKey());
        Cipher encryptCipher = Cipher.getInstance(option.getTransformation());
        encryptCipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] output = encryptCipher.doFinal(option.getInput());
        return output;
    }

    public static byte[] decrypt(CryptoOption option)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        IvParameterSpec ivSpec = null;
        if (option.getIv() != null) {
            ivSpec = new IvParameterSpec(option.getIv());
        }
        SecretKey key = generateSecretKey(option.getAlgorithm().name(), option.getSecretKey());
        Cipher decryptCipher = Cipher.getInstance(option.getTransformation());
        decryptCipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        byte[] output = decryptCipher.doFinal(option.getInput());
        return output;
    }

    private static SecretKey generateSecretKey(String algorithm, byte[] secretKey) throws InvalidKeyException, NoSuchAlgorithmException {
        if (algorithm.startsWith("DES")) {
            KeySpec keySpec;
            if (algorithm.startsWith("DESede")) { // DESede兼容
                keySpec = new DESedeKeySpec(secretKey);
            } else {
                keySpec = new DESKeySpec(secretKey);
            }
            return generateSecretKey(algorithm, keySpec);
        } else {
            return new SecretKeySpec(secretKey, algorithm);
        }
    }

    private static SecretKey generateSecretKey(String algorithm, KeySpec keySpec) throws NoSuchAlgorithmException {
        final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
        try {
            return keyFactory.generateSecret(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}
