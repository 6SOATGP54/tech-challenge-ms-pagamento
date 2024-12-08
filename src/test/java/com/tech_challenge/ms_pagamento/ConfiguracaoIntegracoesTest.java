package com.tech_challenge.ms_pagamento;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech_challenge.ms_pagamento.controller.ConfiguracaoIntegracoes;
import com.tech_challenge.ms_pagamento.document.EscopoCaixaMercadoPago;
import com.tech_challenge.ms_pagamento.document.EscopoLojaMercadoPago;
import com.tech_challenge.ms_pagamento.dtos.models.CredencialModelDTO;
import com.tech_challenge.ms_pagamento.services.IntegracaoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConfiguracaoIntegracoes.class)
@ExtendWith(MockitoExtension.class)
class ConfiguracaoIntegracoesTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IntegracaoService integracaoService;

    @Test
    void deveCadastrarCredenciaisComSucesso() throws Exception {
        CredencialModelDTO credenciaisAcesso = CredencialModelDTO.builder()
                .token("mockToken123")
                .usuario("mockUsuario")
                .webHook("https://mockwebhook.com")
                .build();

        when(integracaoService.cadastroCredenciais(any(CredencialModelDTO.class))).thenReturn(true);

        mockMvc.perform(post("/integracoes/mercadoPago/cadastroCredenciais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(credenciaisAcesso)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(integracaoService).cadastroCredenciais(any(CredencialModelDTO.class));
    }

    @Test
    void deveCadastrarLojaMercadoLivreComSucesso() throws Exception {
        EscopoLojaMercadoPago escopoLojaMercadoPago = new EscopoLojaMercadoPago();
        escopoLojaMercadoPago.setName("Loja Teste");

        when(integracaoService.cadastroLojaMercadoLivre(any(EscopoLojaMercadoPago.class)))
                .thenReturn(escopoLojaMercadoPago);

        mockMvc.perform(post("/integracoes/mercadoPago/cadastroLojaMercadoPago")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(escopoLojaMercadoPago)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Loja Teste"));

        verify(integracaoService).cadastroLojaMercadoLivre(any(EscopoLojaMercadoPago.class));
    }

    @Test
    void deveCadastrarCaixaLojaMercadoLivreComSucesso() throws Exception {
        EscopoCaixaMercadoPago escopoCaixaMercadoPago = EscopoCaixaMercadoPago.builder()
                .category(5611203L)
                .external_id("external-123")
                .external_store_id("store-456")
                .fixed_amount(false)
                .name("Caixa Teste")
                .store_id("store-789")
                .idAPI("api-12345")
                .usuario("usuario_teste")
                .build();

        when(integracaoService.cadastrarCaixaLojaMercadoLivre(any(EscopoCaixaMercadoPago.class)))
                .thenReturn(escopoCaixaMercadoPago);

        mockMvc.perform(post("/integracoes/mercadoPago/cadastrarCaixaLojaMercadoLivre")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(escopoCaixaMercadoPago)))
                .andExpect(status().isOk());

        verify(integracaoService).cadastrarCaixaLojaMercadoLivre(any(EscopoCaixaMercadoPago.class));
    }

    @Test
    void deveRegistrarPagamentoRecebidoComSucesso() throws Exception {
        Object id = "123";
        Object topic = "payment";

        doNothing().when(integracaoService).consultarPagamento(id, topic);

        mockMvc.perform(post("/integracoes/mercadoPago/pagamentoRecebido")
                        .param("id", id.toString())
                        .param("topic", topic.toString()))
                .andExpect(status().isCreated());

        verify(integracaoService).consultarPagamento(id, topic);
    }
}

