package com.api.service;

import com.api.model.entity.UserInterfaceInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 20466
* @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Service
* @createDate 2024-05-01 23:07:11
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {

    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    boolean invokeInterfaceCount(long userId,long interfaceInfoId);
}
