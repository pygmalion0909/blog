package com.example.rsocketapi.rsocketTest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class RSocketController {
  
  private final List<RSocketRequester> CLIENTS = new ArrayList<>();
  
  @ConnectMapping
  public void onConnect(RSocketRequester requester) {
    requester.rsocket()
             .onClose()
             .doFirst(() -> { 
                 CLIENTS.add(requester);
               })
               .doOnError(error -> { 
               })
               .doFinally(consumer -> { 
                 CLIENTS.remove(requester);
               })
             .subscribe();
  }

  @MessageMapping("message")
  Mono<Message> message(Message message) {
    this.sendMsg(message);
    return Mono.just(message);
  }

  @MessageMapping("send")
  void sendMsg(Message message) {
    Message msgDto = new Message();
    msgDto.setUsername(message.getUsername());
    msgDto.setMessage(message.getMessage());

    Flux.fromIterable(CLIENTS)
        .doOnNext(ea -> {
          ea.route("")
            .data(msgDto)
            .send()
            .subscribe();
        })
        .subscribe();
  }

}
