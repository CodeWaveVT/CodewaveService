package edu.vt.codewaveservice.service;

import edu.vt.codewaveservice.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author yukun
* @description 针对表【user(user)】的数据库操作Service
* @createDate 2023-09-10 11:29:07
*/
public interface UserService extends IService<User> {

    User getLoginUser(HttpServletRequest request);

}
