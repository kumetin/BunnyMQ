package com.kmt.app.controller;

import com.kmt.app.entity.QueueMessage;
import com.kmt.app.service.SimpleQueueMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class QueueMessageController {
    
    private final SimpleQueueMessageService queueMessageService;
    
    public QueueMessageController(SimpleQueueMessageService queueMessageService) {
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
        } catch (SimpleQueueMessageService.DequeueTimeoutException e) {
            return ResponseEntity.noContent().build();
        }
    }
    
}
