package edu.vt.codewaveservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import edu.vt.codewaveservice.common.BaseResponse;
import edu.vt.codewaveservice.common.ErrorCode;
import edu.vt.codewaveservice.common.ResultUtils;
import edu.vt.codewaveservice.model.dto.UserLoginRequest;
import edu.vt.codewaveservice.model.dto.UserRegisterRequest;
import edu.vt.codewaveservice.service.UserService;
import edu.vt.codewaveservice.utils.MailUtils;
import edu.vt.codewaveservice.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/code")
    public BaseResponse sendValidateCode(@RequestParam("email") String email, HttpSession session) throws MessagingException {
       return userService.sendValidateCode(email,session);
    }

    @PostMapping("/register")
    public BaseResponse userRegister(@RequestBody UserRegisterRequest userRegisterRequest, HttpSession session){
        return  userService.userRegister(userRegisterRequest,session);
    }

    @PostMapping("/login")
    public BaseResponse userLogin(@RequestBody UserLoginRequest loginRequest, HttpServletRequest httpServletRequest){
        return  userService.doLogin(loginRequest,httpServletRequest);
    }

    @PostMapping("/logout")
    public BaseResponse userLogout(HttpServletRequest httpServletRequest){
        return  userService.doLogout(httpServletRequest);
    }
}
