package api.poja.app.dto;

import java.time.Instant;
import java.util.UUID;

public record ImageSubmissionResponse(
    UUID id, String nomFichier, String email, Instant createdAt) {}
