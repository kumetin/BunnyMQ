package com.kmt.app.controller;

import com.kmt.app.entity.QueueMessage;
import com.kmt.app.service.DequeueTimeoutException;
import com.kmt.app.service.DistributedQueueMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class QueueMessageController {
    
    private final DistributedQueueMessageService queueMessageService;
    
    public QueueMessageController(DistributedQueueMessageService queueMessageService) {
        this.queueMessageService = queueMessageService;
    }
    
    @PostMapping(name = "/{queueName}")
    public ResponseEntity<Void> enqueue(@RequestBody QueueMessage message,
                                        @PathVariable String queueName) {
        queueMessageService.enqueue(queueName, message);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping(name = "/{queueName}")
    public ResponseEntity<QueueMessage> dequeue(
        @PathVariable String queueName,
        @RequestParam(required = false, defaultValue = "10000") long timeout) {
        try {
            Optional<QueueMessage> maybeMessage = queueMessageService.dequeue(queueName, timeout);
            return maybeMessage.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
        } catch (DequeueTimeoutException e) {
            return ResponseEntity.noContent().build();
        }
    }
    
    @DeleteMapping(name = "/{queueName}/{messageId}")
    public ResponseEntity<Void> remove(
        @PathVariable String queueName,
        @PathVariable String messageId) {
        queueMessageService.remove(queueName, messageId);
        return ResponseEntity.ok().build();
    }

}
