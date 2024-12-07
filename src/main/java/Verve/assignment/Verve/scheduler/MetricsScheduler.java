package Verve.assignment.Verve.scheduler;

import Verve.assignment.Verve.service.RedisDeduplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MetricsScheduler {


    private final RedisDeduplicationService redisDeduplicationService;
    private final Logger logger = LoggerFactory.getLogger(MetricsScheduler.class);

    @Autowired
    public MetricsScheduler(RedisDeduplicationService redisDeduplicationService) {
        this.redisDeduplicationService = redisDeduplicationService;
    }

    @Scheduled(cron = "0 * * * * *") // Run at the start of every minute
    public void logUniqueRequestCount() {
        String key = redisDeduplicationService.getPreviousMinuteKey(); // Use the previous minute's key

        long uniqueCount = redisDeduplicationService.getUniqueRequestCount(key);

        logger.info("Unique requests in the last minute: {}", uniqueCount);

        // Optional: Trigger cleanup
        redisDeduplicationService.cleanupOldRequests();

        // Stream the unique request count to Kafka (or any other distributed service)
        /*streamToKafka(uniqueCount);*/
    }

    /*
        private void streamToKafka(long uniqueCount) {
            // Kafka streaming logic here
        }
       */

}