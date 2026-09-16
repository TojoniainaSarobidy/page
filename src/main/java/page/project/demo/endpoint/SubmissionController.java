package page.project.demo.endpoint;

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
import page.project.demo.model.Submission;
import page.project.demo.service.SubmissionService;

@RestController
@AllArgsConstructor
public class SubmissionController {
  private final SubmissionService service;

  @PostMapping(path = "/submissions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Submission> createSubmission(
      @RequestParam("file") MultipartFile file, @RequestParam("email") String email) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.createSubmission(file, email));
  }

  @GetMapping("/submissions")
  public List<Submission> listSubmissions() {
    return service.listSubmissions();
  }
}
