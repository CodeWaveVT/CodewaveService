package edu.vt.codewaveservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.vt.codewaveservice.common.BaseResponse;
import edu.vt.codewaveservice.common.ErrorCode;
import edu.vt.codewaveservice.common.ResultUtils;
import edu.vt.codewaveservice.exception.BusinessException;
import edu.vt.codewaveservice.model.dto.UserLoginRequest;
import edu.vt.codewaveservice.model.dto.UserRegisterRequest;
import edu.vt.codewaveservice.model.entity.User;
import edu.vt.codewaveservice.service.UserService;
import edu.vt.codewaveservice.mapper.UserMapper;
import edu.vt.codewaveservice.utils.MailUtils;
import edu.vt.codewaveservice.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static edu.vt.codewaveservice.common.ErrorCode.USER_EXIST_ERROR;
import static edu.vt.codewaveservice.utils.SystemConstants.USER_LOGIN_STATE;
import static edu.vt.codewaveservice.utils.SystemConstants.USER_PASSWORD_SALT;

/**
* @author yukun
* @description 针对表【user(user)】的数据库操作Service实现
* @createDate 2023-09-10 11:29:07
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserMapper userMapper;

    @Override
    public User getLoginUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute("user_login");
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    @Override
    public BaseResponse userRegister(UserRegisterRequest userRegisterRequest, HttpSession session) {
        System.out.println("register session id "+session.getId());
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String validateCode = userRegisterRequest.getValidateCode();

        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,validateCode)){
            log.info("userAccount:{} userPassword:{} checkPassword:{} validateCode:{} is null", userAccount,userPassword,checkPassword,validateCode);
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(),"userAccount or userPassword or checkPassword or validateCode is null");
        }

        if(!userPassword.equals(checkPassword)){
            log.info("userPassword:{} is not equal checkPassword:{}", userPassword,checkPassword);
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(),"userPassword is not equal checkPassword");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = userMapper.selectCount(queryWrapper);

        if(count>0){
            log.info("userAccount:{} is already exist", userAccount);
            return ResultUtils.error(ErrorCode.USER_EXIST_ERROR,"userAccount is already exist");
        }

        Object cacheCode = session.getAttribute(userAccount);
        log.info("validatecode:{},cacheCode{}", validateCode, cacheCode);
        if (cacheCode == null || !cacheCode.toString().equals(validateCode)) {
            //4. 不一致则报错
            log.info("validateCode is not equal cacheCode");
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(),"validateCode is not equal cacheCode");
        }

        String encryptpassword = DigestUtils.md5DigestAsHex((USER_PASSWORD_SALT+userPassword).getBytes());

        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptpassword);

        int save = userMapper.insert(user);
        if(save!=1){
            return ResultUtils.error(ErrorCode.OPERATION_ERROR,"save user error");
        }
        log.info("register success, userAccount:{}", userAccount);

        return ResultUtils.success("register success");
    }

    @Override
    public BaseResponse forgetPassword(UserRegisterRequest userRegisterRequest, HttpSession session) {
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String validateCode = userRegisterRequest.getValidateCode();

        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,validateCode)){
            log.info("userAccount:{} userPassword:{} checkPassword:{} validateCode:{} is null", userAccount,userPassword,checkPassword,validateCode);
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(),"userAccount or userPassword or checkPassword or validateCode is null");
        }

        if(!userPassword.equals(checkPassword)){
            log.info("userPassword:{} is not equal checkPassword:{}", userPassword,checkPassword);
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(),"userPassword is not equal checkPassword");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = userMapper.selectCount(queryWrapper);

        if(count<=0){
            log.info("userAccount:{} not exist", userAccount);
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR,"userAccount not exist,please register");
        }

        Object cacheCode = session.getAttribute(userAccount);
        log.info("validatecode:{},cacheCode{}", validateCode, cacheCode);
        if (cacheCode == null || !cacheCode.toString().equals(validateCode)) {
            //4. 不一致则报错
            log.info("validateCode is not equal cacheCode");
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(),"validateCode is not equal cacheCode");
        }

        String encryptpassword = DigestUtils.md5DigestAsHex((USER_PASSWORD_SALT+userPassword).getBytes());

        User user = new User();
        user.setUserPassword(encryptpassword);

        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("userAccount", userAccount);

        int save = userMapper.update(user, updateWrapper);

        if(save!=1){
            return ResultUtils.error(ErrorCode.OPERATION_ERROR,"save user error");
        }
        log.info("reset password success, userAccount:{}", userAccount);

        return ResultUtils.success("reset password success");
    }

    @Override
    public BaseResponse sendValidateCode(String email, HttpSession session) {
        System.out.println("send session id "+session.getId());
        if (RegexUtils.isEmailInvalid(email)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"wrong email format");
        }
        String code = MailUtils.achieveCode();
        session.setAttribute(email, code);
        //System.out.println(" ============="+session.getAttribute(email));
        log.info("发送登录验证码send code：{}", code);

        try {
            MailUtils.sendTestMail(email, code);
        } catch (MessagingException e) {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR,"send code failed ");
        }

        return ResultUtils.success("send code success");
    }

    @Override
    public BaseResponse doLogin(UserLoginRequest loginRequest, HttpServletRequest httpServletRequest) {
        String userAccount = loginRequest.getUserAccount();
        String userPassword = loginRequest.getUserPassword();

        System.out.println("userAccount:"+userAccount);
        System.out.println("userPassword:"+userPassword);

        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(),"userAccount or userPassword is null");
        }

        String encryptpassword = DigestUtils.md5DigestAsHex((USER_PASSWORD_SALT+userPassword).getBytes());

        //查询用户存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptpassword);
        User user = userMapper.selectOne(queryWrapper);
        if(user==null){
            log.info("login failed user not found");
            return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR,"user not found");
        }

        //记录用户登录态
        httpServletRequest.getSession().setAttribute(USER_LOGIN_STATE,user);

        User safeUser = new User();
        safeUser.setId(user.getId());
        safeUser.setUserAccount(user.getUserAccount());
        safeUser.setUserAccount(user.getUserAccount());

        log.info("login success, userAccount:{}", userAccount);

        return ResultUtils.success(safeUser);
    }

    @Override
    public BaseResponse doLogout(HttpServletRequest httpServletRequest) {
        httpServletRequest.getSession().removeAttribute(USER_LOGIN_STATE);
        return ResultUtils.success("logout success");
    }

}




