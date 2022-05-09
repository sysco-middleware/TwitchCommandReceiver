package no.sysco.cip.controller;

import lombok.val;
import no.sysco.cip.RoutingApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.http.*;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
//@RunWith(SpringRunner.class) - Deprecated (used in JUnit4)
@ExtendWith(SpringExtension.class) //To add backward compatibility for JUnit4 type tests
@SpringBootTest(
  classes = {RoutingApplication.class},
  webEnvironment=WebEnvironment.DEFINED_PORT
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS) //Clears the context cache after each test method
class CurrentTimeControllerTest {

  /*@SpringBootApplication
  @ComponentScan(basePackages = "no.sysco.cip")
  class Config {

  }*/

  @Autowired
  private TestRestTemplate restTemplate;

  @LocalServerPort
  int port;

  @Test
  void getCurrentDateTime() {
    val url = "http://localhost:" + port + "/currentTime";

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    headers.set("X-COM-PERSIST", "NO");
    headers.set("X-COM-LOCATION", "USA");

    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<String>(headers), String.class);
    assertThat(response.getBody().contains("Hello from local environment")).isEqualTo(true);
  }
}