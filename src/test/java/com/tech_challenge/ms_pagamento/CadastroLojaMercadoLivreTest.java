package com.tech_challenge.ms_pagamento;

import com.tech_challenge.ms_pagamento.document.CredenciaisAcesso;
import com.tech_challenge.ms_pagamento.document.EscopoLojaMercadoPago;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Test
void CadastroLojaMercadoLivreTest() {
    // Mock da entidade EscopoLojaMercadoPago
    EscopoLojaMercadoPago mockLoja = new EscopoLojaMercadoPago();
    mockLoja.setUserId("123");

    // Mock do repositório
    Mockito.when(lojaMercadoLivreRepository.save(Mockito.any())).thenReturn(mockLoja);

    // Mock das credenciais
    CredenciaisAcesso mockCredenciais = new CredenciaisAcesso();
    mockCredenciais.setUsuario("usuario");
    Mockito.when(credenciaisIntegracaoRepository.findAll()).thenReturn(List.of(mockCredenciais));

    // Teste do metodo
    EscopoLojaMercadoPago inputLoja = new EscopoLojaMercadoPago();
    EscopoLojaMercadoPago result = integracaoService.cadastroLojaMercadoLivre(inputLoja);

    // Validação
    assertNotNull(result.getUserId());
    Mockito.verify(lojaMercadoLivreRepository, Mockito.times(1)).save(Mockito.any());
}
