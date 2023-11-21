package edu.vt.codewaveservice.service;

import edu.vt.codewaveservice.common.BaseResponse;
import edu.vt.codewaveservice.model.dto.UserLoginRequest;
import edu.vt.codewaveservice.model.dto.UserRegisterRequest;
import edu.vt.codewaveservice.model.dto.UserResetRequest;
import edu.vt.codewaveservice.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.vt.codewaveservice.model.vo.TaskVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
* @author yukun
* @description 针对表【user(user)】的数据库操作Service
* @createDate 2023-09-10 11:29:07
*/
public interface UserService extends IService<User> {

    User getLoginUser(HttpServletRequest request);

    BaseResponse userRegister(UserRegisterRequest userRegisterRequest, HttpSession session);

    BaseResponse forgetPassword(UserResetRequest userResetRequest, HttpSession session);

    BaseResponse sendValidateCode(String userAccount, HttpSession session);

    BaseResponse doLogin(UserLoginRequest loginRequest, HttpServletRequest httpServletRequest);

    BaseResponse doLogout(HttpServletRequest httpServletRequest);


}
