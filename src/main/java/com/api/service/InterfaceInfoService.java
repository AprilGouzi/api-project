package com.api.service;

import com.api.model.entity.InterfaceInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 20466
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2024-04-30 11:13:02
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    public void validInterfaceInfo(InterfaceInfo interfaceInfo,boolean add);
}
