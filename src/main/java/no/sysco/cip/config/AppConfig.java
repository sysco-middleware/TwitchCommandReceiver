package no.sysco.cip.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * This class is not currently in use. It remains here just as a showcase on how to read data from application.yml file and
 * apply Spring Boot event handler. It also demonstrate how to check current and resource directories and copy files by using
 * Java NIO classes.
 */
//@Configuration
public class AppConfig {

  private final Logger log = LogManager.getLogger(AppConfig.class);

  @Value("${environment}")
  private String environment;

  /*@Autowired
  private Environment environmen;*/

  private enum Env {
      LOCAL,
      DEV,
      TEST,
      PROD,
  }

//  @PostConstruct
  public void setupEnvironment() {
    try {
      copyWsdl(currentEnv());
    } catch (IllegalArgumentException wrongEnv) {
      log.error(wrongEnv);
      System.exit(1);
    }
  }

  private Env currentEnv(){
   /* Env currentEnv = Env.valueOf(environment.toUpperCase());
    switch (currentEnv) {
      case TEST:
      case PROD: return Env.PROD;
      default:
        return Env.TEST;
    }*/
    return Env.valueOf(environment.toUpperCase());
  }

  private void copyWsdl(Env env) {
    try{
      log.info("Working Directory = " + System.getProperty("user.dir"));
      Path currentRelativePath = Paths.get("");
      String s = currentRelativePath.toAbsolutePath().toString();
      log.info("Current relative path is: " + s);

      Path source = Paths.get(getClass().getClassLoader().getResource("wsdl/" + env.toString().toLowerCase() +  "/SentraleFolkeregister-simple.wsdl").toURI());
      Path dest = Paths.get(getClass().getClassLoader().getResource("wsdl/SentraleFolkeregister-simple.wsdl").toURI());
      Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException | URISyntaxException | NullPointerException error) {
        log.error(error);
    }
  }
}
