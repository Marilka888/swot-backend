package ru.marilka.swotbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.marilka.swotbackend.model.Factor;

@Controller
public class FactorWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void broadcastFactorUpdate(Factor factor) {
        messagingTemplate.convertAndSend("/topic/factor-updates", factor);
    }
}

