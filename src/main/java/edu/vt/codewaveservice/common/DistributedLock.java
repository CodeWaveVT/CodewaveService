package edu.vt.codewaveservice.common;


import org.redisson.api.RedissonClient;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class DistributedLock {

    private static RedissonClient redissonClient;
    private static final ConcurrentHashMap<String, String> lockMap = new ConcurrentHashMap<>();
    private static final long leaseTime = 60; // 默认租约时间，例如60秒
    private static final TimeUnit timeUnit = TimeUnit.SECONDS; // 时间单位

    // 禁止外部实例化
    private void DistributedLockManager() {}

    // 静态方法用于设置RedissonClient
    public static void setRedissonClient(RedissonClient client) {
        redissonClient = client;
    }

    public static boolean tryLock(String lockName) {
        String lockValue = lockMap.computeIfAbsent(lockName, k -> java.util.UUID.randomUUID().toString());
        return redissonClient.getBucket(lockName).trySet(lockValue, leaseTime, timeUnit);
    }

    public static void unlock(String lockName) {
        String lockValue = lockMap.get(lockName);
        if (lockValue != null && redissonClient.getBucket(lockName).isExists()) {
            if (redissonClient.getBucket(lockName).get().equals(lockValue)) {
                redissonClient.getKeys().delete(lockName);
                lockMap.remove(lockName);
            }
        }
    }

    public static String extractLockName(String modelType) {
        int firstUnderscoreIndex = modelType.indexOf('_');
        if (firstUnderscoreIndex == -1) {
            // 没有下划线，返回整个字符串
            return modelType;
        }
        int secondUnderscoreIndex = modelType.indexOf('_', firstUnderscoreIndex + 1);
        if (secondUnderscoreIndex == -1) {
            // 只有一个下划线，返回整个字符串
            return modelType;
        }
        return modelType.substring(0, secondUnderscoreIndex);
    }
}