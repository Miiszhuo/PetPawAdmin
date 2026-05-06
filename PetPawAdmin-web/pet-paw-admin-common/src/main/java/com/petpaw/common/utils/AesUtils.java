package com.petpaw.common.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES加密工具类
 */
public class AesUtils {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    // 16字节密钥（AES-128）
    private static final String SECRET_KEY = "petpaw-admin-123"; // 16字节

    private static SecretKeySpec getSecretKey() {
        return new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
    }

    /**
     * 加密
     */
    public static String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("AES加密失败", e);
        }
    }

    /**
     * 解密
     */
    public static String decrypt(String encryptedText) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES解密失败", e);
        }
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        String originalText = "123456";
        String encrypted = encrypt(originalText);
        String decrypted = decrypt(encrypted);

        System.out.println("原文: " + originalText);
        System.out.println("密文: " + encrypted);
        System.out.println("解密后: " + decrypted);
        System.out.println("验证结果: " + originalText.equals(decrypted));
    }
}
