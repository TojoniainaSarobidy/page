package page.project.demo.repository.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "submission")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JSubmission {

  @Id @GeneratedValue @UuidGenerator private UUID id;

  @Column(nullable = false)
  private String email;

  @Column(name = "thumbnail_key")
  private String thumbnailKey;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;
}
