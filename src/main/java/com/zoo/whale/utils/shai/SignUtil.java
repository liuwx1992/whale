package com.zoo.whale.utils.shai;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 * @ClassName:SignUtil
 * @Description:
 * @Author:weixin.liu
 * @Date:2021/11/29
 * @Time:20:29
 * @Version；1.0
 */
public class SignUtil {

    public static void main(String[] args) throws Exception {
        String url = "http://180.76.104.246:8080/data/verify";
        String msgtype = "0510";
        String privateKey = "30820277020100300D06092A864886F70D0101010500048202613082025D02010002818100A78A356E664DC2A8D5EB8AF9D11D70719B4A34B55B47E7DA1758758AD66C920245B00BAD0FC814D2BDC240B5560AABE215DEDDCA1087E47A80076D6D0726D4CDBC77F974B4F95DB987A308D2F2F8D77DD82F57DD14D63753392066BD2009B3BDB3476F06F583278AC9FFD5601C473B6B0F1644016B35A526D50E3CF85A982123020301000102818100923780A2350EB0508141EC2C52608AFDDB354C02EB033A49900E57A72FB3D949BA600610A7B14ADB115932F80F7B88BEEBAF7189241C353EE3F5977E8BBB4C04476AF0275FE69243078177E8E06C487B2C9B48AD783A8016095DBA8B87305C74DADC01EAFEC9C626B525F0056AEBE01ADFC8ED413E48CFB46CDC21C0F348B541024100DF763CD53E9C8307C93BC7AF8C0AD30B874C664C527CE4A7381C57BE5897F3ACFDC7B88703FE9DAD8CE4B4C91677CF40BB9418233B502705FD0AF2379EEE9F59024100BFEF6C70FC29E443F3D7B53FB943170CF13C5D77306B3991A961481E8392BF6A517CA5DF3D6380AD79C70C45C39DA33DDBF7FD183B586C5CD5DCA9ABECBA50DB0240469217AA1CA8DEF4A187A08B7F05981AE6F2A8C7E7B10BA107A663CB783D1CB6803B07BA93F760228401625AB31EC9341F4928CA4F9C0386EF9FFA58B8779199024003D267AD64480D44912175797C5DEAA2965726614E348463F3247DC425C8914933B030B95E4691F4625D65502A52044322133115FF70864FDEEE52EE9A7B274B0241009378C86F25B68EBEC1F83753A53DFF178EFD80C1AFDD873CEDCFB7C67401A5E48BBB3816FE53D675B3FB5EAB6A38496C3B3137B6835E1357696A431EB86B1C46";
        String username = "br20201202";
        String password = "4156594257594C595A4A4E595A424759";
        String name = "邵德光";
        String cardno = "21080219620106053x";
        String validdate = "20160622";
        String invaliddate = "20260622";
        String video = "";
        //String expired = "true";
        String corpSign = null;

//        String imageBase64Content = getImageBase64Content("H:\\jindie\\face.jpg");
        //String imageBase64Content = getImageBase64Content("F:\\pictures\\face2.jpg");


        String sourceVideo = "H:\\jindie\\WIN_20211130_16_34_10_Pro.mp4";
        InputStream inputStream = FileUtils.openInputStream(new File(sourceVideo));
        //String ss = new String(Base64.getEncoder().encode(IOUtils.toByteArray(inputStream)),Charset="ISO-8859-1");
        video = new String(Base64.getEncoder().encode(IOUtils.toByteArray(inputStream)), Charset.defaultCharset());


        String data = msgtype + username + password + name + cardno + validdate + invaliddate;
        try {
            byte[] sign = sign(data.getBytes("UTF-8"), ByteUtil.hexStringToBytes(privateKey), "RSA", "SHA1WithRSA");
            corpSign = ByteUtil.byteToString(sign);
            System.out.println("签名：" + corpSign);

            //String encrypt = encrypt(name, password);
            //System.out.println("加密：" + encrypt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(corpSign);
        JSONObject paramJSON = new JSONObject();
        //paramJSON.put("password", password);
        paramJSON.put("username", username);
        paramJSON.put("msgtype", "DE" + msgtype);

        paramJSON.put("name", name);
        paramJSON.put("name", encrypt(name, password));
        paramJSON.put("cardno", cardno);
        paramJSON.put("cardno", encrypt(cardno, password));

        //paramJSON.put("imagevalue", imageBase64Content);

        paramJSON.put("validdate", validdate);
        paramJSON.put("invaliddate", invaliddate);
        paramJSON.put("video", video);
        //paramJSON.put("expired", expired);
        paramJSON.put("signature", corpSign);
        ////
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("msgtype", msgtype);
//		params.put("username", username);
//		params.put("password", password);
//		params.put("name", name);
//		params.put("name", name);
//		params.put("name", name);
        ////
//		String sign = PaySignCore.createLinkString(params, appSecret);// createLinkString(params,appSecret);
//		paramJSON.put("sign", sign);
//		for(int i = 0; i < 10;i++) {
        long start = System.currentTimeMillis();

        String result = HttpClientUtil.doPost(url, paramJSON.toJSONString(), "utf-8");
        System.out.println("入参:" + paramJSON);
        long end = System.currentTimeMillis();
//		System.out.println("总耗时:" + (end - start) + "ms");
//		System.out.print(result+ "耗时 "+(end - start)+"ms");
        System.out.print(JSONObject.parseObject(result) + "耗时 " + (end - start) + "ms");
        //	}

    }

    /**
     * 签名数据
     *
     * @param data
     * @param privateKey
     * @param keyAlgorithm
     * @param signAlgotithm
     * @return
     * @throws Exception
     */
    public static byte[] sign(byte[] data, byte[] privateKey, String keyAlgorithm, String signAlgotithm)
            throws Exception {
        // 转换私钥材料
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
        // 实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm, SecurityProvider.getProvierName());
        // 取私钥匙对象
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 实例化Signature
        Signature signature = Signature.getInstance(signAlgotithm, SecurityProvider.getProvierName());
        // String prikeyStr = Base64.encodeBase64String(priKey.getEncoded());
        // 初始化Signature
        signature.initSign(priKey);
        // 更新
        signature.update(data);
        // 签名
        return signature.sign();
    }

    /**
     * 对字符串加密
     *
     * @param str
     * @return
     */
    public static String encrypt(String str, String password) {
        try {
            byte[] strBytes = str.getBytes("UTF-8");
            byte[] strBytes_ = encryptMode(strBytes, password);
            Encoder encoder = Base64.getEncoder();
            String result = encoder.encodeToString(strBytes_);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static byte[] encryptMode(byte[] data, String pwd) throws Exception {
        byte[] key = ByteUtil.hexStringToByteArray(pwd);
        key = transferKeyFrom16To24(key);
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(1, deskey);
        byte[] bOut = cipher.doFinal(data);
        return bOut;
    }

    private static byte[] transferKeyFrom16To24(byte[] key) {
        if (key.length != 16) {
            return key;
        }
        byte[] tempKey = new byte[24];
        System.arraycopy(key, 0, tempKey, 0, key.length);
        System.arraycopy(key, 0, tempKey, key.length, 8);
        return tempKey;
    }

    public static String getImageBase64Content(String filePath)
            throws Exception {
        File imgFile = new File(filePath);
        byte[] bytes = null;
        try {
            InputStream is = new FileInputStream(imgFile);
            long length = imgFile.length();
            bytes = new byte[(int) length];

            int offset = 0, numRead = 0;
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return org.apache.commons.codec.binary.Base64.encodeBase64String(bytes);
    }


}
