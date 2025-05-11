package ru.marilka.swotbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.marilka.swotbackend.model.FactorMessage;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/factor.add") // из фронта: /app/factor.add
    public void addFactor(FactorMessage message) {
        messagingTemplate.convertAndSend("/topic/factors/" + message.getSessionId(), message);
    }
}

