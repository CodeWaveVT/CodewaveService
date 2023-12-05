package edu.vt.codewaveservice.service.impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import edu.vt.codewaveservice.common.BaseResponse;
import edu.vt.codewaveservice.mapper.TaskMapper;
import edu.vt.codewaveservice.model.entity.Task;
import edu.vt.codewaveservice.model.vo.TaskVo;
import edu.vt.codewaveservice.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

class TaskServiceImplTest {

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTaskByIdTest() {
        Long userId = 1L;
        Task mockTask = new Task();
        when(taskMapper.selectList(any())).thenReturn(Arrays.asList(mockTask));

        Map<String, List<TaskVo>> result = taskService.getTaskById(userId);

        assertNotNull(result);
        assertTrue(result.containsKey("successTasks"));
        assertTrue(result.containsKey("otherTasks"));
    }

    @Test
    void deleteTaskByIdTest() {
        Long userId = 1L;
        String taskId = "taskId";
        when(taskMapper.delete(any())).thenReturn(1);

        BaseResponse response = taskService.deleteTaskById(userId, taskId);

        assertNotNull(response);
        assertEquals("delete success", response.getData());
    }
}