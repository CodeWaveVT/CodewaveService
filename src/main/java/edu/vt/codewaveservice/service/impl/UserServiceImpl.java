package edu.vt.codewaveservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.vt.codewaveservice.model.entity.User;
import edu.vt.codewaveservice.service.UserService;
import edu.vt.codewaveservice.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author yukun
* @description 针对表【user(user)】的数据库操作Service实现
* @createDate 2023-09-10 11:29:07
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




