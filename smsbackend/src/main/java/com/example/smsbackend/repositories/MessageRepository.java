package com.example.smsbackend.repositories;

import com.example.smsbackend.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiverId(Integer receiverId);
    List<Message> findBySenderId(Integer senderId);
    List<Message> findBySenderIdAndReceiverId(Integer senderId, Integer receiverId);
    List<Message> findBySenderIdAndReceiverIdOrSenderIdAndReceiverId(Integer senderId1, Integer receiverId1, Integer senderId2, Integer receiverId2);
}
