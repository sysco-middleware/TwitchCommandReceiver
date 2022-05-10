package com.cegal.twitch.controller;

import com.cegal.twitch.service.TwitchDispatch;
import com.cegal.twitch.util.ErrorMessage;
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


  /*@RequestMapping (
          method = RequestMethod.POST,
          produces = MediaType.APPLICATION_JSON_VALUE, //XML by default
          path = "/changeScene/{sceneName}"
  )*/
  @CrossOrigin(origins = "*")
  @PostMapping("/changeScene/{sceneName}")
  public ResponseEntity<Integer> changeScene(@PathVariable("sceneName") String sceneName) {
    final int result = twitchDispatch.sceneChange(sceneName);
    return getIntegerResponseEntity(result);
  }
  @CrossOrigin(origins = "*")
  @RequestMapping (
          method = RequestMethod.POST,
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE,
          path = "/sendAction"
  )
  public ResponseEntity<Integer> sendAction(@RequestBody String payload) {
    final int result = twitchDispatch.sceneChange(payload);
    return getIntegerResponseEntity(result);
  }

  @CrossOrigin(origins = "*")
  @RequestMapping (
          method = RequestMethod.GET,
          produces = MediaType.APPLICATION_JSON_VALUE, //XML by default
          path = "/getResponse"
  )
  public ResponseEntity<?> getResponse() {
    try {
      twitchDispatch.receiveMessages();
    } catch (InterruptedException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body(new ErrorMessage("Problem getting response. Check server log"));
    }
    final Optional<String> response = Optional.ofNullable(twitchDispatch.getMessage().getBody().toString());
    if (response.isPresent())
      return ResponseEntity.ok(response.get());
    else
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body(new ErrorMessage("Response message not found"));
  }


  @NotNull
  private ResponseEntity<Integer> getIntegerResponseEntity(int result) {
    if (result == 1)
      return ResponseEntity.ok().build();
    else
      return ResponseEntity.badRequest().build();
  }
}
