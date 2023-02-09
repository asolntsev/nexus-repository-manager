package org.nexus;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpResponse;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.apache.hc.core5.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;
import static org.apache.hc.core5.http.HttpStatus.SC_UNAUTHORIZED;

public class NexusClient {
  private static final Pattern REGEX_SESSION_COOKIE = Pattern.compile("NXSESSIONID=(.+?);.*");
  private final HttpClient client = HttpClientBuilder.create().build();
  private final TimelineParser parser = new TimelineParser();
  
  private final String username;
  private final String password;
  private final String host = "https://oss.sonatype.org";
  private String sessionId = null;

  public NexusClient(String username, String password) {
    this.username = username;
    this.password = password;
  }

  /**
   * Not needed to call directly. 
   * May be useful for checking if username+password is correct.
   */
  public String login() throws IOException {
    String url = host + "/service/local/authentication/login?_dc=" + System.currentTimeMillis();
    HttpGet get = new HttpGet(url);
    get.addHeader("Authorization", authorizationHeader());
    HttpResponse response = client.execute(get);
    int statusCode = response.getCode();
    if (statusCode == SC_UNAUTHORIZED) {
      throw new InvalidCredentials("Login failed (status code: " + statusCode + ")");
    }
    if (statusCode != SC_OK && statusCode != SC_NO_CONTENT) {
      throw new RuntimeException("Error login response: " + statusCode);
    }

    return extractSessionId(response);
  }

  String extractSessionId(HttpResponse response) {
    Header[] headers = response.getHeaders("Set-Cookie");
    return REGEX_SESSION_COOKIE.matcher(headers[0].getValue()).replaceFirst("$1");
  }

  private String authorizationHeader() {
    String secret = username + ':' + password;
    return "Basic " + Base64.getEncoder().encodeToString(secret.getBytes(UTF_8));
  }

  public Timeline downloads(String projectId, String groupId, String artifactId) throws IOException {
    return statistics(projectId, groupId, artifactId, StatisticsType.DOWNLOADS, 6);
  }

  public Timeline uniqueIPs(String projectId, String groupId, String artifactId) throws IOException {
    return statistics(projectId, groupId, artifactId, StatisticsType.UNIQUE_IPS, 12);
  }

  private String getSessionId() throws IOException {
    if (sessionId == null) sessionId = login();
    return sessionId;
  }

  private Timeline statistics(String projectId, String groupId, String artifactId, StatisticsType statisticsType, int months) throws IOException {
    String from = LocalDate.now().minusMonths(months).format(ofPattern("yyyyMM"));
    String url = host + "/service/local/stats/timeline?p=" + projectId +
        "&g=" + groupId + "&a=" + artifactId +
        "&t=" + type(statisticsType) +
        "&from=" + from + "&nom=" + months +
        "&_dc=" + System.currentTimeMillis();
    HttpGet get = new HttpGet(url);
    get.addHeader("Set-Cookie", "NXSESSIONID=" + getSessionId());

    String xml = client.execute(get, new BasicHttpClientResponseHandler());
    try {
      return parser.parseTimelineXml(xml);
    }
    catch (ParserConfigurationException | SAXException invalidXml) {
      throw new IllegalArgumentException("Failed to parse XML response for " + url, invalidXml);
    }
  }

  private String type(StatisticsType statisticsType) {
    switch (statisticsType) {
      case DOWNLOADS:
        return "raw";
      case UNIQUE_IPS:
        return "IP";
    }
    throw new IllegalArgumentException("Unsupported statistics type: " + statisticsType);
  }
}
