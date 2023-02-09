package org.nexus;

import java.util.List;

public class Timeline {
  public final Data data;

  public Timeline(Data data) {
    this.data = data;
  }

  public static class Data {
    public final String projectId;
    public final String groupId;
    public final String artifactId;
    public final String type;
    public final long total;
    public final List<Long> timeline;

    public Data(String projectId, String groupId, String artifactId, String type, long total, List<Long> timeline) {
      this.projectId = projectId;
      this.groupId = groupId;
      this.artifactId = artifactId;
      this.type = type;
      this.total = total;
      this.timeline = timeline;
    }

    public Long previousMonth() {
      return timeline.size() < 2? null : timeline.get(timeline.size() - 2);
    }

    public Long lastMonth() {
      return timeline.isEmpty() ? null : timeline.get(timeline.size() - 1);
    }
  }
}
