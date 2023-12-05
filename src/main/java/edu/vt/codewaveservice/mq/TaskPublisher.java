package edu.vt.codewaveservice.mq;

import com.google.gson.Gson;
import edu.vt.codewaveservice.model.entity.Task;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TaskPublisher {
    private final RedissonClient redissionClient;
    private final Gson gson = new Gson();

    @Autowired
    public TaskPublisher(RedissonClient redissionClient) {
        this.redissionClient = redissionClient;
    }

    public void publish(Task task) {
        String modelType = task.getModelType();
        if(modelType.contains("yash")){
            modelType = "yash";
        }
        String queueName = "taskQueue:" + modelType;
        RList<String> queue = redissionClient.getList(queueName);
        String jsonTask = gson.toJson(task);
        queue.add(jsonTask);
        log.info("Publishing task to Redis: {}", jsonTask.substring(0, 100));
    }

    public synchronized int getQueueSize(String modelType) {
        if(modelType.contains("yash")){
            modelType = "yash";
        }
        String queueName = "taskQueue:" + modelType;
        RList<String> queue = redissionClient.getList(queueName);
        return queue.size();
    }
}