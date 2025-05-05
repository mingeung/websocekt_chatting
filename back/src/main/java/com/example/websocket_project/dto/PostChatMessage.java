package com.example.websocket_project.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostChatMessage {
    private String content;

    public PostChatMessage(String content)
    {
        this.content = content;
    }
}
