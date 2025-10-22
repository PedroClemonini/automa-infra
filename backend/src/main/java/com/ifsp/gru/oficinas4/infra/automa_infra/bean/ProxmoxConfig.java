package com.ifsp.gru.oficinas4.infra.automa_infra.bean;
import com.ifsp.gru.oficinas4.infra.automa_infra.ProxmoxProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ProxmoxConfig {

    @Bean
    public WebClient proxmoxWebClient(ProxmoxProperties properties) {

        // Formato do API Token: PVEAPIToken=USER@REALM!TOKENID=SECRET
        String apiToken = "PVEAPIToken=" + properties.getUser() + "@" + properties.getRealm()
                + "!" + properties.getTokenId() + "=" + properties.getSecret();

        return WebClient.builder()
                .baseUrl("https://" + properties.getHost() + ":8006/api2/json")
                .defaultHeader("Authorization", apiToken)
                // .clientConnector(insecureConnector) // Caso precise ignorar SSL (NÃO RECOMENDADO)
                .build();
    }
}