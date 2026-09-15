package page.project.demo.conf;

import org.springframework.test.context.DynamicPropertyRegistry;
import page.project.demo.PojaGenerated;

@PojaGenerated
public class BucketConf {

  void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("aws.s3.bucket", () -> "dummy-bucket");
  }
}
