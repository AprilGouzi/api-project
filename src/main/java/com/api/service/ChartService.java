package com.api.service;

import com.api.model.vo.InvokeInterfaceInfoVO;

import java.util.List;

/**
 * @author 囍崽
 * version 1.0
 */
public interface ChartService {
    List<InvokeInterfaceInfoVO> listTopInvokeInterfaceInfo(int limit);
}
