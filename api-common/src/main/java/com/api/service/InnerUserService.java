package com.api.service;

import com.api.model.entity.User;

/**
 * @author 囍崽
 * version 1.0
 */
public interface InnerUserService {

    /**
     * 根据accessKey查询用户
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey);
}
