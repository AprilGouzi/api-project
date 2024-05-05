package com.api.model.dto.user;

import lombok.Data;

/**
 * @author 囍崽
 * version 1.0
 * 用户更新请求
 */
@Data
public class UserUpdateRequest {

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}
