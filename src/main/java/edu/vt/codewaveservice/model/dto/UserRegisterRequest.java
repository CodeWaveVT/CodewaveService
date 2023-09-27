package edu.vt.codewaveservice.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@ApiModel(description = "User registration request")
public class UserRegisterRequest implements Serializable {
    @ApiModelProperty(value = "User account", required = true)
    private String userAccount;

    @ApiModelProperty(value = "Validation code", required = true)
    private String validateCode;

    @ApiModelProperty(value = "User password", required = true)
    private String userPassword;

    @ApiModelProperty(value = "Check password", required = true)
    private String checkPassword;
}
