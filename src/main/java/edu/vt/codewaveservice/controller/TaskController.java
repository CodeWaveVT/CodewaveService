package edu.vt.codewaveservice.controller;

import edu.vt.codewaveservice.common.BaseResponse;
import edu.vt.codewaveservice.common.ErrorCode;
import edu.vt.codewaveservice.common.ResultUtils;
import edu.vt.codewaveservice.exception.ThrowUtils;
import edu.vt.codewaveservice.manager.RedisLimitManager;
import edu.vt.codewaveservice.model.dto.GenAudioBookRequest;
import edu.vt.codewaveservice.model.entity.Task;
import edu.vt.codewaveservice.model.entity.User;
import edu.vt.codewaveservice.model.vo.TaskResponse;
import edu.vt.codewaveservice.model.vo.TaskVo;
import edu.vt.codewaveservice.processor.ProcessingContext;
import edu.vt.codewaveservice.processor.Processor;
import edu.vt.codewaveservice.processor.ProcessorException;
import edu.vt.codewaveservice.processor.TextToAudioChain;
import edu.vt.codewaveservice.processor.CriticalProcessor;
import edu.vt.codewaveservice.service.TaskService;
import edu.vt.codewaveservice.service.UserService;
import edu.vt.codewaveservice.utils.TaskIdUtil;
import edu.vt.codewaveservice.utils.TempFileManager;
import lombok.extern.slf4j.Slf4j;
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
import java.util.concurrent.ThreadPoolExecutor;

import static java.lang.Thread.sleep;

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
                .withStatus("waiting")
                .withUserId(-1L)
                .withEbookOriginData(ebookData)
                .build();

        boolean saveResult = taskService.save(task);

        CompletableFuture.runAsync(() -> {
            Task runningTask = new Task.Builder()
                    .withId(task.getId())
                    .withStatus("running")
                    .build();

            boolean b = taskService.updateById(runningTask);

            if (!b) {
                handleUpdateError(task.getId(), "update task running status failed");
                return;
            }

            String result = "generated url";
            TextToAudioChain chain = new TextToAudioChain();
            ProcessingContext context = new ProcessingContext();
            context.setFile(task.getEbookOriginData());
            context.setFileType(task.getBookType());
//            context.setFile(file);
            context.setTempFileManager(new TempFileManager());
            String s3Url = null;

            try {
                s3Url = chain.process(context);
            } catch (ProcessorException pe) {
                Processor failingProcessor = pe.getFailedProcessor();
                if (failingProcessor.getClass().isAnnotationPresent(CriticalProcessor.class)) {
                    String errorMessage = "Error in processor: " + failingProcessor.getClass().getSimpleName();
                    handleUpdateError(task.getId(), errorMessage);
                }
                throw new RuntimeException(pe);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


            result = s3Url;
            System.out.println("generate result :" + result);

            if (result.length() == 0) {
                handleUpdateError(task.getId(), "AI gen error");
                return;
            }

            Task finishTask = new Task.Builder()
                    .withId(task.getId())
                    .withStatus("success")
                    .withGenAudioUrl(result)
                    .build();
            boolean updateResult = taskService.updateById(finishTask);

            System.out.println("update result :"+updateResult);

            if (!updateResult) {
                handleUpdateError(task.getId(), "update task finish status failed");
            }
        }, threadPoolExecutor);

        TaskResponse response = new TaskResponse();
        response.setGenId(task.getId());
        return ResultUtils.success(response);
    }

    @PostMapping("/list/test/completed")
    public BaseResponse<List<TaskVo>> getCompletedTaskListTest(HttpServletRequest httpServletRequest) {
        Long userId = -1L;
        Map<String, List<TaskVo>> taskList = taskService.getTaskById(userId);
        List<TaskVo> successTasks = taskList.get("successTasks");
        return ResultUtils.success(successTasks);
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

    private void handleUpdateError(String taskId, String execMessage) {
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
