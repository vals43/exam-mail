package api.poja.app.endpoint.rest.controller;

import static java.util.UUID.randomUUID;

import api.poja.app.dto.ImageSubmissionResponse;
import api.poja.app.endpoint.event.EventProducer;
import api.poja.app.endpoint.event.model.ImageSubmitted;
import api.poja.app.entity.ImageSubmission;
import api.poja.app.file.bucket.BucketComponent;
import api.poja.app.repository.ImageSubmissionRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class ImageSubmissionController {
  private final BucketComponent bucketComponent;
  private final ImageSubmissionRepository imageSubmissionRepository;
  private final EventProducer<ImageSubmitted> eventProducer;

  @PostMapping(
      value = "/image-submission",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> submitImage(
      @RequestParam @Valid @NotNull @Email String email,
      @RequestParam @Valid @NotNull MultipartFile file)
      throws IOException {
    var id = randomUUID();
    var originalFilename = file.getOriginalFilename();
    var bucketKey = "submissions/" + id + "-" + originalFilename;

    var tempFile = File.createTempFile("upload-", originalFilename);
    try (var os = new FileOutputStream(tempFile)) {
      os.write(file.getBytes());
    }
    bucketComponent.upload(tempFile, bucketKey);
    tempFile.delete();

    var now = Instant.now();
    var submission = new ImageSubmission(id, originalFilename, email, now);
    imageSubmissionRepository.save(submission);

    var event =
        ImageSubmitted.builder()
            .submissionId(id)
            .email(email)
            .s3Key(bucketKey)
            .build();
    eventProducer.accept(List.of(event));

    return ResponseEntity.status(HttpStatus.ACCEPTED).body(id.toString());
  }

  @GetMapping("/image-submission")
  public ResponseEntity<List<ImageSubmissionResponse>> getAllSubmissions() {
    var submissions = imageSubmissionRepository.findAll();
    var dtos =
        submissions.stream()
            .map(
                s ->
                    new ImageSubmissionResponse(
                        s.getId(), s.getNomFichier(), s.getEmail(), s.getCreatedAt()))
            .toList();
    return ResponseEntity.ok(dtos);
  }
}
