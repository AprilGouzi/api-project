package com.api.service;

/**
 * @author 囍崽
 * version 1.0
 */
public interface InnerUserInterfaceInfoService {

    /**
     * 是否有调用次数
     * @param userId
     * @param interfaceInfoId
     * @return
     */
    boolean hasInvokeNum(long userId,long interfaceInfoId);

    /**
     * 根据userId,interfaceInfoId 计数
     * @param userId
     * @param interfaceInfoId
     * @return
     */
    boolean invokeInterfaceCount(long userId,long interfaceInfoId);
}
