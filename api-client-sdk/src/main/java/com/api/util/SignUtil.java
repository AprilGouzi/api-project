package com.api.util;

import cn.hutool.crypto.digest.DigestUtil;

/**
 * @author 囍崽
 * version 1.0
 * 签名工具类
 */
public class SignUtil {
    public static String getSign(String body, String secretKey) {
        String content = body + "." + secretKey;
        return DigestUtil.md5Hex(content);
    }
}
