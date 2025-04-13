package ru.marilka.swotbackend.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/update") // from frontend: /app/update
    @SendTo("/topic/updates") // broadcast to: /topic/updates
    public String broadcastUpdate(String message) {
        return message;
    }
}

