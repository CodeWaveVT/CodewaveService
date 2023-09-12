package edu.vt.codewaveservice.mapper;

import edu.vt.codewaveservice.model.entity.Task;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author ranhuo
* @description 针对表【task(task info table)】的数据库操作Mapper
* @createDate 2023-09-10 11:29:07
* @Entity edu.vt.codewaveservice.model.entity.Task
*/
@Mapper
public interface TaskMapper extends BaseMapper<Task> {

}




