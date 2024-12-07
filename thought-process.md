**Though process**   the design considerations and implementation approach for the assignment, focusing on handling high-throughput requests, deduplication, and distributed processing.
**1. Problem Understanding**
    The application is a REST service that:
        1. Accepts requests via a /api/verve/accept endpoint.
        2. Processes at least 10,000 requests per second.
        3. Logs the unique request count every minute.
        4. Optionally calls an HTTP endpoint with the unique count.
        5. Ensures deduplication works behind a load balancer.
        6. Streams the count of unique requests to a distributed system like Kafka.


**2. High-Level Design** Core Design
        1. The application uses Redis to handle deduplication efficiently under high load.
        2. Scheduled tasks log unique request counts every minute and send the data to a Kafka topic for further processing.
        3. A centralized approach ensures consistency across multiple application instances in a distributed setup.


**3. Implementation Approach**  Deduplication Redis Sorted Sets (ZSet) are used for deduplication:
         1.  Key: unique-requests:<current-minute>.
         2.  Value: Request id.
         3.  Score: Request timestamp.
         Time-to-Live (TTL) of 2 minutes ensures periodic cleanup.
          Redis operations (ZADD, ZCARD) are atomic, preventing race conditions.
**4. Concurrency and Scalability**
        Stateless Service: Application instances are stateless; Redis handles state centrally.
        Redis allows deduplication across all instances, ensuring consistency behind a load balancer.

**5. Logging and Notifications**
        A scheduled task logs the unique request count every minute using SLF4J.
        Optionally, the count is sent via an HTTP POST request to the provided endpoint.

**6. Distributed Streaming**
          Unique request counts are sent to Kafka for downstream processing and analytics.

**3. Extensions Question**
      1. HTTP POST Requests:** Unique counts are sent as JSON payloads when an endpoint is provided.
      
      2. Distributed Deduplication: 
             Redis ensures consistent ID deduplication across all instances, avoiding duplicates even under a load balancer.
      
      3. Kafka Integration: Instead of logging, unique counts are streamed to Kafka for durable and scalable processing.
