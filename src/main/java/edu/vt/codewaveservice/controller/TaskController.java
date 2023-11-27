package edu.vt.codewaveservice.controller;

import com.alibaba.excel.util.StringUtils;
import edu.vt.codewaveservice.common.BaseResponse;
import edu.vt.codewaveservice.common.DistributedLock;
import edu.vt.codewaveservice.common.ErrorCode;
import edu.vt.codewaveservice.common.ResultUtils;
import edu.vt.codewaveservice.exception.ThrowUtils;
import edu.vt.codewaveservice.manager.RedisLimitManager;
import edu.vt.codewaveservice.manager.TaskDispatcher;
import edu.vt.codewaveservice.model.dto.GenAudioBookRequest;
import edu.vt.codewaveservice.model.entity.Task;
import edu.vt.codewaveservice.model.entity.User;
import edu.vt.codewaveservice.model.vo.TaskResponse;
import edu.vt.codewaveservice.model.vo.TaskVo;
import edu.vt.codewaveservice.processor.*;
import edu.vt.codewaveservice.service.TaskService;
import edu.vt.codewaveservice.service.UserService;
import edu.vt.codewaveservice.utils.TaskIdUtil;
import edu.vt.codewaveservice.utils.TempFileManager;
import edu.vt.codewaveservice.utils.YashUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


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


    @Autowired
    private TaskDispatcher  taskDispatcher;

    @PostMapping("/gen/async")
    public BaseResponse<TaskResponse> genAudioByAi(@RequestPart("file") MultipartFile file,
                                                   GenAudioBookRequest genAudioBookRequest, HttpServletRequest httpServletRequest) {
        String name = genAudioBookRequest.getBookName();
        String type = genAudioBookRequest.getBookType();
        String author = genAudioBookRequest.getBookAuthor();
        String modelType = genAudioBookRequest.getModelType();
        ThrowUtils.throwIf(file.isEmpty(),ErrorCode.PARAMS_ERROR,"empty ebook");
        ThrowUtils.throwIf(StringUtils.isBlank(name), ErrorCode.PARAMS_ERROR, "empty book name");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");

        User loginUser = userService.getLoginUser(httpServletRequest);

        redisLimitManager.doRateLimit("getAudioById_"+loginUser.getId());

        if (modelType.contains("yash")) {
            boolean isLocked = DistributedLock.tryLock(DistributedLock.extractLockName(modelType));
            if (!isLocked) {
                return ResultUtils.error(ErrorCode.TOO_MANY_REQUEST,"Another request is in progress for yash's model, please try again later");
            }
        }

        byte[] ebookData;
        try {
            ebookData = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error reading uploaded file", e);
        }

        Task task = new Task.Builder()
                .withId(TaskIdUtil.generateTaskID())
                .withEbookname(name)
                .withBookType(type)
                .withEbookTextData(author)
                .withStatus("waiting")
                .withUserId(loginUser.getId())
                .withModelType(modelType)
                .withEbookOriginData(ebookData)
                .build();

        taskService.save(task);

        taskDispatcher.dispatch(task);

        TaskResponse response = new TaskResponse();
        response.setGenId(task.getId());
        return ResultUtils.success(response);
    }

    @PostMapping("/list/processing")
    public BaseResponse<List<TaskVo>> getProcessingTaskList(HttpServletRequest httpServletRequest) {
        User loginUser = userService.getLoginUser(httpServletRequest);
        Long userId = loginUser.getId();
        Map<String, List<TaskVo>> taskList = taskService.getTaskById(userId);
        List<TaskVo> otherTasks = taskList.get("otherTasks");
        return ResultUtils.success(otherTasks);
    }

    @PostMapping("/list/completed")
    public BaseResponse<List<TaskVo>> getCompletedTaskList(HttpServletRequest httpServletRequest) {
        User loginUser = userService.getLoginUser(httpServletRequest);
        Long userId = loginUser.getId();
        Map<String, List<TaskVo>> taskList = taskService.getTaskById(userId);
        List<TaskVo> successTasks = taskList.get("successTasks");
        return ResultUtils.success(successTasks);
    }

    @PostMapping("/delete/completed")
    public BaseResponse<String> deleteCompletedTask(HttpServletRequest httpServletRequest, String taskId) {
        User loginUser = userService.getLoginUser(httpServletRequest);
        Long userId = loginUser.getId();
        return taskService.deleteTaskById(userId, taskId);
    }

    @PostMapping("/gen/test/async")
    public BaseResponse<TaskResponse> genAudioByAiTest(@RequestPart("file") MultipartFile file,
                                                       GenAudioBookRequest genAudioBookRequest, HttpServletRequest httpServletRequest) {
        String name = genAudioBookRequest.getBookName();
        String type = genAudioBookRequest.getBookType();
        String author = genAudioBookRequest.getBookAuthor();
        String modelType = genAudioBookRequest.getModelType();
        ThrowUtils.throwIf(file.isEmpty(),ErrorCode.PARAMS_ERROR,"empty ebook");
        ThrowUtils.throwIf(StringUtils.isBlank(name), ErrorCode.PARAMS_ERROR, "empty book name");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");

        byte[] ebookData;
        try {
            ebookData = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error reading uploaded file", e);
        }

        Task task = new Task.Builder()
                .withId(TaskIdUtil.generateTaskID())
                .withEbookname(name)
                .withBookType(type)
                .withEbookTextData(author)
                .withStatus("waiting")
                .withUserId(-2L)
                .withModelType(modelType)
                .withEbookOriginData(ebookData)
                .build();

        taskService.save(task);

        if(modelType.contains("yash")) {
            System.out.printf("yash model ");

            CompletableFuture.runAsync(() -> {
                Task updateTask = new Task();
                updateTask.setId(task.getId());
                updateTask.setStatus("running");
                taskService.updateById(updateTask);
                String fileType = task.getBookType();

                ProcessingContext context = new ProcessingContext();
                context.setFile(task.getEbookOriginData());
                context.setFileType(task.getBookType());
                context.setTempFileManager(new TempFileManager());
                context.setModelType(task.getModelType());

                Processor startingProcessor = new ConverterProcessorFactory().getProcessor(fileType);
                startingProcessor.process(context);

                String resultUrl = null;
                resultUrl = YashUtil.getAudio(context.getText(),task.getEbookname(),task.getModelType());
                System.out.println("generate result: " + resultUrl);

            Task finishTask = new Task.Builder()
                    .withId(task.getId())
                    .withStatus("success")
                    .withGenAudioUrl(resultUrl)
                    .build();
            boolean updateResult = taskService.updateById(finishTask);
                System.out.println("update result :" + updateResult);
            });
        }else{
            taskDispatcher.dispatch(task);
        }

        TaskResponse response = new TaskResponse();
        response.setGenId(task.getId());
        return ResultUtils.success(response);
    }

    @PostMapping("/list/test/completed")
    public BaseResponse<List<TaskVo>> getCompletedTaskListTest(HttpServletRequest httpServletRequest) {
        Long userId = -2L;
        Map<String, List<TaskVo>> taskList = taskService.getTaskById(userId);
        List<TaskVo> successTasks = taskList.get("successTasks");
        return ResultUtils.success(successTasks);
    }



    @PostMapping("/list/test/processing")
    public BaseResponse<List<TaskVo>> getProcessingTaskListTest(HttpServletRequest httpServletRequest) {
        Long userId = -2L;
        Map<String, List<TaskVo>> taskList = taskService.getTaskById(userId);
        List<TaskVo> otherTasks = taskList.get("otherTasks");
        return ResultUtils.success(otherTasks);
    }



}
