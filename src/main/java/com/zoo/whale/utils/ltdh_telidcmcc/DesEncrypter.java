package com.zoo.whale.utils.ltdh_telidcmcc;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * @ClassName:DesEncrypter
 * @Description:
 * @Author:weixin.liu
 * @Date:2021/7/12
 * @Time:14:58
 * @Version；1.0
 */
public class DesEncrypter {

    Cipher ecipher;
    Cipher dcipher;
    byte[] salt = {(byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32, (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03};

    public DesEncrypter(String passPhrase) throws Exception {
        int iterationCount = 2;
        KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);
        SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
        ecipher = Cipher.getInstance(key.getAlgorithm());
        dcipher = Cipher.getInstance(key.getAlgorithm());
        AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
        ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
        dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
    }


    public String encrypt(String str) throws Exception {
        str = new String(str.getBytes(), "UTF-8");
        return Base64.encodeBase64String(ecipher.doFinal(str.getBytes()));
    }

    public String decrypt(String str) throws Exception {
        return new String(dcipher.doFinal(Base64.decodeBase64(str)), "UTF-8");
    }


    public static void main(String[] args) throws Exception {
        DesEncrypter desEncrypter = new DesEncrypter("apiKey");
        String str = "mobile=xxxx";
        String jiami = desEncrypter.encrypt(str);
        System.out.println(jiami);
        String jiemi = desEncrypter.decrypt(jiami);
        System.out.println(jiemi);
    }

}
