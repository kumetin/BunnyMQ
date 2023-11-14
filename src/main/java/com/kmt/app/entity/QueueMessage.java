package com.kmt.app.entity;

public record QueueMessage(String payload, long timestamp) {
    public String id() {
        return String.valueOf(timestamp);
    }
}
