package com.api.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.api.model.User;
import com.api.util.SignUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 囍崽
 * version 1.0
 * 调用API
 */
public class ApiClient {

    private static final String GATEWAY_HOST = "http://localhost:8090";

    private final String accessKey;
    private final String secretKey;

    public ApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getNameByGet(String name) {
        return HttpUtil.get(GATEWAY_HOST + "/api/name/" + name);
    }

    public String getNameByPost(String name) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        return HttpUtil.post(GATEWAY_HOST + "/api/name", paramMap);
    }

    public String getNameByPostWithJSON(User user) {
        String jsonStr = JSONUtil.toJsonStr(user);
        HttpResponse response = HttpRequest.post(GATEWAY_HOST + "/api/name/user")
                .addHeaders(getHeaders(jsonStr))
                .body(jsonStr)
                .execute();

        System.out.println("response = " + response);
        System.out.println("status = " + response.getStatus());
        if (response.isOk()) {
            return response.body();
        }
        return "fail";
    }

    private Map<String, String> getHeaders(String body) {
        Map<String, String> header = new HashMap<>();
        header.put("accessKey", accessKey);
        header.put("sign", SignUtil.getSign(body, secretKey));

        header.put("body", body);
        header.put("nonce", RandomUtil.randomNumbers(5));//防止重放，防止用户恶意重复使用之前执行成功的请求
        header.put("timestamp", String.valueOf(System.currentTimeMillis()));//校验有效期
        return header;
    }
}
