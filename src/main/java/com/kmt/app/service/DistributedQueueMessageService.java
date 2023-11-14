package com.kmt.app.service;

import com.kmt.app.entity.QueueMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Service
public class DistributedQueueMessageService {
    
    @Value("${server.port}")
    private int nodePort;
    
    private final Map<String, BlockingQueueWithRandomAccessRemove> queues = new ConcurrentHashMap<>();
    private final Map<String, RemoteQueueNode> nodes = new ConcurrentHashMap<>();
    
    public DistributedQueueMessageService() {
        findNodes().forEach(n -> registerNode(n.getNodeId(), n));
    }
    
    private Set<RemoteQueueNode> findNodes() {
        // TODO implement brother nodes discovery
        return Set.of();
    }
    
    public void registerNode(String nodeId, RemoteQueueNode distributedQueueNode) {
        nodes.put(nodeId, distributedQueueNode);
    }
    
    public void enqueue(String queueName, QueueMessage message) {
        queues.computeIfAbsent(queueName, k -> new BlockingQueueWithRandomAccessRemove()).add(message);
        nodes.values().forEach(n -> n.enqueue(queueName, message));
    }
    
    public Optional<QueueMessage> dequeue(String queueName, long timeout) throws DequeueTimeoutException {
        Optional<QueueMessage> message = null;
        try {
            message = Optional.ofNullable(
                queues.computeIfAbsent(queueName, k -> new BlockingQueueWithRandomAccessRemove())
                    .poll(timeout, TimeUnit.MILLISECONDS)
            );
        } catch (InterruptedException e) {
            throw new DequeueTimeoutException();
        }
        
        message.ifPresent(m -> {
            for (RemoteQueueNode n : nodes.values()) {
                n.remove(queueName, m.id());
            }
        });
        
        return message;
    }
    
    public void remove(String queueName, String messageId) {
        Optional.ofNullable(queues.get(queueName)).ifPresent(q -> q.removeById(messageId));
    }
    
    public static class RemoteQueueNode {
        
        private final String nodeId;
        private final int port;
        
        public String getNodeId() {
            return nodeId;
        }

        public RemoteQueueNode(String nodeId, int port) {
                this.nodeId = nodeId;
                this.port = port;
        }
        
        public void enqueue(String queueName, QueueMessage message) {
            // TODO implement http request POST localhost:<port>/api/<queueName>
        }
        
        public void remove(String queueName, String messageId) {
            // TODO implement http request DELETE localhost:<port>/api/<queueName>/<messageId>
        }
    }

    public static class BlockingQueueWithRandomAccessRemove {
        private final BlockingQueue<QueueMessage> queue = new LinkedBlockingQueue<>();
        private final Map<String, QueueMessage> messageMap = new ConcurrentHashMap<>();
        
        public void add(QueueMessage message) {
            queue.add(message);
            messageMap.put(message.id(), message);
        }
        
        public QueueMessage poll(long timeout, TimeUnit timeUnit) throws InterruptedException {
            return queue.poll(timeout, timeUnit);
        }
        
        public void removeById(String messageId) {
            QueueMessage removedMessage = messageMap.remove(messageId);
            if (removedMessage != null) {
                queue.remove(removedMessage);
            }
        }
    }
}
