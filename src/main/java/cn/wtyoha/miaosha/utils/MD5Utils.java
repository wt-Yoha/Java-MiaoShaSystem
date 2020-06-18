package cn.wtyoha.miaosha.utils;


import org.apache.commons.codec.digest.DigestUtils;

import java.util.UUID;

public class MD5Utils {
    public static String md5(String s){
        return DigestUtils.md5Hex(s);
    }

    public static String randomString(){
        return UUID.randomUUID().toString().replace("-","");
    }

    public static String inputPassToDBPass(String inputPass, String salt) {
        String saltString = "" + salt.charAt(0) + salt.charAt(5) + salt.charAt(7) + inputPass + salt.charAt(2) + salt.charAt(1);
        return md5(saltString);
    }
}
