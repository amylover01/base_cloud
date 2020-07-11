package com.amylover.common.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @description :
 * @date : 2019/9/4 9:48
 */
public class RSAUtils {


    public static final String KEY_ALGORITHM = "RSA";

    public static final String RSA_PADDING_KEY = "RSA/ECB/PKCS1Padding";

    public static final String SIGNATURE_ALGORITHM_MD5 = "MD5withRSA";

    public static final String SIGNATURE_ALGORITHM_SHA1 = "SHA1WithRSA";

    private static final Object LOCK = new Object();

    private static Class bcProvider = null;
    /**
     * RSA密钥长度，RSA算法的默认密钥长度是1024密钥长度必须是64的倍数，在512到65536位之间
     */
    private static final int KEY_SIZE = 2048;

    /*
     * 生成密钥对
     */
    public static Map<String, String> initKey() throws Exception {
        KeyPairGenerator keygen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        SecureRandom secrand = new SecureRandom();
        /**
         * 初始化随机产生器
         */
        secrand.setSeed("initSeed".getBytes());
        /**
         * 初始化密钥生成器
         */
        keygen.initialize(KEY_SIZE, secrand);
        KeyPair keys = keygen.genKeyPair();

        byte[] pub_key = keys.getPublic().getEncoded();
        String publicKeyString = Base64.encodeBase64String(pub_key);

        byte[] pri_key = keys.getPrivate().getEncoded();
        String privateKeyString = Base64.encodeBase64String(pri_key);

        Map<String, String> keyPairMap = new HashMap<>();
        keyPairMap.put("pu", publicKeyString);
        keyPairMap.put("pr", privateKeyString);

        return keyPairMap;
    }


    /**
     * 获取RSA公钥
     *
     * @param key 公钥字符串
     * @return
     * @throws Exception
     */
    public static PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(key.getBytes(StandardCharsets.UTF_8));
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * RSA公钥加密
     *
     * @param plainText   待加密数据
     * @param s_publicKey 公钥字符串
     * @return
     */
    public static String encrypt(String plainText, String s_publicKey) {
        if (plainText == null || s_publicKey == null) {
            return null;
        }
        try {
            PublicKey publicKey = getPublicKey(s_publicKey);
            Cipher cipher = getCipherInstance();
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] enBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return formatString(new String(Base64.encodeBase64(enBytes), StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 格式化RSA加密字符串,去掉换行和渐近符号
     *
     * @param sourceStr
     * @return
     */
    private static String formatString(String sourceStr) {
        if (sourceStr == null) {
            return null;
        }
        return sourceStr.replaceAll("\\r", "").replaceAll("\\n", "");
    }

    /**
     * 获取RSA私钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(key.getBytes(StandardCharsets.UTF_8));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * RSA私钥解密
     *
     * @param enStr        待解密数据
     * @param s_privateKey 私钥字符串
     * @return
     */
    public static String decrypt(String enStr, String s_privateKey) {
        if (enStr == null || s_privateKey == null) {
            return null;
        }
        try {
            PrivateKey privateKey = getPrivateKey(s_privateKey);
            Cipher cipher = getCipherInstance();
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] deBytes = cipher.doFinal(Base64.decodeBase64(enStr.getBytes("UTF-8")));
            return new String(deBytes, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    private static Cipher getCipherInstance() throws NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        String jdkVer = System.getProperty("java.version");
        if (jdkVer.startsWith("1.4.")) {
            if (bcProvider == null) {
                synchronized (LOCK) {
                    if (bcProvider == null) {
                        bcProvider = Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
                    }
                }
            }
            Provider provider = (Provider) bcProvider.newInstance();
            return Cipher.getInstance(RSA_PADDING_KEY, provider);
        } else {
            return Cipher.getInstance(RSA_PADDING_KEY);
        }
    }


    /**
     * RSA签名
     * <p>
     * MD5摘要RSA签名
     *
     * @param content    待签名数据
     * @param privateKey 关联方私钥
     * @param encode     字符集编码
     * @return
     */
    public static String sign(String content, String privateKey, String encode) {
        if (content == null || privateKey == null || encode == null) {
            return null;
        }
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey.getBytes(StandardCharsets.UTF_8)));
            KeyFactory keyf = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM_MD5);
            signature.initSign(priKey);
            signature.update(content.getBytes(encode));
            byte[] signed = signature.sign();
            return new String(Base64.encodeBase64(signed), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * RSA签名
     * <p>
     * SHA1摘要RSA签名
     *
     * @param content    待签名数据
     * @param privateKey 关联方私钥
     * @param encode     字符集编码
     * @return
     */
    public static String signwithsha1(String content, String privateKey, String encode) {
        if (content == null || privateKey == null || encode == null) {
            return null;
        }
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey.getBytes(StandardCharsets.UTF_8)));
            KeyFactory keyf = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM_SHA1);
            signature.initSign(priKey);
            signature.update(content.getBytes(encode));
            byte[] signed = signature.sign();
            return new String(Base64.encodeBase64(signed), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * RSA签名
     * <p>
     * MD5摘要RSA签名
     *
     * @param content    待签名数据
     * @param privateKey 关联方私钥
     * @return
     */
    public static String sign(String content, String privateKey) {
        if (content == null || privateKey == null) {
            return null;
        }
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey.getBytes(StandardCharsets.UTF_8)));
            KeyFactory keyf = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM_MD5);
            signature.initSign(priKey);
            signature.update(content.getBytes("UTF-8"));
            byte[] signed = signature.sign();
            return new String(Base64.encodeBase64(signed), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * RSA签名
     * <p>
     * SHA1摘要RSA签名
     *
     * @param content    待签名数据
     * @param privateKey 关联方私钥
     * @return
     */
    public static String signwithsha1(String content, String privateKey) {
        if (content == null || privateKey == null) {
            return null;
        }
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey.getBytes("UTF-8")));
            KeyFactory keyf = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM_SHA1);
            signature.initSign(priKey);
            signature.update(content.getBytes("UTF-8"));
            byte[] signed = signature.sign();
            return new String(Base64.encodeBase64(signed), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * RSA签名验证
     * <p>
     * MD5摘要RSA签名验证
     *
     * @param content   待签名数据
     * @param sign      签名值
     * @param publicKey 分配给关联方公钥
     * @param encode    字符集编码
     * @return 布尔值
     */
    public static boolean verifySign(String content, String sign, String publicKey, String encode) {
        if (content == null || sign == null || publicKey == null || encode == null) {
            return false;
        }
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            byte[] encodedKey = Base64.decodeBase64(publicKey.getBytes("UTF-8"));
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM_MD5);
            signature.initVerify(pubKey);
            signature.update(content.getBytes(encode));
            return signature.verify(Base64.decodeBase64(sign.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * RSA签名验证
     * <p>
     * SHA1摘要RSA签名验证
     *
     * @param content   待签名数据
     * @param sign      签名值
     * @param publicKey 分配给关联方公钥
     * @param encode    字符集编码
     * @return 布尔值
     */
    public static boolean verifySignwithsha1(String content, String sign, String publicKey, String encode) {
        if (content == null || sign == null || publicKey == null || encode == null) {
            return false;
        }
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            byte[] encodedKey = Base64.decodeBase64(publicKey.getBytes("UTF-8"));
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM_SHA1);
            signature.initVerify(pubKey);
            signature.update(content.getBytes(encode));
            return signature.verify(Base64.decodeBase64(sign.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * RSA签名验证
     * <p>
     * MD5摘要RSA签名验证
     *
     * @param content   待签名数据
     * @param sign      签名值
     * @param publicKey 分配给关联方公钥
     * @return 布尔值
     */
    public static boolean verifySign(String content, String sign, String publicKey) {
        if (content == null || sign == null || publicKey == null) {
            return false;
        }
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            byte[] encodedKey = Base64.decodeBase64(publicKey.getBytes("UTF-8"));
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM_MD5);
            signature.initVerify(pubKey);
            signature.update(content.getBytes("UTF-8"));
            return signature.verify(Base64.decodeBase64(sign.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * RSA签名验证
     * <p>
     * sha1摘要RSA签名验证
     *
     * @param content   待签名数据
     * @param sign      签名值
     * @param publicKey 分配给关联方公钥
     * @return 布尔值
     */
    public static boolean verifySignwithsha1(String content, String sign, String publicKey) {
        if (content == null || sign == null || publicKey == null) {
            return false;
        }
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            byte[] encodedKey = Base64.decodeBase64(publicKey.getBytes("UTF-8"));
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM_SHA1);
            signature.initVerify(pubKey);
            signature.update(content.getBytes("UTF-8"));
            return signature.verify(Base64.decodeBase64(sign.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void main(String[] args) throws Exception {

      //  String rsa_private_key = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMj7DxZYCwaA7cXCLce7teHdZ54IySb6Nx/Vgdq55t9Ob2LHE/5Njn3J1X26Pk+Voc8k0WoxPO+u8rzJioEUMTKrmUrD+d2Y60UGyr7kp6FOfZM8oI/LSXbWWZFkLln7CcuTpgwteN7P6/158h8Zb6j7gs8nOMsVHHHuIBqVTmBdAgMBAAECgYEAgJql/NY5t9+oRnMKeFRU/kJ+m1Fj5d0WiPhGL3/li97Ux5A76u4Vz8fnJJyYYrSM5c/ZcOow4+4+xS7xwcb58x55JbqFzUTbG2uk4FgBw/XNUlRPnsIxkfgsYgDhzWtEXIHPpGh2eVMIzehb8GzjEi50uJIw+7nvvxsiPi7xUAECQQD83VCqHn9f+3oxx1JW8K/JcfsIzeWlh6b9U4ZSd/UEmqT2EXU90/Ghk9W4l+bkULPdTrgzQfx0/v/sCgqt7aPBAkEAy3kLqOB/RLQ11xAMXXOS7SgQuHNRQRp5JxL4uTKk9MNMFYvC1t3b1b4FqRHKXpxG9VWMaTXql0EjabVoMVGznQJAPpMeFRs69iurT4o9zBn/xmAYeVNKky4AmwwCX4Ij9iG21NX5J3W9g/irmOSuVK3L9OY4GycnLGS2PFk3z1bRgQJAOk5phO8wCSQK+aHjqTPhOoxsni6NbjaD2Sqak5drpagBOXtfTestf8aAIL60WwA1ZUAqiEum+cj0uOn+/YMIEQJAXgGxkQI2uriPUC3komiSLyKehoARQ2yzUhuEvjuIXKvnGhKySXm7XnVj769572yd9pchwRuNTB8xAbBUPU/A5A==";
        //String rsa_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDI+w8WWAsGgO3Fwi3Hu7Xh3WeeCMkm+jcf1YHauebfTm9ixxP+TY59ydV9uj5PlaHPJNFqMTzvrvK8yYqBFDEyq5lKw/ndmOtFBsq+5KehTn2TPKCPy0l21lmRZC5Z+wnLk6YMLXjez+v9efIfGW+o+4LPJzjLFRxx7iAalU5gXQIDAQAB";
        Map<String, String> map = initKey();
        String rsa_public_key = map.get("pu");
        String rsa_private_key = map.get("pr");
        String plainText = "123";

        String sign = sign(plainText, rsa_private_key);
        System.out.println("sign===" + sign);

        System.out.println(verifySign(plainText, sign, rsa_public_key));

        String encryptStr = encrypt(plainText, rsa_public_key);
        System.out.println("encrypt result:" + encryptStr);

        String decryptStr = decrypt(encryptStr, rsa_private_key);
        System.out.println("decrypt result:" + decryptStr);
    }

}