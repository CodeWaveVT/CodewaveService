package edu.vt.codewaveservice.controller;

import edu.vt.codewaveservice.common.BaseResponse;
import edu.vt.codewaveservice.common.ResultUtils;
import edu.vt.codewaveservice.model.entity.User;
import edu.vt.codewaveservice.model.vo.TaskVo;
import edu.vt.codewaveservice.service.TaskService;
import edu.vt.codewaveservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TaskControllerTest {
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }

    @Test
    void getProcessingTaskList() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        TaskVo mockTask1 = new TaskVo();
        mockTask1.setTaskId("1");
        mockTask1.setStatus("failed");
        mockTask1.setBookName("test");

        TaskVo mockTask2 = new TaskVo();
        mockTask2.setTaskId("2");
        mockTask2.setStatus("failed");
        mockTask2.setBookName("test");
        List<TaskVo> mockTaskList = Arrays.asList(mockTask1,mockTask2);

        when(userService.getLoginUser(any(HttpServletRequest.class))).thenReturn(mockUser);
        when(taskService.getTaskById(anyLong())).thenReturn(Collections.singletonMap("otherTasks", mockTaskList));

        mockMvc.perform(post("/task/list/processing")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)));

        verify(userService, times(1)).getLoginUser(any(HttpServletRequest.class));
        verify(taskService, times(1)).getTaskById(anyLong());
    }

    @Test
    void getCompletedTaskList() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);

        TaskVo mockTask1 = new TaskVo();
        mockTask1.setTaskId("1");
        mockTask1.setStatus("success");
        mockTask1.setBookName("test");

        TaskVo mockTask2 = new TaskVo();
        mockTask2.setTaskId("2");
        mockTask2.setStatus("success");
        mockTask2.setBookName("test");

        List<TaskVo> mockSuccessTaskList = Arrays.asList(mockTask1, mockTask2);

        when(userService.getLoginUser(any(HttpServletRequest.class))).thenReturn(mockUser);
        when(taskService.getTaskById(anyLong())).thenReturn(Collections.singletonMap("successTasks", mockSuccessTaskList));

        mockMvc.perform(post("/task/list/completed")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)));

        verify(userService, times(1)).getLoginUser(any(HttpServletRequest.class));
        verify(taskService, times(1)).getTaskById(anyLong());
    }


    @Test
    void deleteCompletedTask() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        String mockTaskId = "taskId123";

        when(userService.getLoginUser(any(HttpServletRequest.class))).thenReturn(mockUser);
        when(taskService.deleteTaskById(eq(mockUser.getId()), eq(mockTaskId)))
                .thenReturn(ResultUtils.success("Task deleted successfully"));

        mockMvc.perform(post("/task/delete/completed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("taskId", mockTaskId)) // 添加 taskId 作为请求参数
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Task deleted successfully"));

        verify(userService, times(1)).getLoginUser(any(HttpServletRequest.class));
        verify(taskService, times(1)).deleteTaskById(eq(mockUser.getId()), eq(mockTaskId));
    }

}