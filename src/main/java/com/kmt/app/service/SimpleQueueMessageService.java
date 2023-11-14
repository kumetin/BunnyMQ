package com.kmt.app.service;

import com.kmt.app.entity.QueueMessage;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


@Service
public class SimpleQueueMessageService {
    
    private final Map<String, BlockingQueue<QueueMessage>> queues = new ConcurrentHashMap<>();
    
    public void enqueue(String queueName, QueueMessage message) {
        queues.computeIfAbsent(queueName, k -> new LinkedBlockingQueue<>()).add(message);
    }
    
    public Optional<QueueMessage> dequeue(String queueName, long timeout) throws DequeueTimeoutException {
        try {
            return Optional.ofNullable(
                queues.computeIfAbsent(queueName, k -> new LinkedBlockingQueue<>())
                    .poll(timeout, TimeUnit.MILLISECONDS)
            );
        } catch (InterruptedException e) {
            throw new DequeueTimeoutException();
        }
    }
}
