package page.project.demo.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class Submission {
  private UUID id;
  private String email;
  private String thumbnailKey;
  private OffsetDateTime createdAt;
}
