package page.project.demo.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import page.project.demo.model.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({
    MissingServletRequestParameterException.class,
    MultipartException.class,
    InvalidSubmissionException.class
  })
  public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex) {
    return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
  }
}
