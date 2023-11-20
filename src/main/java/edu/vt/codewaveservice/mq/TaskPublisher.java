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
    private final String topicName = "tasks";

    private final Gson gson = new Gson();

    @Autowired
    public TaskPublisher(RedissonClient redissionClient) {
        this.redissionClient = redissionClient;
    }

    public void publish(Task task) {
        String queueName = "taskQueue:" + task.getModelType();
        RList<String> queue = redissionClient.getList(queueName);
        String jsonTask = gson.toJson(task);
        queue.add(jsonTask);
        log.info("Publishing task to Redis: {}", jsonTask.substring(0, 100));
    }
}

