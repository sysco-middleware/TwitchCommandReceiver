package no.sysco.cip.controller;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.client.ClientBuilder;

@Configuration
class UserServiceControllerResteasyTestConfig {
  @Value("${users.api.url.v1}")
  private String targetEndpoint;

  @Bean
  public UserServiceControllerResteasyTestProxy getUserServiceControllerResteasyTestProxy() {
    ResteasyClient client = (ResteasyClient)ClientBuilder.newClient();
    ResteasyWebTarget target = client.target(targetEndpoint);
    UserServiceControllerResteasyTestProxy proxy = target.proxy(UserServiceControllerResteasyTestProxy.class);
    return proxy;
  }
}