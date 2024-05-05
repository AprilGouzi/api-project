package com.api.model.vo;

import com.api.model.entity.InterfaceInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 囍崽
 * version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InvokeInterfaceInfoVO extends InterfaceInfo {

    /**
     * 接口调用次数
     */
    private Integer invokeNum;

    private static final long serialVersionUID = 1L;
}
