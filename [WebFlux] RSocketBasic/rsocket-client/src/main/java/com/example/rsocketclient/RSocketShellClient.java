package com.example.rsocketclient;

import java.time.Duration;

import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@ShellComponent
public class RSocketShellClient {
  
  private Disposable disposable;

  private final RSocketRequester rSocketRequester;

  public RSocketShellClient(RSocketRequester rSocketRequester) {
    this.rSocketRequester = rSocketRequester;
  }
  
  @ShellMethod("Send one request. One response will be printed.")
  public void requestResponse() throws InterruptedException {
    log.info("Sending one request. Waiting for one response...");
    this.rSocketRequester.route("request-response")
                         .data(new Message("superpil", "client"))
                         .retrieveMono(Message.class)
                         .log()
                         .block();
  }

  @ShellMethod("Send one request. No response will be returned.")
  public void fireAndForget() throws InterruptedException {
    log.info("Fire-And-Forget. Sending one request. Expect no response (check server log)...");
    this.rSocketRequester.route("fire-and-forget")
                         .data(new Message("superpil", "client"))
                         .send()
                         .block();
  }
  
  // stream start
  @ShellMethod("Send one request. Many responses (stream) will be printed.")
  public void stream() {
    log.info("Request-Stream. Sending one request. Waiting for unlimited responses (Stop process to quit)...");
    this.disposable = this.rSocketRequester.route("stream")
                                           .data(new Message("superpil", "client"))
                                           .retrieveFlux(Message.class)
                                           .log()
                                           .subscribe();
  }

  // stream stop
  @ShellMethod("Stop streaming messages from the server.")
  public void stop(){
    if(null != disposable) disposable.dispose();
  }

  // channel
  @ShellMethod("Stream some settings to the server. Stream of responses will be printed.")
  public void channel(){
    Mono<Duration> setting1 = Mono.just(Duration.ofSeconds(1));
    Mono<Duration> setting2 = Mono.just(Duration.ofSeconds(3)).delayElement(Duration.ofSeconds(5));
    Mono<Duration> setting3 = Mono.just(Duration.ofSeconds(5)).delayElement(Duration.ofSeconds(15));
    Flux<Duration> settings = Flux.concat(setting1, setting2, setting3)
                                  .log()
                                  .doOnNext(d -> log.info("Sending setting for {}-second interval.", d.getSeconds()));

    disposable = this.rSocketRequester.route("channel")
                                      .data(settings)
                                      .retrieveFlux(Message.class)
                                      .log()
                                      .subscribe();
  }
  
}
