package page.project.demo.conf;

import org.springframework.test.context.DynamicPropertyRegistry;
import page.project.demo.PojaGenerated;

@PojaGenerated
public class EmailConf {

  void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("aws.ses.source", () -> "dummy-ses-source");
  }
}
