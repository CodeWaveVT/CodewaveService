package edu.vt.codewaveservice.service;

import edu.vt.codewaveservice.common.BaseResponse;
import edu.vt.codewaveservice.model.dto.UserLoginRequest;
import edu.vt.codewaveservice.model.dto.UserRegisterRequest;
import edu.vt.codewaveservice.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
* @author yukun
* @description 针对表【user(user)】的数据库操作Service
* @createDate 2023-09-10 11:29:07
*/
public interface UserService extends IService<User> {

    User getLoginUser(HttpServletRequest request);

    BaseResponse userRegister(UserRegisterRequest userRegisterRequest, HttpSession session);

    BaseResponse sendValidateCode(String userAccount, HttpSession session);

    /**
     *
     * @return
     */
    BaseResponse doLogin(UserLoginRequest loginRequest, HttpServletRequest httpServletRequest);

    BaseResponse doLogout(HttpServletRequest httpServletRequest);

}
