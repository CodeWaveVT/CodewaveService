package edu.vt.codewaveservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.vt.codewaveservice.model.entity.Task;
import edu.vt.codewaveservice.model.vo.TaskVo;
import edu.vt.codewaveservice.service.TaskService;
import edu.vt.codewaveservice.mapper.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author yukun
* @description 针对表【task(task info table)】的数据库操作Service实现
* @createDate 2023-09-10 11:29:07
*/
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task>
    implements TaskService{

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public Map<String, List<TaskVo>> getTaskById(Long userId) {
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("createTime");
        queryWrapper.eq("userId", userId);

        List<Task> tasks = taskMapper.selectList(queryWrapper);

        List<TaskVo> successTasks = new ArrayList<>();
        List<TaskVo> otherTasks = new ArrayList<>();

        for (Task task : tasks) {
            TaskVo taskVo = new TaskVo();
            taskVo.setTaskId(String.valueOf(task.getId()));
            taskVo.setBookName(task.getEbookname());
            taskVo.setBookUrl(task.getGenAudioUrl());
            taskVo.setAuthor("unknown");
            taskVo.setCreateTime(task.getCreateTime());
            taskVo.setStatus(task.getStatus());

            if ("success".equalsIgnoreCase(task.getStatus())) {
                successTasks.add(taskVo);
            } else {
                otherTasks.add(taskVo);
            }
        }

        Map<String, List<TaskVo>> resultMap = new HashMap<>();
        resultMap.put("successTasks", successTasks);
        resultMap.put("otherTasks", otherTasks);

        return resultMap;
    }
}




