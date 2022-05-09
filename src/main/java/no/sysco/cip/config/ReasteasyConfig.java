package no.sysco.cip.config;

import org.springframework.context.annotation.Configuration;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@Configuration
@ApplicationPath("/api/v2")
public class ReasteasyConfig extends Application {

}
