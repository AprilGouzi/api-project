package com.api.client;


import com.api.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;


/**
 * @author 囍崽
 * version 1.0
 */
@SpringBootTest
public class ApiClientTests {

    @Resource
    private ApiClient apiClient;

    @Test
    void testApiClient() throws UnsupportedEncodingException {
        String result1 = apiClient.getNameByGet("xizai");
        String result2 = apiClient.getNameByPost("XIZAI");
        User user = new User();
        user.setName("xizaia");
        String result3 = apiClient.getNameByPostWithJSON(user);
        System.out.println(result1);
        System.out.println(result2);
        System.out.println(result3);
        //ApiClient apiClient = new ApiClient();
        //String result1 = apiClient.getNameByGet("XIZAI");
        //String result2 = apiClient.getNameByPost("xizai");
        //User user = new User();
        //user.setName("XiZai");
        //String result3 = apiClient.getNameByPostWithJSON(user);
        //System.out.println(result1);
        //System.out.println(result2);
        //System.out.println(result3);
    }
}
