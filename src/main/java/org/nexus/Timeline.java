package org.nexus;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "statsTimelineResp")
public class Timeline {
  public Data data;

  public static class Data {
    public String projectId;
    public String groupId;
    public String artifactId;
    public String type;
    public long total;
    @JacksonXmlProperty(localName = "int")
    @JacksonXmlElementWrapper(localName = "timeline")
    public List<Long> timeline = new ArrayList<>();

    public Long lastMonth() {
      return timeline.isEmpty() ? null : timeline.get(timeline.size() - 1);
    }
  }
}
