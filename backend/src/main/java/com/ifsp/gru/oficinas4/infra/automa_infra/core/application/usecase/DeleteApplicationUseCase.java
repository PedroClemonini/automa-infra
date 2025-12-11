package com.ifsp.gru.oficinas4.infra.automa_infra.core.application.usecase;

import com.ifsp.gru.oficinas4.infra.automa_infra.core.application.port.ApplicationRepositoryPort;
import com.ifsp.gru.oficinas4.infra.automa_infra.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DeleteApplicationUseCase {

    private final ApplicationRepositoryPort applicationRepository;

    /**
     * Exclui uma Application pelo ID, garantindo que ela existe.
     * @param id O ID da Application a ser excluída.
     * @throws ResourceNotFoundException se a Application não for encontrada.
     */
    @Transactional
    public void execute(Long id) { // Alterado para void

        // 1. Verifica se a Application existe (necessário para lançar a 404 correta)
        applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application não encontrada com ID: " + id));
        // Mensagem de erro mais clara, incluindo o ID

        // 2. Deleta a Application (chamando a Porta/Adapter)
        applicationRepository.delete(id);

        // Retorno removido
    }
}