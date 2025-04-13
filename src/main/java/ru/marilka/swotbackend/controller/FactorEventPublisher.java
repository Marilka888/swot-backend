package ru.marilka.swotbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FactorEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastFactorUpdate(Long sessionId, String type, String name) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", type); // e.g., "strong"
        payload.put("name", name);
        payload.put("action", "added");

        messagingTemplate.convertAndSend("/topic/factors/" + sessionId, payload);
    }

    public void broadcastFactorDeleted(Long sessionId, String type, String name) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", type);
        payload.put("name", name);
        payload.put("action", "deleted");

        messagingTemplate.convertAndSend("/topic/factors/" + sessionId, payload);
    }
}

