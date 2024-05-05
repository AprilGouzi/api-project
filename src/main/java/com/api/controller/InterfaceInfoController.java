package com.api.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.api.annotation.AuthCheck;
import com.api.client.ApiClient;
import com.api.common.BaseResponse;
import com.api.common.DeleteRequest;
import com.api.common.ErrorCode;
import com.api.common.ResultUtils;
import com.api.constant.CommonConstant;
import com.api.exception.BusinessException;
import com.api.model.User;
import com.api.model.dto.InterfaceInfo.InterfaceInfoAddRequest;
import com.api.model.dto.InterfaceInfo.InterfaceInfoQueryRequest;
import com.api.model.dto.InterfaceInfo.InterfaceInfoUpdateRequest;
import com.api.model.dto.InterfaceInfo.InvokeInterfaceRequest;
import com.api.model.entity.InterfaceInfo;
import com.api.model.enums.InterfaceInfoStatusEnum;
import com.api.service.InterfaceInfoService;
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
@RequestMapping("/interfaceInfo")
@Slf4j
@Api(tags = "接口信息相关接口")
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private ApiClient apiClient;

    /**
     * 创建接口
     *
     * @param interfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("创建接口")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest,
                                               HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);

        //校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        com.api.model.entity.User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        boolean result = interfaceInfoService.save(interfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(interfaceInfo.getId());
    }

    /**
     * 删除接口
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @ApiOperation("删除接口")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest,
                                                     HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() < 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        com.api.model.entity.User loginUser = userService.getLoginUser(request);
        long id = deleteRequest.getId();

        //判断接口是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        //仅本人或管理员删除
        if (!oldInterfaceInfo.getUserId().equals(loginUser.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = interfaceInfoService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 更新接口
     *
     * @param interfaceInfoUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @ApiOperation("更新接口")
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest,
                                                     HttpServletRequest request) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() < 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);

        //参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        com.api.model.entity.User loginUser = userService.getLoginUser(request);
        long id = interfaceInfoUpdateRequest.getId();
        //判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //仅本人或管理员修改
        if (!oldInterfaceInfo.getUserId().equals(loginUser.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据id获取接口信息
     * @param id
     * @return
     */
    @GetMapping("/get")
    @ApiOperation("根据id获取接口信息")
    public BaseResponse<InterfaceInfo> getInterfaceInfoById(long id){
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        return ResultUtils.success(interfaceInfo);
    }

    /**
     * 获取接口信息列表（仅管理员可使用）
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    @ApiOperation("获取接口信息列表（仅管理员可使用）")
    public BaseResponse<List<InterfaceInfo>> listInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        if (interfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list(queryWrapper);
        return ResultUtils.success(interfaceInfoList);
    }

    /**
     * 分页查询接口信息
     * @param interfaceInfoQueryRequest
     * @return
     */
    @GetMapping("/list/page")
    @ApiOperation("分页查询接口信息")
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest){
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        String description = interfaceInfoQuery.getDescription();
        // description 需支持模糊搜索
        interfaceInfoQuery.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(interfaceInfoPage);
    }

    /**
     * 上线下线接口
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("上线下线接口")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> startOrStopInterfaceInfo(@PathVariable Integer status, Long id) {

        if (id == null || id < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //判断接口是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        //如果是上线接口，就测试接口是否能使用
        if (status == InterfaceInfoStatusEnum.ONLINE.getValue()) {
            //判断接口是否能使用
            // TODO 根据测试地址来调用
            // 这里先用固定方法进行测试,后面改
            User user = new User();
            user.setName("XIZAI");
            String name = apiClient.getNameByPostWithJSON(user);
            if (StrUtil.isBlank(name)) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口验证失败");
            }
        }

        //更新数据库
        InterfaceInfo interfaceInfo = InterfaceInfo.builder()
                .id(id)
                .status(status)
                .build();
        boolean isSuccessful = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(isSuccessful);
    }

    /**
     * 在线调用接口
     *
     * @param invokeInterfaceRequest
     * @param request
     * @return
     */
    @PostMapping("/invoke")
    @ApiOperation("在线调用接口")
    public BaseResponse<Object> invokeInterface(@RequestBody InvokeInterfaceRequest invokeInterfaceRequest,
                                                HttpServletRequest request) {
        if (invokeInterfaceRequest == null || invokeInterfaceRequest.getId() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //判断接口是否存在
        long id = invokeInterfaceRequest.getId();
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口未上线");
        }
        if (interfaceInfo.getStatus() != InterfaceInfoStatusEnum.ONLINE.getValue()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口未上线");
        }
        //得到当前用户
        com.api.model.entity.User loginUser = userService.getLoginUser(request);
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();
        ApiClient client = new ApiClient(accessKey, secretKey);

        //先写死，后写活
        // TODO 判断该接口是否可以调用时由固定方法名改为根据测试地址来调用
        String userRequestParams = invokeInterfaceRequest.getRequestParams();
        User user = JSONUtil.toBean(userRequestParams, User.class);
        String result = client.getNameByPostWithJSON(user);
        return ResultUtils.success(result);
    }

    ///**
    // * 上线接口
    // *
    // * @param idRequest
    // * @return
    // */
    //@PostMapping("/online")
    //@AuthCheck(mustRole = "admin")
    //@ApiOperation("上线接口")
    //public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest) {
    //    if (idRequest == null || idRequest.getId() < 0) {
    //        throw new BusinessException(ErrorCode.PARAMS_ERROR);
    //    }
    //    //判断接口是否存在
    //    long id = idRequest.getId();
    //    InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
    //    if (oldInterfaceInfo == null) {
    //        throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
    //    }
    //
    //    //判断接口是否能使用
    //
    //    //这里先用固定的方法进行测试，后面改
    //    User user = new User();
    //    user.setName("MARS");
    //    String name = apiClient.getNameByPostWithJSON(user);
    //    if (StrUtil.isBlank(name)) {
    //        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口验证失败");
    //    }
    //    //更新数据库
    //    InterfaceInfo interfaceInfo = new InterfaceInfo();
    //    interfaceInfo.setId(id);
    //    interfaceInfo.setStatus(InterfaceInfoStatusEnum.ONLINE.getValue());
    //    boolean isSuccessful = interfaceInfoService.updateById(interfaceInfo);
    //    return ResultUtils.success(isSuccessful);
    //}
    //
    ///**
    // * 下线接口
    // *
    // * @param idRequest
    // * @return
    // */
    //@PostMapping("/offline")
    //@ApiOperation("下线接口")
    //@AuthCheck(mustRole = "admin")
    //public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest) {
    //    if (idRequest == null || idRequest.getId() < 0) {
    //        throw new BusinessException(ErrorCode.PARAMS_ERROR);
    //    }
    //    //判断接口是否存在
    //    long id = idRequest.getId();
    //    InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
    //    if (oldInterfaceInfo == null) {
    //        throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
    //    }
    //    //更新数据库
    //    InterfaceInfo interfaceInfo = new InterfaceInfo();
    //    interfaceInfo.setId(id);
    //    interfaceInfo.setStatus(InterfaceInfoStatusEnum.OFFLINE.getValue());
    //    boolean isSuccessful = interfaceInfoService.updateById(interfaceInfo);
    //    return ResultUtils.success(isSuccessful);
    //}
}
