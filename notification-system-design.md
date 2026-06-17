# Notification System Design
# Stage 1

## Notification System REST API Design

### Objective

Design REST APIs and JSON contracts for a notification platform that allows front-end applications to display notifications to users in real time.

---

## Core Actions

1. Send a notification to a user.
2. Retrieve notifications for a user.
3. Mark a notification as read.
4. Mark all notifications as read.
5. Delete a notification.
6. Subscribe to real-time notifications.

---

## API Endpoints

### 1. Create Notification

**Endpoint**

```
POST /api/v1/notifications
```

**Request Body**

```json
{
  "userId": "12345",
  "title": "Payment Successful",
  "message": "Your payment of ₹500 has been processed successfully.",
  "type": "PAYMENT",
  "priority": "HIGH"
}
```

**Response**

```json
{
  "notificationId": "n001",
  "status": "CREATED",
  "timestamp": "2026-06-17T10:00:00Z"
}
```

---

### 2. Get User Notifications

**Endpoint**

```
GET /api/v1/users/{userId}/notifications
```

**Response**

```json
{
  "notifications": [
    {
      "notificationId": "n001",
      "title": "Payment Successful",
      "message": "Your payment of ₹500 has been processed.",
      "type": "PAYMENT",
      "read": false,
      "createdAt": "2026-06-17T10:00:00Z"
    }
  ]
}
```

---

### 3. Mark Notification as Read

**Endpoint**

```
PATCH /api/v1/notifications/{notificationId}/read
```

**Response**

```json
{
  "notificationId": "n001",
  "status": "READ"
}
```

---

### 4. Mark All Notifications as Read

**Endpoint**

```
PATCH /api/v1/users/{userId}/notifications/read-all
```

**Response**

```json
{
  "userId": "12345",
  "status": "ALL_NOTIFICATIONS_MARKED_AS_READ"
}
```

---

### 5. Delete Notification

**Endpoint**

```
DELETE /api/v1/notifications/{notificationId}
```

**Response**

```json
{
  "notificationId": "n001",
  "status": "DELETED"
}
```

---

## Notification JSON Schema

```json
{
  "notificationId": "string",
  "userId": "string",
  "title": "string",
  "message": "string",
  "type": "INFO | ALERT | PAYMENT | PROMOTION",
  "priority": "LOW | MEDIUM | HIGH",
  "read": "boolean",
  "createdAt": "ISO-8601 timestamp"
}
```

---

## Real-Time Notification Mechanism

Use **WebSocket** technology to push notifications instantly to connected clients.

### Subscription Endpoint

```
ws://localhost:8080/ws/notifications
```

### Example Event Payload

```json
{
  "event": "NEW_NOTIFICATION",
  "data": {
    "notificationId": "n001",
    "title": "Payment Successful",
    "message": "Your payment has been processed.",
    "type": "PAYMENT"
  }
}
```

---

## Design Considerations

* Use versioned APIs (`/api/v1`).
* Follow REST naming conventions.
* Use meaningful HTTP methods (GET, POST, PATCH, DELETE).
* Keep JSON structures consistent.
* Support scalability through WebSocket-based real-time delivery.
* Ensure extensibility for future notification types.



# Stage 2

## Persistent Storage Choice

I would recommend using a **Relational Database Management System (RDBMS)** such as **PostgreSQL** for storing notifications.

### Reasons for Choosing PostgreSQL

* Supports ACID properties, ensuring reliable data storage.
* Efficient querying capabilities for filtering notifications.
* Supports indexing for improved read performance.
* Scales vertically and supports partitioning for larger datasets.
* Well-suited for structured notification data.

---

## Database Schema

### notifications Table

| Column Name     | Data Type    | Constraints               |
| --------------- | ------------ | ------------------------- |
| notification_id | UUID         | PRIMARY KEY               |
| user_id         | VARCHAR(50)  | NOT NULL                  |
| title           | VARCHAR(255) | NOT NULL                  |
| message         | TEXT         | NOT NULL                  |
| type            | VARCHAR(50)  | NOT NULL                  |
| priority        | VARCHAR(20)  | NOT NULL                  |
| is_read         | BOOLEAN      | DEFAULT FALSE             |
| created_at      | TIMESTAMP    | DEFAULT CURRENT_TIMESTAMP |

---

## SQL Schema Definition

```sql
CREATE TABLE notifications (
    notification_id UUID PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## Potential Problems with Increasing Data Volume

### 1. Slower Query Performance

As the number of notifications grows, retrieving notifications may become slower.

**Solution:**

* Create indexes on frequently queried columns.

```sql
CREATE INDEX idx_user_id ON notifications(user_id);
CREATE INDEX idx_created_at ON notifications(created_at);
```

---

### 2. Large Table Size

Millions of records can increase storage requirements.

**Solution:**

* Implement table partitioning based on date.
* Archive older notifications periodically.

---

### 3. High Read Traffic

Many users fetching notifications simultaneously can overload the database.

**Solution:**

* Use caching mechanisms such as Redis.
* Introduce read replicas.

---

### 4. Scalability Challenges

Single database instances may become bottlenecks.

**Solution:**

* Use horizontal scaling strategies.
* Employ database sharding when necessary.

---

## SQL Queries Based on Stage 1 APIs

### Create Notification

```sql
INSERT INTO notifications (
    notification_id,
    user_id,
    title,
    message,
    type,
    priority
)
VALUES (
    gen_random_uuid(),
    '12345',
    'Payment Successful',
    'Your payment of ₹500 has been processed successfully.',
    'PAYMENT',
    'HIGH'
);
```

---

### Retrieve Notifications for a User

```sql
SELECT *
FROM notifications
WHERE user_id = '12345'
ORDER BY created_at DESC;
```

---

### Mark a Notification as Read

```sql
UPDATE notifications
SET is_read = TRUE
WHERE notification_id = 'notification-id';
```

---

### Mark All Notifications as Read

```sql
UPDATE notifications
SET is_read = TRUE
WHERE user_id = '12345';
```

---

### Delete a Notification

```sql
DELETE FROM notifications
WHERE notification_id = 'notification-id';
```

---

## Conclusion

PostgreSQL provides reliability, consistency, and efficient querying capabilities for notification systems. With proper indexing, partitioning, caching, and scaling strategies, the system can handle increasing data volumes effectively.




## Stage 3

### 1. Is the query accurate?

The given query is not fully accurate.

SELECT *
FROM notifications
WHERE studentID = 1042 AND isRead = false
ORDER BY createdAt ASC;

### 2. Why is this query slow?

- Large dataset (5 million rows)
- No proper indexing
- Full table scan + sorting

### 3. Improved approach

CREATE INDEX idx_student_unread_created
ON notifications (studentID, isRead, createdAt);

### 4. Indexing every column is not good

- Slows INSERT/UPDATE/DELETE
- Wastes storage
- Not all indexes are used

### 5. Placement notifications (last 7 days)

SELECT studentID, *
FROM notifications
WHERE notificationType = 'Placement'
AND createdAt >= NOW() - INTERVAL 7 DAY;

CREATE INDEX idx_type_created
ON notifications (notificationType, createdAt);



## Stage 4

### Problem
Notifications are being fetched from the database on every page load for every student, causing high DB load and poor performance.

---

### 1. Suggested Solution: Caching Layer (Redis)

Use a caching system like Redis to store frequently accessed unread notifications.

#### Approach:
- When notifications are fetched first time → store in cache
- On next request → serve from cache instead of DB
- Update cache when a new notification arrives or when user reads one

#### Example:
- Key: `student:{studentId}:notifications`
- Value: list of unread notifications

---

### ✔ Benefits:
- Reduces database load significantly
- Very fast response time (in-memory access)
- Improves scalability for large traffic

### ❌ Tradeoffs:
- Cache invalidation complexity
- Risk of stale data if not updated properly
- Extra infrastructure cost (Redis server)

---

### 2. Alternative Solution: Pagination + Lazy Loading

Instead of loading all notifications at once:
- Load only first 20–50 notifications
- Fetch more using “Load More” or infinite scroll

---

### ✔ Benefits:
- Reduces initial load time
- Less memory usage on frontend and backend
- Simple to implement without extra tools

### ❌ Tradeoffs:
- Still hits DB frequently
- Not ideal for high traffic systems
- User may experience slight delay while loading more data

---

### 3. Advanced Solution: Read-Through Cache + Event Updates

Combine DB + cache + event-driven updates:
- Write notification → DB + cache update
- Read → served from cache
- Background sync ensures consistency

---

### ✔ Benefits:
- Best performance at scale
- Near real-time updates
- Reduces DB dependency

### ❌ Tradeoffs:
- Complex architecture
- Harder debugging
- Requires message queue (Kafka/RabbitMQ)

---

### Final Recommendation
Use **Redis caching + pagination together**:
- Cache for unread notifications
- Pagination for large history lists

This provides a balance between performance, cost, and system simplicity.



## Stage 5

### Problem
HR clicks "Notify All" and 50,000 students must receive both:
- Email notification
- In-app notification

The current pseudocode:
is inefficient because it processes notifications sequentially.

---

### 1. Issues in Current Approach
- Sequential processing → very slow for 50,000 users
- High API latency
- Risk of system timeout
- No fault tolerance (one failure can affect flow)

---

### 2. Improved Solution: Asynchronous Batch Processing

Instead of processing in a loop synchronously:

#### Approach:
- Push notification jobs into a queue (Kafka / RabbitMQ / SQS)
- Worker services process notifications in parallel
- Send email + in-app notification asynchronously

---

### 3. Optimized Pseudocode
for batch in split(student_ids, batch_size):
    push_to_queue(batch, message)
    
Worker:

---

### 4. Benefits
- High scalability (handles 50K+ easily)
- Faster processing using parallel workers
- Fault tolerance via retry mechanisms
- Non-blocking API response

---

### 5. Tradeoffs
- More complex architecture
- Requires message queue setup
- Eventual consistency (slight delay in delivery)
- Monitoring and retry handling needed

---

### 6. Final Recommendation
Use:
- Message Queue (Kafka/RabbitMQ)
- Worker-based processing
- Batch size control (1000–5000 users per batch)

This ensures reliable and scalable bulk notification delivery.