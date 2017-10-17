package com.yzx.flow.core.util;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.commons.lang3.StringUtils;

public class RSAUtils {

    /** *//**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";
    
    /** *//**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /** *//**
     * 获取公钥的key
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";
    
    /** *//**
     * 获取私钥的key
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";
    
    /** *//**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;
    
    /** *//**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /** *//**
     * <p>
     * 生成密钥对(公钥和私钥)
     * </p>
     * 
     * @return
     * @throws Exception
     */
    public static Map<String, Object> genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }
    
    /** *//**
     * <p>
     * 用私钥对信息生成数字签名
     * </p>
     * 
     * @param data 已加密数据
     * @param privateKey 私钥(BASE64编码)
     * 
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64Utils.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return Base64Utils.encode(signature.sign());
    }

    /** *//**
     * <p>
     * 校验数字签名
     * </p>
     * 
     * @param data 已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign 数字签名
     * 
     * @return
     * @throws Exception
     * 
     */
    public static boolean verify(byte[] data, String publicKey, String sign)
            throws Exception {
        byte[] keyBytes = Base64Utils.decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(Base64Utils.decode(sign));
    }

    /** *//**
     * <P>
     * 私钥解密
     * </p>
     * 
     * @param encryptedData 已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey)
            throws Exception {
        byte[] keyBytes = Base64Utils.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /** *//**
     * <p>
     * 公钥解密
     * </p>
     * 
     * @param encryptedData 已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey)
            throws Exception {
        byte[] keyBytes = Base64Utils.decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * 公钥加密信息
     * @param publicKey
     * @param content
     * @return
     */
    public static String getSecretCode(String publicKey,String content){
    	try {
    		if(StringUtils.isBlank(content)||StringUtils.isBlank(publicKey)){
    			return "";
    		}
			byte[] encryptByPublicKey = encryptByPublicKey(content.getBytes(), publicKey);
			return Base64Utils.encode(encryptByPublicKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return "";
    }
    
    /**
     * 私钥解密信息
     * @param publicKey
     * @param content
     * @return
     */
    public static String getCodeString(String privateKey,String content){
    	try {
    		if(StringUtils.isBlank(content)||StringUtils.isBlank(privateKey)){
    			return "";
    		}
    		byte[] decryptByPrivateKey = decryptByPrivateKey(Base64Utils.decode(content), privateKey);
    		return new String(decryptByPrivateKey);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return "";
    }
    
    public static void main(String[] args) {
    	String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCvKbqSu3puJVqseyhg9Nby6wQ6MuaFuKHH1MsCSl6jo7s64hBzOPLH6dSsdOCtysDGFhRT/Dmpd8ou9NDJsaR/zwDELeTbzqPbgJ9HVe4zKbJcVfsBU+WKHTTgD/+3AwO0DozsDCllV49Ue01rv2Qx9lBitSRs0x1U+3fXTvLgnQIDAQAB";
    	String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAK8pupK7em4lWqx7KGD01vLrBDoy5oW4ocfUywJKXqOjuzriEHM48sfp1Kx04K3KwMYWFFP8Oal3yi700MmxpH/PAMQt5NvOo9uAn0dV7jMpslxV+wFT5YodNOAP/7cDA7QOjOwMKWVXj1R7TWu/ZDH2UGK1JGzTHVT7d9dO8uCdAgMBAAECgYA//DpPR3Tdr3+D6ilB4a5zZi5RJC7ZQiy21qCh4ZDYrgLC67menXryVBSAOuGoGpx4v7AFTemyYjPVG1D5nI6+FodD8K+CQVsdiGPAL0/D99OoygRR2xPHPSohKS8VU7dGcYoJJs9XsvWV1WtkOGPMFjk52QiZYNFsCchTLZR1AQJBANxUNBk8njBeZjT//syujE6n+aw3hMcU3du5dMXk4ULlFsN/ArNcR+nWes6Sz+DW9N2P3AvpHQ8HkdNjpxMtCPECQQDLhZHVvjzWzalFRyngorkf0xKSsmDhye7CN0x1AQ7znND0ebcJPK84HO6aBhY0RRMSR3nY+89NOuFy4tFBdzJtAkEAqju09TrMznphZdnVOLg0WCGY2uMah+crc7Va7/vjwBfY+ruLjqFiPt4kjK8KRqJPF1Erp1g5x38FwNpiIILPQQJBAJYYevZE57+PS3AhTJwzxg3Xb9IqpdK/R0uK1uNPlYRKubwja77vN1ZN2BZVuNhxvN/1QMKq6zbM3uoIp0T9sH0CQBAF/9RNpBWqj+xshaWbvaVT4hI1OsMwW6peaHphsJKN7h/PaQdNptcexVCV9MkI1cJWbuZgKein75NSjZcbft4=";
    	String content = "content";
    	String coe  = getSecretCode(publicKey, content);
    	System.out.println(coe);
    	String  con = getCodeString(privateKey, coe);
    	System.out.println(con);
	}
    
    
    
    /** *//**
     * <p>
     * 公钥加密
     * </p>
     * 
     * @param data 源数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey)
            throws Exception {
        byte[] keyBytes = Base64Utils.decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /** *//**
     * <p>
     * 私钥加密
     * </p>
     * 
     * @param data 源数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey)
            throws Exception {
        byte[] keyBytes = Base64Utils.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /** *//**
     * <p>
     * 获取私钥
     * </p>
     * 
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return Base64Utils.encode(key.getEncoded());
    }

    /** *//**
     * <p>
     * 获取公钥
     * </p>
     * 
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return Base64Utils.encode(key.getEncoded());
    }

}
