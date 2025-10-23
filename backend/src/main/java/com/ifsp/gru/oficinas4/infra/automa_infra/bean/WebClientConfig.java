package com.ifsp.gru.oficinas4.infra.automa_infra.config; // (Crie o pacote 'config')

import com.ifsp.gru.oficinas4.infra.automa_infra.ProxmoxProperties;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient proxmoxWebClient(ProxmoxProperties properties) throws SSLException {

        // 1. Configura para aceitar certificados SSL auto-assinados (Necessário para Proxmox)
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));

        // 2. Constrói a Base URL a partir do ProxmoxProperties
        String baseUrl = String.format("https://%s:%d/api2/json",
                properties.getHost(),
                properties.getPort());

        // 3. Cria o Bean do WebClient
        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}