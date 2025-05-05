package com.example.websocket_project.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat")
public class ChatMessage {
    @Id
    private String id; //id는 string으로
    private Long chatRoomId;
    private Long senderId;
    private String content;
    private LocalDateTime createdAt;

}
