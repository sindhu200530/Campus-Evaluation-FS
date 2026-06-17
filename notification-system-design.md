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
