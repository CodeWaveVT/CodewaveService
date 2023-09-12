package edu.vt.codewaveservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.vt.codewaveservice.model.entity.Task;
import edu.vt.codewaveservice.service.TaskService;
import edu.vt.codewaveservice.mapper.TaskMapper;
import org.springframework.stereotype.Service;

/**
* @author yukun
* @description 针对表【task(task info table)】的数据库操作Service实现
* @createDate 2023-09-10 11:29:07
*/
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task>
    implements TaskService{

}




