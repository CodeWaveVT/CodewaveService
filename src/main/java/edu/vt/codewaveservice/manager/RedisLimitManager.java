package edu.vt.codewaveservice.manager;


import edu.vt.codewaveservice.common.ErrorCode;
import edu.vt.codewaveservice.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class RedisLimitManager {

    @Resource
    private RedissonClient redissonClient;

    public void doRateLimit(String key) {
        //base user to make limit
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL, 1, 1, RateIntervalUnit.SECONDS); //1 request per second
        boolean canOp = rateLimiter.tryAcquire(1); //need one token per request
        if (!canOp) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }


    }


}
