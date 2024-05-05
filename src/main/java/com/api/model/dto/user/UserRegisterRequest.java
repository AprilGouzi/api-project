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
@ApiModel(description = "用户注册请求")
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    @ApiModelProperty(value = "用户账户",required = true)
    private String userAccount;

    @ApiModelProperty(value = "用户密码",required = true)
    private String userPassword;

    @ApiModelProperty(value = "检查密码",required = true)
    private String checkPassword;
}
