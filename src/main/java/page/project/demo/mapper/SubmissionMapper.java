package page.project.demo.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import page.project.demo.model.Submission;
import page.project.demo.repository.model.JSubmission;

@Component
public class SubmissionMapper {
  public List<Submission> toDto(List<JSubmission> submissions) {
    return submissions.stream().map(this::toDto).toList();
  }

  public Submission toDto(JSubmission entity) {
    return Submission.builder()
        .id(entity.getId())
        .email(entity.getEmail())
        .thumbnailKey(entity.getThumbnailKey())
        .createdAt(entity.getCreatedAt())
        .build();
  }
}
