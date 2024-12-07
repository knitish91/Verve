package Verve.assignment.Verve.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestProcessorService {

    private static final Logger logger = LoggerFactory.getLogger(RequestProcessorService.class);

    @Autowired
    private RedisDeduplicationService redisDeduplicationService;

    public void processRequest(Integer id, String endpoint) {
        long timestamp = System.currentTimeMillis();
        redisDeduplicationService.handleCountRequest(String.valueOf(id), timestamp);
    }
}