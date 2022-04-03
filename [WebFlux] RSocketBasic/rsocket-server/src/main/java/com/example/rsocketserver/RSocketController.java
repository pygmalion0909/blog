package com.example.rsocketserver;

import java.time.Duration;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Controller
@Slf4j
public class RSocketController {
  
  @MessageMapping("request-response")
  public Message requestResponse(Message request) {
    log.info("Received request-response request: {}", request);
    return new Message("superpil", "server");
  }

  @MessageMapping("fire-and-forget")
  public void fireAndForget(Message request) {
    log.info("Received fire-and-forget request: {}", request);
  }

  @MessageMapping("stream")
  Flux<Message> stream(Message request) {
    return Flux.interval(Duration.ofSeconds(1))
               .map(index -> new Message("superpil", "server"));
  }

  @MessageMapping("channel")
  Flux<Message> channel(final Flux<Duration> settings) {
    log.info("settinsg : {}", settings);
    return settings.doOnNext(setting -> log.info("Frequency setting is {} second(s).", setting.getSeconds()))
                   .switchMap(setting -> Flux.interval(setting)
                                             .map(index -> new Message("superpil", "server"))
                             );
  }

}
