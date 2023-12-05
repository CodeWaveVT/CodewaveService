package edu.vt.codewaveservice.mq;

import com.google.gson.Gson;
import edu.vt.codewaveservice.manager.TaskProcessingStrategy;
import edu.vt.codewaveservice.model.entity.Task;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Service
@Slf4j
public class TaskListener {

    private final Map<String, TaskProcessingStrategy> strategies;
    private final Map<String, ExecutorService> executorServices;
    private final RedissonClient redissonClient;
    private final Gson gson;

    @Autowired
    public TaskListener(Map<String, TaskProcessingStrategy> strategies,
                        Map<String, ExecutorService> executorServices,
                        RedissonClient redissonClient, Gson gson) {
        this.strategies = strategies;
        this.executorServices = executorServices;
        this.redissonClient = redissonClient;
        this.gson = gson;
    }

    @PostConstruct
    public void startListening() {
        for(String modelType : strategies.keySet()){
            if(modelType.contains("yash")){
                continue;
            }
            listenToQueue(modelType);
        }
        listenToQueue("yash");
    }

    private void listenToQueue(String modelType) {
        RBlockingQueue<String> queue = redissonClient.getBlockingQueue("taskQueue:" + modelType);
        TaskProcessingStrategy strategy = strategies.get(modelType);

        String finalModelType = modelType;

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String taskJson = queue.take(); // 阻塞直到有元素
                    Task task = gson.fromJson(taskJson, Task.class);
                    log.info("Task taken from Redis: {}", taskJson.substring(0, 100));
                    log.info("strategy: " + strategy.getClass().getSimpleName());
                    log.info("executorService: " + executorServices.get(finalModelType));
                    strategy.process(task, executorServices.get(finalModelType));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    // 日志记录异常
                }
            }
        }).start();
    }
}
