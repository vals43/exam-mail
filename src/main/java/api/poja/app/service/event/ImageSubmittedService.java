package api.poja.app.service.event;

import api.poja.app.endpoint.event.model.ImageSubmitted;
import api.poja.app.file.bucket.BucketComponent;
import api.poja.app.mail.Email;
import api.poja.app.mail.Mailer;
import jakarta.mail.internet.InternetAddress;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ImageSubmittedService implements Consumer<ImageSubmitted> {
  private final BucketComponent bucketComponent;
  private final Mailer mailer;

  @Override
  @SneakyThrows
  public void accept(ImageSubmitted event) {
    var presignedUrl = bucketComponent.presign(event.getS3Key(), Duration.ofHours(24));
    var recipient = new InternetAddress(event.getEmail());
    var htmlBody =
        "<h1>Image reçue</h1>"
            + "<p>Votre image a bien été reçue.</p>"
            + "<p><a href=\""
            + presignedUrl
            + "\">Télécharger l'image</a></p>"
            + "<p><img src=\""
            + presignedUrl
            + "\" alt=\"Image\" style=\"max-width:600px\"/></p>";

    mailer.accept(new Email(recipient, List.of(), List.of(), "Confirmation image", htmlBody, List.of()));
  }
}
