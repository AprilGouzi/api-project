package com.api.service.impl.inner;

import cn.hutool.core.util.StrUtil;
import com.api.common.ErrorCode;
import com.api.exception.BusinessException;
import com.api.mapper.UserMapper;
import com.api.model.entity.User;
import com.api.service.InnerUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import javax.annotation.Resource;

/**
 * @author 囍崽
 * version 1.0
 */
public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User getInvokeUser(String accessKey) {
        if (StrUtil.isBlank(accessKey)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getAccessKey, accessKey);

        return userMapper.selectOne(queryWrapper);
    }
}
