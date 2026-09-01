package com.zoo.whale.utils.ylzh2;

/**
 * @ClassName:Test
 * @Description:
 * @Author:weixin.liu
 * @Date:2021/12/20
 * @Time:19:35
 * @Version；1.0
 */
public class Test {

    private static String upsPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvE+KiUV9Np01zJZfqiUHXval9+N7XQoqWWYHxxsbAyEmHEMMleoGBtShoyZTlVb4i5KXtPjG/Pj5n7msiFvLAo9/FJKAfWEwJdSyGw2j5MJrmYyEiRbbt6+Nw5OgRO+jaTcFG/K7onyvtFuCq3A9GOadW2G6PPljM87gJEtnekJRCDulwYV6r45OUfLdgtjR0hD2nBMNwKCyFfhkK5armI8kB4a0nTUi2guuudJvMRskj2Tb83mx/NKT5XIf79VhY28Y05znHd/tfdwl7aTA7uSHv+mD/VRDDvDVaDf9iFa0NlldcYIJ0hugkO09+1iJ1BSJXNosU27LqJzWiBUxvQIDAQAB";

    public static void main(String[] args) throws Exception {
        // rsa 加解密的工具类
        RSAUtil usaUtil = new RSAUtil();
        //String s = "MYDMYv7rM8Ed62oHmCIGsvr6fk0r2THISWGD98jQfVx6Fz2jBgx0NA+neXZkEQigAamniIhJRJ9OIEeL/0xEckjJpS551vl/f8u/8CSIGkemQO7OWUYrVEATmFwnPElPo8VSdXAMR7hCzSB0hEEl90TjX8Nv0+GiB+msE0xUxgJs0a3HNwZqdoW3eQ+Jd9g86P2su604EpFkZaKhzcbk3y4jVDmGydr5hajL7wFYcTF0QNn721FPCD6GTe90IXOXXctzDqW8M4jiqBegZK+BF63c+CgpF0KSW11Z2vr9aAIcVGaKhiMRPjUJrWdBEts/RNyCtxhIVH1Q2rso9FYfIA==";
        //String s = "rtqsmps+HYrIqoF3hZKBsGNjHjt3yEtedcwqc1h9dnuOA0nxF/jm/1VdOXeQ0faj/VO1vOs7S8HoxCoVHbbF2Ve9DE1EsPhMNuOXhsNRs9ZIIdvIuo1IBOQ7Fo/RbAOeN848B2JRPxV+L6uPRIEULa4deRIUJyGyhSx/xmDDnK2fjtcT6nhVA4Q2/NdWdFyUObhKbimzUlNRfzqqzMmGwo1qoDDpDSWqNWn2r5sDu/ieZVcTb4Mw6P4402XuxdClSogXJJRDj3jPS8lkcq1/yabqDHSRAUzBWQ2trY4amzMH63oCZ1Z5tsnjrbUdH+7PYphqbVHPfsBTXFRSlquc0g==";
        String s = "X7j5XZI3nBV0KwprISMoHUejs87D/4Toyoio8tuo+HQ7FSok+cQUPolRdvVvRlGMdsSLtfbfOgaHFR8k1eN1VEj+Pa4YaxXumJ7vonZMCvNIDBNHBvEJqUvoFgOAE0sUKNc5k37yx1FxvhtTH9FYTfSxh5qfXnWH0R59n+CMz7Uc+r8MDDDj5W+QgefRj8i7ujP6aK1q1oJZmIrtxwzNfG3Lai1C177XfPg/2LaXYlPrR6BsGniDyd3gtjbolmKyG+YIkWd2Uy6X3owtO7a94elvF8JH/59EZlXFAw+b/QAh+24TTgzC2I6G7SoDy201RLOWompOxUnxVeN48qDA5g==";
        String decode = usaUtil.decryptByPublicKey(upsPublicKey, s);
        System.out.println(decode);

    }
}
