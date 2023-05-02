package com.example.service1.entities;

import jakarta.persistence.*;

import java.security.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class notifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "orderId")
    private int orderId;

    @Column(name = "username")
    private String username;

    @Column(name = "message")
    private String message;

    @Column(name = "notificationTime")
    private String notificationTime;

    public notifications() {
    }
    public notifications( String username, int orderId, String message, String time) {
        this.orderId = orderId;
        this.username = username;
        this.message = message;
        this.notificationTime = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(String notificationTime) {
        this.notificationTime = notificationTime;
    }
}
