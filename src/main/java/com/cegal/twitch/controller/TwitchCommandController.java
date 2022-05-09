package com.cegal.twitch.controller;

import com.cegal.twitch.service.TwitchDispatch;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TwitchCommandController {

  @Autowired
  private TwitchDispatch twitchDispatch;


  /*@RequestMapping (
          method = RequestMethod.POST,
          produces = MediaType.APPLICATION_JSON_VALUE, //XML by default
          path = "/changeScene/{sceneName}"
  )*/
  @PostMapping("/changeScene/{sceneName}")
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
