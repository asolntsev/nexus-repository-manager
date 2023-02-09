package org.nexus;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static java.lang.Thread.currentThread;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

public class TimelineParserTest {
  private final TimelineParser parser = new TimelineParser();

  @Test
  void parsesDownloadsResponse() throws IOException, ParserConfigurationException, SAXException {
    String file = "downloads-response.xml";
    String xml = read(file);
    Timeline downloads = parser.parseTimelineXml(xml);
    assertThat(downloads.data.projectId).isEqualTo("186c4c63cde8c");
    assertThat(downloads.data.groupId).isEqualTo("com.codeborne");
    assertThat(downloads.data.artifactId).isEqualTo("selenide");
    assertThat(downloads.data.type).isEqualTo("RAW");
    assertThat(downloads.data.total).isEqualTo(2181893L);
    assertThat(downloads.data.timeline).hasSize(6);
    assertThat(downloads.data.timeline.get(0).intValue()).isEqualTo(361952);
    assertThat(downloads.data.timeline.get(4).intValue()).isEqualTo(469551);
    assertThat(downloads.data.timeline.get(5).intValue()).isEqualTo(0);
  }

  @Test
  void parsesUniqueIpsResponse() throws IOException, ParserConfigurationException, SAXException {
    String file = "unique-ips-response.xml";
    String xml = read(file);
    Timeline downloads = parser.parseTimelineXml(xml);
    assertThat(downloads.data.projectId).isEqualTo("186c4c63cde8c");
    assertThat(downloads.data.groupId).isEqualTo("com.codeborne");
    assertThat(downloads.data.artifactId).isEqualTo("selenide");
    assertThat(downloads.data.type).isEqualTo("IP");
    assertThat(downloads.data.total).isEqualTo(641072L);
    assertThat(downloads.data.timeline).hasSize(12);
    assertThat(downloads.data.timeline.get(0).intValue()).isEqualTo(46524);
    assertThat(downloads.data.timeline.get(10).intValue()).isEqualTo(70914);
    assertThat(downloads.data.timeline.get(11).intValue()).isEqualTo(0);
  }

  private static String read(String file) throws IOException {
    return IOUtils.toString(requireNonNull(
      currentThread().getContextClassLoader().getResource(file),
      () -> "File not found in classpath: " + file
    ), UTF_8);
  }
}
