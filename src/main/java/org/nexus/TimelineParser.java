package org.nexus;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

class TimelineParser {
  private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

  Timeline parseTimelineXml(String xml) throws ParserConfigurationException, IOException, SAXException {
    DocumentBuilder builder = factory.newDocumentBuilder();
    try (StringReader in = new StringReader(xml)) {
      El root = El.root(builder.parse(new InputSource(in)));
      El data = root.singleElement("data");
      El timeline = data.singleElement("timeline");
      List<Long> monthNumbers = timeline.children("int", (El::longValue));

      return new Timeline(new Timeline.Data(
        data.singleElement("projectId").text(), 
        data.singleElement("groupId").text(), 
        data.singleElement("artifactId").text(),
        data.singleElement("type").text(),
        data.singleElement("total").longValue(),
        monthNumbers
      ));
    }
  }
}
