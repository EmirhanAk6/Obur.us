package com.project.Obur.us.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class WebClientConfig {

    @Value("${recommender.base-url}")
    private String recommenderBaseUrl;

    @Value("${recommender.timeout:30s}")
    private Duration timeout;

    @Bean
    public WebClient recommenderWebClient() {

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) timeout.toMillis())
                .responseTimeout(timeout)
                .doOnConnected(conn ->
                        conn.addHandlerLast(
                                new ReadTimeoutHandler(timeout.toSeconds(), TimeUnit.SECONDS)
                        ).addHandlerLast(
                                new WriteTimeoutHandler(timeout.toSeconds(), TimeUnit.SECONDS)
                        )
                );

        log.info("Recommender WebClient initialized with baseUrl={} timeout={}",
                recommenderBaseUrl, timeout);

        return WebClient.builder()
                .baseUrl(recommenderBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}