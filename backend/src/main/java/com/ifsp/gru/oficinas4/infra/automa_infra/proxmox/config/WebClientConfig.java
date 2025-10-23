package com.ifsp.gru.oficinas4.infra.automa_infra.proxmox.config;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final ProxmoxProperties proxmoxProperties;

    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .secure(ssl -> {
                    try {
                        ssl.sslContext(
                                SslContextBuilder.forClient()
                                        .trustManager(InsecureTrustManagerFactory.INSTANCE)
                                        .build()
                        );
                    } catch (SSLException e) {
                        throw new RuntimeException(e);
                    }
                });

        return WebClient.builder()
                .baseUrl(proxmoxProperties.getHost())
                .defaultHeaders(headers -> headers.add(
                        "Authorization",
                        "PVEAPIToken=" +
                                proxmoxProperties.getUser() + "@" +
                                proxmoxProperties.getRealm() + "!" +
                                proxmoxProperties.getTokenName() + "=" +
                                proxmoxProperties.getToken()
                ))
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

}