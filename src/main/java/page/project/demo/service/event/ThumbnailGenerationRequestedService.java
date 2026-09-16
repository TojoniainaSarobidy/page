package page.project.demo.service.event;

import jakarta.mail.internet.InternetAddress;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import page.project.demo.endpoint.event.model.ThumbnailGenerationRequested;
import page.project.demo.file.bucket.BucketComponent;
import page.project.demo.mail.Email;
import page.project.demo.mail.Mailer;
import page.project.demo.repository.SubmissionRepository;

@Service
@AllArgsConstructor
public class ThumbnailGenerationRequestedService implements Consumer<ThumbnailGenerationRequested> {
  private static final int THUMBNAIL_SIZE = 256;
  private final BucketComponent bucketComponent;
  private final SubmissionRepository submissionRepository;
  private final Mailer mailer;

  @SneakyThrows
  @Override
  public void accept(ThumbnailGenerationRequested event) {
    File originalFile = bucketComponent.download(event.getOriginalKey());
    File resizedFile = resizeTo256(originalFile, event.getExtension());
    var thumbnailKey = "thumbnails/" + event.getSubmissionId() + "." + event.getExtension();
    bucketComponent.upload(resizedFile, thumbnailKey);
    var submission =
        submissionRepository
            .findById(event.getSubmissionId())
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "Soumission introuvable : " + event.getSubmissionId()));
    submission.setThumbnailKey(thumbnailKey);
    submissionRepository.save(submission);
    var downloadUri = bucketComponent.presign(thumbnailKey, Duration.ofDays(7));
    mailer.accept(
        new Email(
            new InternetAddress(event.getEmail()),
            List.of(),
            List.of(),
            "Votre vignette est prête",
            "Bonjour,\n\nVotre image a été traitée avec succès.\n"
                + "Vous pouvez télécharger votre vignette ici : "
                + downloadUri
                + "\n\n(Lien valable 7 jours.)",
            List.of()));
  }

  private File resizeTo256(File original, String extension) throws IOException {
    var sourceImage = ImageIO.read(original);
    var resizedImage =
        new BufferedImage(THUMBNAIL_SIZE, THUMBNAIL_SIZE, BufferedImage.TYPE_INT_RGB);

    var graphics = resizedImage.createGraphics();
    graphics.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    graphics.drawImage(sourceImage, 0, 0, THUMBNAIL_SIZE, THUMBNAIL_SIZE, null);
    graphics.dispose();
    var format = extension.equalsIgnoreCase("png") ? "png" : "jpg";
    var outputFile = File.createTempFile("thumbnail-", "." + format);
    outputFile.deleteOnExit();
    ImageIO.write(resizedImage, format, outputFile);
    return outputFile;
  }
}
