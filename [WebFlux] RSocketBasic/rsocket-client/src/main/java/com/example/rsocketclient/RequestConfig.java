package com.example.rsocketclient;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.MimeTypeUtils;

import reactor.util.retry.Retry;

@Configuration
public class RequestConfig {

  @Bean
  public RSocketRequester getRSocketRequester(RSocketStrategies rSocketStrategies){
    return RSocketRequester.builder()
                           .rsocketConnector(connector -> connector.reconnect(Retry.backoff(10, Duration.ofMillis(500)))) // 서버 재연결 설정
                           .rsocketStrategies(rSocketStrategies) // 인코더, 디코더 설정
                           .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
                           .tcp("localhost", 7000);
  }
  
}
