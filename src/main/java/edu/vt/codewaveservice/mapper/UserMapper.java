package edu.vt.codewaveservice.mapper;

import edu.vt.codewaveservice.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author yukun
* @description 针对表【user(user)】的数据库操作Mapper
* @createDate 2023-09-10 11:29:07
* @Entity edu.vt.codewaveservice.model.entity.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




