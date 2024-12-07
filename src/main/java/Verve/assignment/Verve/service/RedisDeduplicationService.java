package Verve.assignment.Verve.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;

@Component
public class RedisDeduplicationService {

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisDeduplicationService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void handleCountRequest(String id, long timestamp) {
        String key = getCurrentMinuteKey();

        // Check if the ID already exists for the current minute
        Double score = redisTemplate.opsForZSet().score(key, id);
        if (score != null) {
            return;
        }

        // Add the ID with timestamp as the score in Redis
        redisTemplate.opsForZSet().add(key, id, timestamp);

        // Set an expiration for the key
        redisTemplate.expire(key, Duration.ofMinutes(2));
    }

    public long getUniqueRequestCount(String key) {

        Long count = redisTemplate.opsForZSet().zCard(key);
        if (count == null) {
            return 0;
        }
        return count;
    }

    // Get the key for the current minute (used when adding the request)
    private String getCurrentMinuteKey() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        return "unique-requests:" + sdf.format(new Date());
    }

    // Get the key for the previous minute (used by scheduled task)
    public String getPreviousMinuteKey() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -1); // Subtract one minute from the current time

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        return "unique-requests:" + sdf.format(cal.getTime());
    }

    public void cleanupOldRequests() {
        String key = getCurrentMinuteKey();
        long oneMinuteAgo = System.currentTimeMillis() - 60000;

        // Remove entries older than 1 minute
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, (double) oneMinuteAgo);
    }

}

