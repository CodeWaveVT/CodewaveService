package edu.vt.codewaveservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.vt.codewaveservice.common.ErrorCode;
import edu.vt.codewaveservice.exception.BusinessException;
import edu.vt.codewaveservice.model.entity.User;
import edu.vt.codewaveservice.service.UserService;
import edu.vt.codewaveservice.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
* @author yukun
* @description 针对表【user(user)】的数据库操作Service实现
* @createDate 2023-09-10 11:29:07
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

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

}




