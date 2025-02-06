package com.example.smsbackend.controllers;

import com.example.smsbackend.dtos.MessageDto;
import com.example.smsbackend.entities.Message;
import com.example.smsbackend.services.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(@RequestBody MessageDto messageDto) {
        Message message = messageService.sendMessage(messageDto);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/user/{receiverId}")
    public ResponseEntity<List<Message>> getMessagesForReceiver(@PathVariable Integer receiverId) {
        List<Message> messages = messageService.getMessagesForReceiver(receiverId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/conversation")
    public ResponseEntity<List<Message>> getMessagesBetweenUsers(
            @RequestParam Integer senderId,
            @RequestParam Integer receiverId) {
        List<Message> messages = messageService.getMessagesBetweenUsers(senderId, receiverId);
        return ResponseEntity.ok(messages);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<String> deleteMessage(
            @PathVariable Long messageId,
            @RequestParam Integer userId) {
        messageService.deleteMessage(messageId, userId);
        return ResponseEntity.ok("Message deleted successfully");
    }
}
