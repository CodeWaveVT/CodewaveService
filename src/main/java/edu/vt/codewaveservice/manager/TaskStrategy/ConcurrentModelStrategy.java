package edu.vt.codewaveservice.manager.TaskStrategy;

import edu.vt.codewaveservice.manager.TaskProcessingStrategy;
import edu.vt.codewaveservice.model.entity.Task;
import edu.vt.codewaveservice.processor.*;
import edu.vt.codewaveservice.service.TaskService;
import edu.vt.codewaveservice.utils.TempFileManager;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.ExecutorService;

@Slf4j
//教训，错加了@Component注解，导致TaskDispatcher初始化时，会初始化这个类，但是这个类依赖的TaskService还没有初始化，导致错误注入
public class ConcurrentModelStrategy implements TaskProcessingStrategy {

    private TaskService taskService;
    public ConcurrentModelStrategy(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void process(Task task, ExecutorService executorService) {
        executorService.submit(() -> {
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
            context.setModelType(task.getModelType());
            System.out.println("model type :"+task.getModelType());
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
       });

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
