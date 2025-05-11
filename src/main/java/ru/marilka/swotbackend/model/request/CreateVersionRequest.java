package ru.marilka.swotbackend.model.request;

import lombok.Data;

@Data
public class CreateVersionRequest {
    private String sessionId;
    private String baseVersionId;
}

