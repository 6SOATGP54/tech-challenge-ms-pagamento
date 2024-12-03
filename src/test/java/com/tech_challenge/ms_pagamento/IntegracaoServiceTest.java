package com.tech_challenge.ms_pagamento;

import com.tech_challenge.ms_pagamento.document.CredenciaisAcesso;
import com.tech_challenge.ms_pagamento.dtos.models.CredencialModelDTO;
import com.tech_challenge.ms_pagamento.repository.CredenciaisIntegracaoRepository;
import com.tech_challenge.ms_pagamento.services.IntegracaoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class IntegracaoServiceTest {

    @InjectMocks
    private IntegracaoService integracaoService;

    @Mock
    private CredenciaisIntegracaoRepository credenciaisIntegracaoRepository;

    @Test
    void testCadastroCredenciais() {
        // Mock de retorno do repositório
        CredenciaisAcesso mockEntity = new CredenciaisAcesso();
        mockEntity.setId(1L); // Simula o retorno com ID preenchido
        Mockito.when(credenciaisIntegracaoRepository.save(Mockito.any())).thenReturn(mockEntity);

        // Teste do método
        CredencialModelDTO dto = new CredencialModelDTO();
        Boolean result = integracaoService.cadastroCredenciais(dto);

        // Validação
        assertTrue(result);
        Mockito.verify(credenciaisIntegracaoRepository, Mockito.times(1)).save(Mockito.any());
    }
}
