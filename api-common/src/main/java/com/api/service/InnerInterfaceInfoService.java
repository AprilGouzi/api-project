package com.api.service;

import com.api.model.entity.InterfaceInfo;

/**
 * @author 囍崽
 * version 1.0
 */
public interface InnerInterfaceInfoService {

    /**
     * 根据path,method查询接口信息
     * @param path
     * @param method
     * @return
     */
    InterfaceInfo getInvokeInterfaceInfo(String path,String method);
}
