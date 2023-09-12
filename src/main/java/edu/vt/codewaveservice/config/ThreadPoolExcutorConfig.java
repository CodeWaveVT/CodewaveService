package edu.vt.codewaveservice.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolExcutorConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        ThreadFactory threadFactory = new ThreadFactory() {
            private int count = 1;
            @Override
            public Thread newThread(@NotNull Runnable r) {
                Thread t = new Thread(r);
                t.setName("Codewave thread:" + count);
                count++;
                return t;
            }
        };
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2,4,100, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(4),threadFactory);
        return threadPoolExecutor;
    }
}
