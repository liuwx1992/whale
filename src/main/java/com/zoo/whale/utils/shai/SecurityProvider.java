package com.zoo.whale.utils.shai;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

/**
 * @ClassName:SecurityProvider
 * @Description:
 * @Author:weixin.liu
 * @Date:2021/11/29
 * @Time:21:03
 * @Version；1.0
 */
public class SecurityProvider {
    private static String PROVIDER_NAME = "BC";
    private static boolean ISDECIDE = false;

    public static String getProvierName() {
        if (ISDECIDE && Security.getProvider(PROVIDER_NAME) != null) {
            return PROVIDER_NAME;
        } else {
            Security.addProvider(new BouncyCastleProvider());
            ISDECIDE = true;
            return PROVIDER_NAME;
        }

    }
}
