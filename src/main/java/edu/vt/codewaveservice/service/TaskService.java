package edu.vt.codewaveservice.service;

import edu.vt.codewaveservice.model.entity.Task;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.vt.codewaveservice.model.vo.TaskVo;

import java.util.List;
import java.util.Map;

/**
* @author yukun
* @description 针对表【task(task info table)】的数据库操作Service
* @createDate 2023-09-10 11:29:07
*/
public interface TaskService extends IService<Task> {

    Map<String, List<TaskVo>> getTaskById(Long userId);

}
