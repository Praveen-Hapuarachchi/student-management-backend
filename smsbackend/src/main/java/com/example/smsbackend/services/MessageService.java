package com.example.smsbackend.services;

import com.example.smsbackend.dtos.MessageDto;
import com.example.smsbackend.entities.Message;
import com.example.smsbackend.entities.User;
import com.example.smsbackend.repositories.MessageRepository;
import com.example.smsbackend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public Message sendMessage(MessageDto messageDto) {
        User sender = userRepository.findById(messageDto.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository.findById(messageDto.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(messageDto.getContent());

        return messageRepository.save(message);
    }

    public List<Message> getMessagesForReceiver(Integer receiverId) {
        return messageRepository.findByReceiverId(receiverId);
    }

    public List<Message> getMessagesBetweenUsers(Integer senderId, Integer receiverId) {
        return messageRepository.findBySenderIdAndReceiverIdOrSenderIdAndReceiverId(
                senderId, receiverId, receiverId, senderId);
    }


    public void deleteMessage(Long messageId, Integer userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));

        // Ensure the user deleting the message is either the sender or the receiver
        if (!message.getSender().getId().equals(userId) && !message.getReceiver().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not authorized to delete this message");
        }

        messageRepository.deleteById(messageId);
    }
}
