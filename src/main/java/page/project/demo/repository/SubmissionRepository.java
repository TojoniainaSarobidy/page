package page.project.demo.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import page.project.demo.repository.model.JSubmission;

public interface SubmissionRepository extends JpaRepository<JSubmission, UUID> {}
