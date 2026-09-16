package page.project.demo.conf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import page.project.demo.endpoint.event.EventProducer;
import page.project.demo.endpoint.event.model.ThumbnailGenerationRequested;
import page.project.demo.exception.InvalidSubmissionException;
import page.project.demo.file.bucket.BucketComponent;
import page.project.demo.mapper.SubmissionMapper;
import page.project.demo.model.Submission;
import page.project.demo.repository.SubmissionRepository;
import page.project.demo.repository.model.JSubmission;
import page.project.demo.service.SubmissionService;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

  @Mock private SubmissionRepository submissionRepository;
  @Mock private SubmissionMapper submissionMapper;
  @Mock private BucketComponent bucketComponent;
  @Mock private EventProducer<ThumbnailGenerationRequested> eventProducer;
  @InjectMocks private SubmissionService submissionService;

  @Test
  void createSubmission_persistsWithNullThumbnailAndPublishesEvent() {
    var file = new MockMultipartFile("file", "photo.png", "image/png", "fake".getBytes());
    when(submissionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(submissionMapper.toDto(any(JSubmission.class)))
        .thenAnswer(
            inv -> {
              JSubmission entity = inv.getArgument(0);
              return Submission.builder()
                  .id(entity.getId())
                  .email(entity.getEmail())
                  .thumbnailKey(entity.getThumbnailKey())
                  .createdAt(entity.getCreatedAt())
                  .build();
            });

    var result = submissionService.createSubmission(file, "test@example.com");

    assertThat(result.getThumbnailKey()).isNull();
    assertThat(result.getEmail()).isEqualTo("test@example.com");
    verify(bucketComponent).upload(any(File.class), startsWith("raw/"));
    verify(eventProducer).accept(anyList());
  }

  @Test
  void createSubmission_rejectsInvalidContentType() {
    var file = new MockMultipartFile("file", "doc.pdf", "application/pdf", "x".getBytes());

    assertThatThrownBy(() -> submissionService.createSubmission(file, "test@example.com"))
        .isInstanceOf(InvalidSubmissionException.class);
  }

  @Test
  void createSubmission_rejectsInvalidEmail() {
    var file = new MockMultipartFile("file", "photo.png", "image/png", "fake".getBytes());

    assertThatThrownBy(() -> submissionService.createSubmission(file, "not-an-email"))
        .isInstanceOf(InvalidSubmissionException.class);
  }
}
