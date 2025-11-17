package com.ifsp.gru.oficinas4.infra.automa_infra.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,           // Código HTTP (404, 500, etc)
        String message,       // Mensagem de erro legível
        LocalDateTime timestamp, // Quando o erro ocorreu
        String path           // Qual endpoint causou o erro
) {}