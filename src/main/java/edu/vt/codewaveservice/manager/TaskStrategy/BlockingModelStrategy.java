package edu.vt.codewaveservice.manager.TaskStrategy;

import edu.vt.codewaveservice.common.DistributedLock;
import edu.vt.codewaveservice.manager.TaskProcessingStrategy;
import edu.vt.codewaveservice.model.entity.Task;
import edu.vt.codewaveservice.processor.*;
import edu.vt.codewaveservice.service.TaskService;
import edu.vt.codewaveservice.utils.TempFileManager;
import edu.vt.codewaveservice.utils.YashUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

@Slf4j
public class BlockingModelStrategy implements TaskProcessingStrategy {

    private TaskService taskService;

    public BlockingModelStrategy(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void process(Task task, ExecutorService executorService) {

        executorService.submit(() -> {

            try {
                Task runningTask = new Task.Builder()
                        .withId(task.getId())
                        .withStatus("running")
                        .build();
                boolean b = taskService.updateById(runningTask);
                if (!b) {
                    handleUpdateError(task.getId(), "update task running status failed");
                    return;
                }

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

            } catch (Exception e) {
            } finally {
                DistributedLock.unlock(DistributedLock.extractLockName(task.getModelType()));
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
