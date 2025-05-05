package com.example.websocket_project.repository;

import com.example.websocket_project.domain.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatRepository extends MongoRepository<ChatMessage, String> {

    // 특정 채팅방의 모든 메시지 조회 (createdAt 오름차순 정렬)
    List<ChatMessage> findByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId);

    // 특정 채팅방의 메시지를 30개씩 페이징 조회
    Page<ChatMessage> findByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId, Pageable pageable);

    // 커서 기반 페이징 (createdAt 기준으로 이전 데이터 조회)
    @Query("{ 'chatRoomId': ?0, 'createdAt': { $lt: ?1 } }")
    List<ChatMessage> findByChatRoomIdBefore(Long chatRoomId, LocalDateTime cursor, Pageable pageable);
}