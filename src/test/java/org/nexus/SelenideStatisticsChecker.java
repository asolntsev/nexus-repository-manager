package org.nexus;

import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Properties;

public class SelenideStatisticsChecker {
  private static final String PROJECT_SELENIDE = "186c4c63cde8c";
  private static final String GROUP_ID = "com.codeborne";
  private static final String ARTIFACT_ID = "selenide";

  public static void main(String[] args) throws IOException {
    new SelenideStatisticsChecker().check();
  }

  private void check() throws IOException {
    String username = loadGradleProperties().getProperty("sonatypeUsername");
    String password = loadGradleProperties().getProperty("sonatypePassword");
    NexusClient client = new NexusClient(username, password);

    Timeline downloads = client.downloads(PROJECT_SELENIDE, GROUP_ID, ARTIFACT_ID);
    Timeline uniqueIPs = client.uniqueIPs(PROJECT_SELENIDE, GROUP_ID, ARTIFACT_ID);
    System.out.println("Downloads last month: " + downloads.data.lastMonth() + " (prev. " + downloads.data.previousMonth() + ")");
    System.out.println("Unique IPs last month: " + uniqueIPs.data.lastMonth() + " (prev. " + uniqueIPs.data.previousMonth() + ")");
  }

  private Properties loadGradleProperties() {
    Properties properties = new Properties();
    try (FileReader in = new FileReader(System.getProperty("user.home") + "/.gradle/gradle.properties")) {
      properties.load(in);
    }
    catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return properties;
  }
}
