package com.api.service;

import com.api.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author 20466
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-04-30 15:47:21
*/
public interface UserService extends IService<User> {
    long userRegister(String userAccount, String userPassword, String checkPassword);

    User login(String userAccount, String userPassword, HttpServletRequest request);

    boolean logout(HttpServletRequest request);

    User getLoginUser(HttpServletRequest request);

    boolean isAdmin(HttpServletRequest request);
}
