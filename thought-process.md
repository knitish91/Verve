# **Thought Process**

This document outlines the design considerations and implementation approach for the assignment, focusing on handling high-throughput requests, deduplication, and distributed processing.

---

## **1. Problem Understanding**

The application is a REST service designed to:

1. Accept requests via the `/api/verve/accept` endpoint.
2. Handle at least **10,000 requests per second**.
3. Log the count of unique requests every minute.
4. Optionally make an HTTP request with the unique count.
5. Ensure deduplication works across multiple instances behind a load balancer.
6. Stream the count of unique requests to a distributed system like Kafka.

---

## **2. High-Level Design**

### **Core Design**
1. **Redis for Deduplication**: 
   - Redis Sorted Sets (`ZSet`) efficiently handle deduplication under high load.
   - Keys: `unique-requests:<current-minute>`.
   - Values: Request IDs, with timestamps as scores.
   - A **Time-to-Live (TTL)** of 2 minutes ensures periodic cleanup.

2. **Centralized State Management**:
   - Redis acts as a central store for consistent deduplication across distributed instances.
   - Atomic Redis operations (`ZADD`, `ZCARD`) prevent race conditions.

3. **Scalability**:
   - Stateless service instances behind a load balancer rely on Redis for deduplication.
   - The architecture supports horizontal scaling by decoupling state from the application.

---

## **3. Implementation Approach**

### **Concurrency and Scalability**
1. **Stateless Service**: Application instances do not store state; deduplication and metrics aggregation are centralized in Redis.
2. **High Throughput**: Multiple threads and asynchronous processing enable the application to handle requests efficiently.

### **Logging and Notifications**
1. A **scheduled task** logs the unique request count every minute using SLF4J.
2. When an HTTP endpoint is provided, the service sends the unique count via a POST request with a JSON payload.

---

## **4. Extensions**

1. **HTTP POST Requests**:
   - Unique counts are sent to the provided endpoint as JSON payloads instead of using HTTP GET.

2. **Distributed Deduplication**:
   - Redis ensures consistent deduplication across all instances, even when behind a load balancer.

3. **Streaming to Kafka**:
   - Unique counts are sent to a Kafka topic, enabling durable and scalable downstream processing.

---

This approach ensures high performance, scalability, and resilience while fulfilling all requirements.
