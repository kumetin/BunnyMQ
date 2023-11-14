package com.kmt.app.service;

import com.kmt.app.entity.QueueMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Service
public class DistributedQueueMessageService {
    
    @Value("${server.port}") // Assuming each node runs on a different port
    private int nodePort;
    
    private final Map<String, BlockingQueue<QueueMessage>> queues = new ConcurrentHashMap<>();
    
    // Simulated list of known nodes in the network
    private final Map<Integer, String> knownNodes = new ConcurrentHashMap<>();
    
    public void registerNode(int port, String nodeId) {
        knownNodes.put(port, nodeId);
    }
    
    public void enqueue(String queueName, QueueMessage message) {
        queues.computeIfAbsent(queueName, k -> new LinkedBlockingQueue<>()).add(message);
        broadcastUpdate(queueName, message);
    }
    
    public Optional<QueueMessage> dequeue(String queueName, long timeout) throws SimpleQueueMessageService.DequeueTimeoutException {
        try {
            return Optional.ofNullable(
                queues.computeIfAbsent(queueName, k -> new LinkedBlockingQueue<>())
                    .poll(timeout, TimeUnit.MILLISECONDS)
            );
        } catch (InterruptedException e) {
            throw new SimpleQueueMessageService.DequeueTimeoutException();
        }
    }
    
    private void broadcastUpdate(String queueName, QueueMessage message) {
        // Simulated broadcast to all known nodes to keep queues in sync
        for (Map.Entry<Integer, String> entry : knownNodes.entrySet()) {
            int port = entry.getKey();
            if (port != nodePort) {
                // Simulated message sending to other nodes
                // You can use a more sophisticated mechanism like REST or messaging queues for real-world scenarios
                System.out.println("Sending update to node at port " + port + ": " + message.payload());
            }
        }
    }
}
