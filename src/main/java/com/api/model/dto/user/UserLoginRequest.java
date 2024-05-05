package com.api.model.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 囍崽
 * version 1.0
 */
@Data
@ApiModel(description = "用户登录请求")
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    @ApiModelProperty(value = "用户账户", required = true)
    private String userAccount;

    @ApiModelProperty(value = "用户密码", required = true)
    private String userPassword;

}
