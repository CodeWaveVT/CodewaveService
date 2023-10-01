package edu.vt.codewaveservice.controller;

import edu.vt.codewaveservice.common.BaseResponse;
import edu.vt.codewaveservice.common.ErrorCode;
import edu.vt.codewaveservice.common.ResultUtils;
import edu.vt.codewaveservice.exception.ThrowUtils;
import edu.vt.codewaveservice.manager.AiXunFeiManager;
import edu.vt.codewaveservice.manager.RedisLimitManager;
import edu.vt.codewaveservice.model.dto.GenAudioBookRequest;
import edu.vt.codewaveservice.model.entity.Task;
import edu.vt.codewaveservice.model.entity.User;
import edu.vt.codewaveservice.model.vo.TaskResponse;
import edu.vt.codewaveservice.model.vo.TaskVo;
import edu.vt.codewaveservice.service.TaskService;
import edu.vt.codewaveservice.service.UserService;
import edu.vt.codewaveservice.utils.TaskIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

import static edu.vt.codewaveservice.utils.ConvertUtil.convertEpubToTxt;

@RestController
@RequestMapping("/task")
@Slf4j
public class TaskController {

    @Resource
    private RedisLimitManager redisLimitManager;
    @Resource
    private UserService userService;

    @Resource
    private TaskService taskService;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private AiXunFeiManager aiXunFeiManager;

    @PostMapping("/gen/async")
    public BaseResponse<TaskResponse> genChartByAi(@RequestPart("file") MultipartFile file,
                                                   GenAudioBookRequest genAudioBookRequest, HttpServletRequest httpServletRequest) {
        String name = genAudioBookRequest.getBookName();
        String type = genAudioBookRequest.getBookType();
        ThrowUtils.throwIf(file.isEmpty(),ErrorCode.PARAMS_ERROR,"empty ebook");
        //ThrowUtils.throwIf(StringUtils.isBlank(name), ErrorCode.PARAMS_ERROR, "empty book name");
        //ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");

//        User loginUser = userService.getLoginUser(httpServletRequest);
//         todo
//        redisLimitManager.doRateLimit("getChartById_"+loginUser.getId());

            // Convert the EPUB file to TXT
            String txtContent = null;
            try {
                txtContent = convertEpubToTxt(file);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            //log.info("convert finish "+txtContent);
            System.out.println(txtContent);

            Task task = new Task();
            task.setId(0L);
            task.setEbookname(name);
            task.setBookType(type);
            task.setEbookTextData(txtContent);
            task.setUserId(-1L);
            task.setCreateTime(new Date());
            task.setUpdateTime(new Date());
            task.setIsDelete(0);
        boolean saveResult = taskService.save(task);

        String finalTxtContent = txtContent;

        CompletableFuture.runAsync(() -> {
            Task updateTask = new Task();
            updateTask.setId(task.getId());
            updateTask.setStatus("running");

            boolean b = taskService.updateById(updateTask);
            if (!b) {
                handleChartUpdateError(task.getId(), "update task running status failed");
                return;
            }

            String result = null;
            try {
                result = aiXunFeiManager.TextToAudioMultiPart(finalTxtContent, name);
                System.out.println("generate result" + result);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (result.length() == 0) {
                handleChartUpdateError(task.getId(), "AI gen error");
                return;
            }

            Task finishTask = new Task();
            updateTask.setId(task.getId());
            updateTask.setStatus("success");
            updateTask.setGenAudioUrl(result);

            boolean updateResult = taskService.updateById(finishTask);
            if (!updateResult) {
                handleChartUpdateError(task.getId(), "update task finish status failed");
            }
        }, threadPoolExecutor);

        TaskResponse response = new TaskResponse();
        response.setGenId("-1");
        return ResultUtils.success(response);
    }

    @PostMapping("/list/completed")
    public BaseResponse<List<TaskVo>> getCompletedTaskList(HttpServletRequest httpServletRequest) {
        User loginUser = userService.getLoginUser(httpServletRequest);
        Long userId = loginUser.getId();
        Map<String, List<TaskVo>> taskList = taskService.getTaskById(userId);
        List<TaskVo> successTasks = taskList.get("successTasks");
        return ResultUtils.success(successTasks);
    }

    @PostMapping("/list/processing")
    public BaseResponse<List<TaskVo>> getProcessingTaskList(HttpServletRequest httpServletRequest) {
        User loginUser = userService.getLoginUser(httpServletRequest);
        Long userId = loginUser.getId();
        Map<String, List<TaskVo>> taskList = taskService.getTaskById(userId);
        List<TaskVo> otherTasks = taskList.get("otherTasks");
        return ResultUtils.success(otherTasks);
    }

    private void handleChartUpdateError(long taskId, String execMessage) {
        Task updateTask = new Task();
        updateTask.setId(taskId);
        updateTask.setStatus("failed");
        updateTask.setExecMessage(execMessage);

        boolean updateResult = taskService.updateById(updateTask);
        if (!updateResult) {
            log.error("hundle update status failed" + taskId + "," + execMessage);
        }
    }
}
