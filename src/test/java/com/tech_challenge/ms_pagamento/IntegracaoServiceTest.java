package com.tech_challenge.ms_pagamento;

import com.tech_challenge.ms_pagamento.document.*;
import com.tech_challenge.ms_pagamento.dtos.OrdemVendaMercadoPagoDTO;
import com.tech_challenge.ms_pagamento.dtos.models.CredencialModelDTO;
import com.tech_challenge.ms_pagamento.repository.*;
import com.tech_challenge.ms_pagamento.services.IntegracaoService;
import com.tech_challenge.ms_pagamento.services.RequestServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IntegracaoServiceTest {

    @Mock
    private CredenciaisIntegracaoRepository credenciaisIntegracaoRepository;

    @Mock
    private LojaMercadoLivreRepository lojaMercadoLivreRepository;

    @Mock
    private CaixaMercadoPagoRepository caixaMercadoPagoRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private IntegracaoService integracaoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCadastrarCredenciaisQuandoDadosForemValidos() {
        CredencialModelDTO credencialDTO = new CredencialModelDTO("token_test", "user_test", "");

        CredenciaisAcesso credenciaisAcesso = new CredenciaisAcesso();

        when(credenciaisIntegracaoRepository.save(any(CredenciaisAcesso.class)))
                .thenReturn(credenciaisAcesso);

        Boolean resultado = integracaoService.cadastroCredenciais(credencialDTO);

        assertTrue(resultado);
        verify(credenciaisIntegracaoRepository, times(1)).save(any(CredenciaisAcesso.class));
    }

    @Test
    void deveCadastrarLojaMercadoLivre() {
        EscopoLojaMercadoPago escopoLojaMercadoPago = new EscopoLojaMercadoPago();
        escopoLojaMercadoPago.setName("Loja Teste");

        CredenciaisAcesso credenciaisAcesso = new CredenciaisAcesso();
        credenciaisAcesso.setUsuario("usuario_teste");

        EscopoLojaMercadoPago lojaSalva = new EscopoLojaMercadoPago();
        lojaSalva.setUserId("12345");

        when(credenciaisIntegracaoRepository.findAll()).thenReturn(List.of(credenciaisAcesso));
        when(lojaMercadoLivreRepository.save(any(EscopoLojaMercadoPago.class))).thenReturn(lojaSalva);

        EscopoLojaMercadoPago resultado = integracaoService.cadastroLojaMercadoLivre(escopoLojaMercadoPago);

        assertNotNull(resultado);
        assertEquals("12345", resultado.getUserId());
        verify(lojaMercadoLivreRepository, times(1)).save(any(EscopoLojaMercadoPago.class));
    }

    @Test
    void deveCadastrarCaixaLojaMercadoLivre() {
        EscopoCaixaMercadoPago caixa = new EscopoCaixaMercadoPago();
        caixa.setStore_id("12345");

        EscopoLojaMercadoPago loja = new EscopoLojaMercadoPago();
        loja.setUserId("12345");
        loja.setExternalId("98765");

        CredenciaisAcesso credenciaisAcesso = new CredenciaisAcesso();
        credenciaisAcesso.setUsuario("usuario_teste");
        credenciaisAcesso.setToken("token_teste");

        when(lojaMercadoLivreRepository.findByUserId("12345")).thenReturn(loja);
        when(credenciaisIntegracaoRepository.findAll()).thenReturn(List.of(credenciaisAcesso));
        when(caixaMercadoPagoRepository.save(any(EscopoCaixaMercadoPago.class))).thenReturn(caixa);

        EscopoCaixaMercadoPago resultado = integracaoService.cadastrarCaixaLojaMercadoLivre(caixa);

        assertNotNull(resultado);
        assertEquals("12345", resultado.getStore_id());
        verify(caixaMercadoPagoRepository, times(1)).save(any(EscopoCaixaMercadoPago.class));
    }

    @Test
    void deveConsultarPagamentoEEnviarMensagemQuandoPagamentoEfetuado() {
        Object id = "123";
        Object type = "payment";

        CredenciaisAcesso credenciaisAcesso = new CredenciaisAcesso();
        credenciaisAcesso.setUsuario("usuario_teste");
        credenciaisAcesso.setToken("token_teste");

        when(credenciaisIntegracaoRepository.findAll()).thenReturn(List.of(credenciaisAcesso));
        when(RequestServices.requestToMercadoPago(any(), any(), anyString(), any(), any()))
                .thenReturn("{\"status\":\"approved\",\"external_reference\":\"12345\"}");

        integracaoService.consultarPagamento(id, type);

        verify(rabbitTemplate, times(1)).convertAndSend(eq("pagamento.ex"), eq(""), any(Message.class));
    }

    @Test
    void deveGerarQRCodeEPublicarNaFilaQuandoBemSucedido() {
        OrdemVendaMercadoPagoDTO ordemVendaMercadoPagoDTO = new OrdemVendaMercadoPagoDTO(
                "Descrição",
                "12345",
                List.of(),
                "Título",
                BigDecimal.valueOf(100),
                "webhook_url"
        );

        CredenciaisAcesso credenciaisAcesso = new CredenciaisAcesso();
        credenciaisAcesso.setUsuario("usuario_teste");
        credenciaisAcesso.setToken("token_teste");

        EscopoCaixaMercadoPago caixa = new EscopoCaixaMercadoPago();
        caixa.setExternal_id("caixa_teste");

        when(credenciaisIntegracaoRepository.findAll()).thenReturn(List.of(credenciaisAcesso));
        when(caixaMercadoPagoRepository.findByUsuario("usuario_teste")).thenReturn(Optional.of(caixa));
        when(RequestServices.requestToMercadoPago(any(), any(), anyString(), any(), any()))
                .thenReturn("QRCodeData");

        integracaoService.gerarQR(ordemVendaMercadoPagoDTO);

        verify(rabbitTemplate, times(1)).convertAndSend(eq("pagamento.qrcode"), eq("QRCodeData"));
    }

    @Test
    void deveLancarExcecaoQuandoNaoForPossivelGerarQRCode() {
        OrdemVendaMercadoPagoDTO ordemVendaMercadoPagoDTO = new OrdemVendaMercadoPagoDTO(
                "Descrição",
                "12345",
                List.of(),
                "Título",
                BigDecimal.valueOf(100),
                "webhook_url"
        );

        CredenciaisAcesso credenciaisAcesso = new CredenciaisAcesso();
        credenciaisAcesso.setUsuario("usuario_teste");
        credenciaisAcesso.setToken("token_teste");

        EscopoCaixaMercadoPago caixa = new EscopoCaixaMercadoPago();
        caixa.setExternal_id("caixa_teste");

        when(credenciaisIntegracaoRepository.findAll()).thenReturn(List.of(credenciaisAcesso));
        when(caixaMercadoPagoRepository.findByUsuario("usuario_teste")).thenReturn(Optional.of(caixa));
        when(RequestServices.requestToMercadoPago(any(), any(), anyString(), any(), any()))
                .thenReturn(null);

        assertThrows(RuntimeException.class, () -> integracaoService.gerarQR(ordemVendaMercadoPagoDTO));

        verify(rabbitTemplate, times(1)).convertAndSend(eq("pagamento.ex"), eq(""), any(Message.class));
    }
}
