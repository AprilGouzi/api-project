package com.api.controller;

import cn.hutool.core.bean.BeanUtil;
import com.api.annotation.AuthCheck;
import com.api.common.BaseResponse;
import com.api.common.DeleteRequest;
import com.api.common.ErrorCode;
import com.api.common.ResultUtils;
import com.api.constant.UserConstant;
import com.api.exception.BusinessException;
import com.api.model.dto.user.*;
import com.api.model.entity.User;
import com.api.model.vo.UserVO;
import com.api.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 囍崽
 * version 1.0
 */
@RestController
@RequestMapping("/user")
@Slf4j
@Api(tags = "用户相关接口")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    @ApiOperation("用户注册")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        //1.判断用户注册信息
        if (userRegisterRequest == null) {
            //2.返回参数错误信息
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //3.判断用户注册信息是否为空
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            //4.为空则直接返回空，注册失败
            return null;
        }
        //5.进行用户注册
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public BaseResponse<User> login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User user = userService.login(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    /**
     * 用户退出登录
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("退出登录")
    public BaseResponse<Boolean> logout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.logout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    @ApiOperation("获取当前登录用户")
    public BaseResponse<UserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    /**
     * 创建用户
     *
     * @param userAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("创建用户")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtil.copyProperties(userAddRequest, user);
        userService.save(user);
        return ResultUtils.success(user.getId());
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @ApiOperation("删除用户")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() < 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @ApiOperation("更新用户")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtil.copyProperties(userUpdateRequest, user);
        userService.updateById(user);
        return ResultUtils.success(true);
    }

    /**
     * 根据id获取用户,仅管理员
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    @ApiOperation("根据id获取用户")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id, HttpServletRequest request) {
        if (id < 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        return ResultUtils.success(user);
    }

    /**
     * 根据id获取包装类
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get/vo")
    @ApiOperation("根据id获取包装类")
    public BaseResponse<UserVO> getUserVOById(long id, HttpServletRequest request) {
        BaseResponse<User> response = getUserById(id, request);
        User user = response.getData();
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @ApiOperation("分页获取用户列表")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest,
                                                   HttpServletRequest request) {
        //1.构造分页构造器对象
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> pageInfo = new Page<>(current, pageSize);

        //2.条件构造器
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        //3.添加过滤条件
        String userName = userQueryRequest.getUserName();
        queryWrapper.like(userName != null, User::getUserName, userName);
        //4.添加排序条件
        queryWrapper.orderByDesc(User::getCreateTime);

        //5.分页查询
        userService.page(pageInfo, queryWrapper);
        return ResultUtils.success(pageInfo);
    }

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    @ApiOperation("分页获取用户封装列表")
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest,
                                                       HttpServletRequest request) {
        //1.构造分页构造器对象
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> pageInfo = new Page<>(current, pageSize);
        Page<UserVO> userVOPage = new Page<>();
        //2.条件构造器
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        //3.添加过滤条件
        String userName = userQueryRequest.getUserName();
        queryWrapper.like(userName != null, User::getUserName, userName);
        //4.添加排序条件
        queryWrapper.orderByDesc(User::getCreateTime);

        //5.分页查询
        userService.page(pageInfo, queryWrapper);

        //6.对象拷贝
        BeanUtil.copyProperties(pageInfo, userVOPage, "records");
        List<User> records = pageInfo.getRecords();
        List<UserVO> list = records.stream().map((item) -> {
            UserVO userVO = new UserVO();
            BeanUtil.copyProperties(item, userVO);
            return userVO;
        }).collect(Collectors.toList());
        userVOPage.setRecords(list);

        return ResultUtils.success(userVOPage);
    }

    ///**
    // * 更新个人信息
    // *
    // * @param userUpdateMyRequest
    // * @param request
    // * @return
    // */
    //@PostMapping("/update/my")
    //@ApiOperation("更新个人信息")
    //public BaseResponse<Boolean> updateMyUser(@RequestBody UserUpdateMyRequest userUpdateMyRequest,
    //                                          HttpServletRequest request) {
    //    if (userUpdateMyRequest == null) {
    //        throw new BusinessException(ErrorCode.PARAMS_ERROR);
    //    }
    //    userService.getLoginUser(request)
    //    User user = new User();
    //    BeanUtil.copyProperties(userUpdateMyRequest, user);
    //    user.setId(loginUser.getId());
    //    userService.updateById(user);
    //    return ResultUtils.success(true);
    //}
}
