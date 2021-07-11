package org.nexus;

import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.message.BasicHeader;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NexusClientTest {
  private final String header = "NXSESSIONID=2aba565d-201b-44bf-a8fb-77d8b9fc3959; Path=/; HttpOnly; SameSite=lax; Secure";
  private final NexusClient client = new NexusClient("username-1", "password-1");

  @Test
  void extractSessionIdFromCookieHeader() {
    HttpResponse response = mock(HttpResponse.class);
    when(response.getHeaders("Set-Cookie")).thenReturn(new Header[]{new BasicHeader("Set-Cookie", header)});
    assertThat(client.extractSessionId(response)).isEqualTo("2aba565d-201b-44bf-a8fb-77d8b9fc3959");
  }
}