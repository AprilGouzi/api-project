package com.api.model.dto.InterfaceInfo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 囍崽
 * version 1.0
 * 调用接口参数
 */
@Data
public class InvokeInterfaceRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 请求参数
     */
    private String requestParams;
}
