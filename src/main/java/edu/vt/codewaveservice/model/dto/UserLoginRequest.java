package edu.vt.codewaveservice.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "User login request")
public class UserLoginRequest {
    @ApiModelProperty(value = "User account", required = true)
    private String userAccount;

    @ApiModelProperty(value = "User password", required = true)
    private String userPassword;
}
