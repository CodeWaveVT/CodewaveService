//package edu.vt.codewaveservice.mq;
//
//import edu.vt.codewaveservice.manager.TTSModels.TTSModelProperties;
//import org.redisson.api.RTopic;
//import org.redisson.api.RedissonClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.listener.PatternTopic;
//import org.springframework.data.redis.listener.RedisMessageListenerContainer;
//import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
//
//@Configuration
//public class RedisConfig {
//
//    @Autowired
//    private TaskListenerFactory taskListenerFactory;
//
//    @Autowired
//    private TTSModelProperties ttsModelProperties;
//
//    @Autowired
//    private RedissonClient redissonClient;
//
//    @Bean
//    public void registerTaskListeners() {
//        ttsModelProperties.getModels().keySet().forEach(modelType -> {
//            TaskListener listener = taskListenerFactory.createListener(modelType);
//            RTopic topic = redissonClient.getTopic(modelType);
//            topic.addListener(String.class, listener);
//        });
//    }
//}
