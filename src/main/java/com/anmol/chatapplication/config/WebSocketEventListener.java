package com.anmol.chatapplication.config;

import com.anmol.chatapplication.chat.ChatMessage;
import com.anmol.chatapplication.chat.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j

// to inform users that a user has left the chat
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;


    @EventListener
    public void handleWebSocketDisconnectListener(
            SessionDisconnectEvent event
    ){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = headerAccessor.getSessionAttributes().get("username").toString(); // because its an object so .toString

        if(username!=null){
            log.info("User disconnected:{}", username);
            var chatMessage  = ChatMessage.builder()
                    .type(MessageType.LEAVE)
                    .sender(username)
                    .build();
            messagingTemplate.convertAndSend("/topic/public", chatMessage); // informing other users
        }

    }
}
