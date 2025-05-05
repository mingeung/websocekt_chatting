package com.example.websocket_project.service;


import com.example.websocket_project.domain.ChatMessage;
import com.example.websocket_project.dto.PostChatMessage;
import com.example.websocket_project.repository.ChatRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ChatService {
    private ChatRepository chatRepository;
    private final SimpMessagingTemplate messagingTemplate;


    public void addMessage(PostChatMessage postChatMessage, Long chatRoomId) {

        ChatMessage chatMessage = new ChatMessage();
        String content = postChatMessage.getContent();
        chatMessage.setContent(content); //private로 정의된 변수를 setter, getter를 이용해서 접근
        //1. 채팅 메시지 저장
        chatRepository.save(chatMessage);

        //2. 다른 사람에게 메시지 전송
        messagingTemplate.convertAndSend("/sub/chat/" + chatRoomId, content);
    }

    /**
     * 특정 채팅방의 모든 메시지를 조회
     */
    public List<ChatMessage> getAllMessages(Long chatRoomId) {
        return chatRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId);
    }

    /**
     * 특정 채팅방의 메시지를 30개씩 페이징 조회
     */
    public Page<ChatMessage> getMessagesWithPagination(Long chatRoomId, int page) {
        PageRequest pageable = PageRequest.of(page, 30);
        return chatRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId, pageable);
    }

    // 커서 기반 페이징 조회 (이전 메시지 조회) => 안쓰는거
    public List<ChatMessage> getMessagesBefore(Long chatRoomId, LocalDateTime cursor) {
        return chatRepository.findByChatRoomIdBefore(chatRoomId, cursor, PageRequest.of(0, 30));
    }
}
