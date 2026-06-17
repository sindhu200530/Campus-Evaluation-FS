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
