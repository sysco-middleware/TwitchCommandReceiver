package no.sysco.cip.controller;

import no.sysco.cip.model.User;
import no.sysco.cip.service.TwitchDispatch;
import no.sysco.cip.service.UserService;
import no.sysco.cip.util.ErrorMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class TwitchCommandController {

  @Autowired
  private TwitchDispatch twitchDispatch;


  @RequestMapping (
          value = "/changeScene",
          method = RequestMethod.POST,
          produces = MediaType.APPLICATION_JSON_VALUE, //XML by default
          path = "{sceneName}"
  )
  public ResponseEntity<Integer> changeScene(@PathVariable("sceneName") String sceneName) {
    final int result = twitchDispatch.sceneChange(sceneName);
    return getIntegerResponseEntity(result);
  }

  @NotNull
  private ResponseEntity<Integer> getIntegerResponseEntity(int result) {
    if (result == 1)
      return ResponseEntity.ok().build();
    else
      return ResponseEntity.badRequest().build();
  }
}
