package page.project.demo.service;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import java.io.File;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import page.project.demo.endpoint.event.EventProducer;
import page.project.demo.endpoint.event.model.ThumbnailGenerationRequested;
import page.project.demo.exception.InvalidSubmissionException;
import page.project.demo.file.bucket.BucketComponent;
import page.project.demo.mapper.SubmissionMapper;
import page.project.demo.model.Submission;
import page.project.demo.repository.SubmissionRepository;
import page.project.demo.repository.model.JSubmission;

@Service
@AllArgsConstructor
@Slf4j
public class SubmissionService {
  private static final Set<String> ALLOWED_CONTENT_TYPES =
      Set.of("image/png", "image/jpeg", "image/jpg");
  private final SubmissionRepository submissionRepository;
  private final SubmissionMapper mapper;
  private final BucketComponent bucketComponent;
  private final EventProducer<ThumbnailGenerationRequested> eventProducer;

  @SneakyThrows
  public Submission createSubmission(MultipartFile file, String email) {
    validate(file, email);

    var id = UUID.randomUUID();
    var createdAt = OffsetDateTime.now(ZoneOffset.UTC);
    var extension = extensionOf(file.getContentType());
    var rawKey = "raw/" + id + "." + extension;
    var tempFile = File.createTempFile("submission-" + id, "." + extension);
    tempFile.deleteOnExit();
    file.transferTo(tempFile);
    bucketComponent.upload(tempFile, rawKey);

    var entity =
        submissionRepository.save(
            JSubmission.builder()
                .id(id)
                .email(email)
                .thumbnailKey(null)
                .createdAt(createdAt)
                .build());

    eventProducer.accept(
        List.of(
            ThumbnailGenerationRequested.builder()
                .submissionId(id)
                .email(email)
                .originalKey(rawKey)
                .extension(extension)
                .build()));

    log.info("Submission {} créée, génération de vignette demandée", id);
    return mapper.toDto(entity);
  }

  public List<Submission> listSubmissions() {
    return mapper.toDto(submissionRepository.findAll());
  }

  private void validate(MultipartFile file, String email) {
    if (file == null || file.isEmpty()) {
      throw new InvalidSubmissionException("Le fichier est obligatoire.");
    }
    var contentType = file.getContentType();
    if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
      throw new InvalidSubmissionException("Le fichier doit être une image PNG ou JPEG.");
    }
    try {
      new InternetAddress(email).validate();
    } catch (AddressException e) {
      throw new InvalidSubmissionException("L'adresse email fournie est invalide.");
    }
  }

  private String extensionOf(String contentType) {
    return contentType.equalsIgnoreCase("image/png") ? "png" : "jpg";
  }
}
