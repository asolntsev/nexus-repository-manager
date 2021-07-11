package org.nexus;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

import static java.lang.Thread.currentThread;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

class TimelineTest {
  @Test
  void parsesFromXml() throws IOException {
    URL resource = requireNonNull(currentThread().getContextClassLoader().getResource("timeline-response.xml"));
    String xml = IOUtils.toString(resource, UTF_8);

    Timeline timeline = new XmlMapper().readValue(xml, Timeline.class);
    assertThat(timeline.data.timeline).hasSize(12);
    assertThat(timeline.data.lastMonth()).isEqualTo(232315L);
  }
}