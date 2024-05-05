package com.api.service.impl;

import com.api.common.ErrorCode;
import com.api.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.api.model.entity.UserInterfaceInfo;
import com.api.service.UserInterfaceInfoService;
import com.api.mapper.UserInterfaceInfoMapper;
import org.springframework.stereotype.Service;

/**
 * @author 20466
 * @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Service实现
 * @createDate 2024-05-01 23:07:11
 */
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
        implements UserInterfaceInfoService {

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //创建时，所有参数必须非空
        if (add) {
            if (userInterfaceInfo.getInterfaceInfoId() <= 0 || userInterfaceInfo.getUserId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口或用户不存在");
            }
        }
        if (userInterfaceInfo.getLeftNum() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "剩余次数不能小于0");
        }
    }

    // TODO 添加事务，添加锁
    /**
     * 调用次数统计
     * @param userId
     * @param interfaceInfoId
     * @return
     */
    @Override
    public boolean invokeInterfaceCount(long userId, long interfaceInfoId) {
        if (userId <= 0 || interfaceInfoId <= 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        //构造条件构造器
        LambdaUpdateWrapper<UserInterfaceInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserInterfaceInfo::getUserId, userId)
                .eq(UserInterfaceInfo::getInterfaceInfoId, interfaceInfoId)
                .gt(UserInterfaceInfo::getLeftNum, 0)
                .setSql("left_num = left_num - 1,total_num = total_num + 1");

        return this.update(updateWrapper);
    }
}




