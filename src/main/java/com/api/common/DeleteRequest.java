package com.api.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 囍崽
 * version 1.0
 * 删除请求，所有接口都是适用，所以做成一个通用类
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * 删除id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}
