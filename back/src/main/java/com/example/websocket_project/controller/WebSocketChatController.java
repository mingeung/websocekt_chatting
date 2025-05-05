package com.example.websocket_project.controller;

import com.example.websocket_project.domain.ChatMessage;
import com.example.websocket_project.dto.PostChatMessage;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
public class WebSocketChatController {
    com.example.websocket_project.service.ChatService chatService;
    private final com.example.websocket_project.repository.ChatRepository chatRepository;

    @MessageMapping("/chatMessage/{chatRoomId}")
    public ResponseEntity<?> postChatMessage(@DestinationVariable("chatRoomId") Long chatRoomId,
                                             @Payload PostChatMessage postChatMessage){

        chatService.addMessage(postChatMessage,chatRoomId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 특정 채팅방의 모든 메시지 조회 API
     */
    @GetMapping("/{chatRoomId}/messages")
    public List<ChatMessage> getAllMessages(@PathVariable Long chatRoomId) {
        return chatService.getAllMessages(chatRoomId);
    }

    /**
     * 특정 채팅방의 메시지를 30개씩 페이징 조회 API
     */
    @GetMapping("/{chatRoomId}/messages/paged")
    public Page<ChatMessage> getMessagesWithPagination(
            @PathVariable Long chatRoomId,
            @RequestParam(defaultValue = "0") int page) {
        return chatService.getMessagesWithPagination(chatRoomId, page);
    }

    // 커서 기반 이전 메시지 조회 => 안쓸거임.
    @GetMapping("/{chatRoomId}/before")
    public List<ChatMessage> getMessagesBefore(
            @PathVariable Long chatRoomId,
            @RequestParam LocalDateTime cursor
    ) {
        return chatService.getMessagesBefore(chatRoomId, cursor);
    }

    /*
     * 채팅 조회 더미데이터 생성 1만건
     * */
    @PostMapping("/generate-dummy")
    public String generateDummyData() {
        List<ChatMessage> messages = new ArrayList<>();
        for (int i = 1; i <= 10000; i++) {
            messages.add(new ChatMessage(
                    null,
                    1L,
                    (long) (Math.random() * 100) + 1,
                    "Test Message " + i,
                    LocalDateTime.now().minusSeconds(i)
            ));
        }
        chatRepository.saveAll(messages);
        return "✅ 10,000건의 더미 데이터가 삽입되었습니다.";
    }
}
