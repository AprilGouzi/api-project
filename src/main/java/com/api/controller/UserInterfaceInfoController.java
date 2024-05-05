package com.api.controller;

import com.api.annotation.AuthCheck;
import com.api.common.BaseResponse;
import com.api.common.DeleteRequest;
import com.api.common.ErrorCode;
import com.api.common.ResultUtils;
import com.api.constant.CommonConstant;
import com.api.constant.UserConstant;
import com.api.exception.BusinessException;
import com.api.model.dto.UserInterfaceInfo.UserInterfaceInfoAddRequest;
import com.api.model.dto.UserInterfaceInfo.UserInterfaceInfoQueryRequest;
import com.api.model.dto.UserInterfaceInfo.UserInterfaceInfoUpdateRequest;
import com.api.model.entity.User;
import com.api.model.entity.UserInterfaceInfo;
import com.api.service.UserInterfaceInfoService;
import com.api.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

/**
 * @author 囍崽
 * version 1.0
 */
@RestController
@RequestMapping("/userInterfaceInfo")
@Slf4j
@Api(tags = "用户接口调用信息")
public class UserInterfaceInfoController {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private UserService userService;

    // TODO 自行完成用户调用接口增删改查代码

    //创建用户调用接口

    /**
     * 创建用户调用接口
     *
     * @param userInterfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("创建用户调用接口")
    public BaseResponse<Long> addUserInterfaceInfo(@RequestBody UserInterfaceInfoAddRequest userInterfaceInfoAddRequest,
                                                   HttpServletRequest request) {
        if (userInterfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //将userInterfaceInfoAddRequest拷贝到userInterfaceInfo 用户在数据库传输数据
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoAddRequest, userInterfaceInfo);

        //校验
        userInterfaceInfoService.validUserInterfaceInfo(userInterfaceInfo, true);
        //设置当前用户id
        User loginUser = userService.getLoginUser(request);
        userInterfaceInfo.setUserId(loginUser.getId());
        boolean result = userInterfaceInfoService.save(userInterfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        //返回接口id
        return ResultUtils.success(userInterfaceInfo.getId());
    }

    //删除用户调用接口

    /**
     * 删除用户调用接口
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @ApiOperation("删除用户调用接口")
    public BaseResponse<Boolean> deleteUserInterfaceInfo(@RequestBody DeleteRequest deleteRequest,
                                                         HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        //判断是否存在
        UserInterfaceInfo oldUserInterfaceInfo = userInterfaceInfoService.getById(id);
        if (oldUserInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //仅本人或管理员删除
        if (!oldUserInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = userInterfaceInfoService.removeById(id);
        return ResultUtils.success(result);
    }

    //更新用户调用接口

    /**
     * 更新用户调用接口
     *
     * @param userInterfaceInfoUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @ApiOperation("更新用户调用接口")
    public BaseResponse<Boolean> updateUserInterfaceInfo(@RequestBody UserInterfaceInfoUpdateRequest userInterfaceInfoUpdateRequest,
                                                         HttpServletRequest request) {
        long id = userInterfaceInfoUpdateRequest.getId();
        if (userInterfaceInfoUpdateRequest == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoUpdateRequest, userInterfaceInfo);
        //参数检验
        userInterfaceInfoService.validUserInterfaceInfo(userInterfaceInfo, false);
        User user = userService.getLoginUser(request);
        UserInterfaceInfo oldUserInterfaceInfo = userInterfaceInfoService.getById(id);
        if (oldUserInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        //仅本人或管理员可修改
        if (!oldUserInterfaceInfo.getUserId().equals(user.getId()) &&
                !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = userInterfaceInfoService.updateById(userInterfaceInfo);
        return ResultUtils.success(result);
    }

    //根据id获取用户调用接口

    /**
     * 根据id获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    @ApiOperation("根据id获取用户调用接口")
    public BaseResponse<UserInterfaceInfo> getUserInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getById(id);
        return ResultUtils.success(userInterfaceInfo);
    }

    //获取列表

    /**
     * 获取列表
     *
     * @param userInterfaceInfoQueryRequest
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/list")
    @ApiOperation("获取列表")
    public BaseResponse<List<UserInterfaceInfo>> listUserInterfaceInfo(
            UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest) {
        if (userInterfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoQueryRequest, userInterfaceInfo);
        //构造查询构造器
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>(userInterfaceInfo);
        List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoService.list(queryWrapper);
        return ResultUtils.success(userInterfaceInfoList);
    }

    //分页查询用户调用接口

    /**
     * 分页查询用户调用接口
     *
     * @param userInterfaceInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    @ApiOperation("分页查询用户调用接口")
    public BaseResponse<Page<UserInterfaceInfo>> listUserInterfaceByPage(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest,
                                                                         HttpServletRequest request) {
        if (userInterfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoQueryRequest, userInterfaceInfo);
        long current = userInterfaceInfoQueryRequest.getCurrent();
        long pageSize = userInterfaceInfoQueryRequest.getPageSize();
        String sortField = userInterfaceInfoQueryRequest.getSortField();
        String sortOrder = userInterfaceInfoQueryRequest.getSortOrder();

        //限制爬虫
        if (pageSize > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>(userInterfaceInfo);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField)
                , sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<UserInterfaceInfo> page = userInterfaceInfoService.page(new Page<>(current, pageSize), queryWrapper);
        return ResultUtils.success(page);
    }


}
