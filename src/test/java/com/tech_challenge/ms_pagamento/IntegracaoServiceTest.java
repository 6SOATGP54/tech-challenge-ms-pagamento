package com.tech_challenge.ms_pagamento;

import com.tech_challenge.ms_pagamento.document.CredenciaisAcesso;
import com.tech_challenge.ms_pagamento.document.EscopoLojaMercadoPago;
import com.tech_challenge.ms_pagamento.dtos.models.CredencialModelDTO;
import com.tech_challenge.ms_pagamento.repository.CaixaMercadoPagoRepository;
import com.tech_challenge.ms_pagamento.repository.CredenciaisIntegracaoRepository;
import com.tech_challenge.ms_pagamento.repository.LojaMercadoLivreRepository;
import com.tech_challenge.ms_pagamento.services.IntegracaoService;
import com.tech_challenge.ms_pagamento.services.RequestServices;
import com.tech_challenge.ms_pagamento.util.Assembler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IntegracaoServiceTest {

    @InjectMocks
    private IntegracaoService integracaoService;

    @Mock
    private CredenciaisIntegracaoRepository credenciaisIntegracaoRepository;

    @Mock
    private LojaMercadoLivreRepository lojaMercadoLivreRepository;

    @Mock
    private CaixaMercadoPagoRepository caixaMercadoPagoRepository;

    @Mock
    private RequestServices requestServicesMock;

    @Autowired
    private ApplicationContext context;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);  // Inicializa os mocks
        Assembler.modelMapper = new ModelMapper(); // Inicializa o ModelMapper estático
        System.setProperty("mercadopago.mock", "true");

        if (context != null) {
            Environment environment = context.getEnvironment();
        }
    }

    @Disabled
    void deveCadastrarCredenciaisComSucesso() {
        // Arrange
        CredencialModelDTO credencialDTO = CredencialModelDTO.builder()
                .token("mockToken123")
                .usuario("mockUsuario")
                .webHook("https://mockwebhook.com")
                .build();

        CredenciaisAcesso credenciaisSalvas = CredenciaisAcesso.builder()
                .token("mockToken123")
                .usuario("mockUsuario")
                .webHook("https://mockwebhook.com")
                .tipoIntegracao(CredenciaisAcesso.ServicosIntegracao.MERCADO_PAGO)
                .build();

        credenciaisSalvas.setId("1");

        // Mockando comportamento do repositório para salvar a entidade
        when(credenciaisIntegracaoRepository.save(any(CredenciaisAcesso.class))).thenReturn(credenciaisSalvas);

        // Act
        Boolean resultado = integracaoService.cadastroCredenciais(credencialDTO);

        // Assert
        assertTrue(resultado, "O método deveria retornar true quando o ID está presente.");
        verify(credenciaisIntegracaoRepository, times(1)).save(any(CredenciaisAcesso.class));
    }

    @Test
    void naoDeveCadastrarCredenciaisComSucesso() {
        // Arrange
        CredencialModelDTO credencialDTO = CredencialModelDTO.builder()
                .token("mockToken123")
                .usuario("mockUsuario")
                .webHook("https://mockwebhook.com")
                .build();

        CredenciaisAcesso credenciaisSalvas = CredenciaisAcesso.builder()
                .token("mockToken123")
                .usuario("mockUsuario")
                .webHook("https://mockwebhook.com")
                .tipoIntegracao(CredenciaisAcesso.ServicosIntegracao.MERCADO_PAGO)
                .build();


        // Mockando comportamento do repositório para salvar a entidade
        when(credenciaisIntegracaoRepository.save(any(CredenciaisAcesso.class))).thenReturn(credenciaisSalvas);

        // Act
        Boolean resultado = integracaoService.cadastroCredenciais(credencialDTO);

        // Assert
        assertFalse(resultado);
        verify(credenciaisIntegracaoRepository, times(1)).save(any(CredenciaisAcesso.class));
    }

    @Disabled
    void deveCadastrarLojaComSucesso() {
        // Arrange
        EscopoLojaMercadoPago escopoLojaMercadoPago = new EscopoLojaMercadoPago();
        escopoLojaMercadoPago.setName("Loja Teste");

        // Criando as credenciais de acesso
        CredenciaisAcesso credenciaisAcesso = CredenciaisAcesso.builder()
                .token("mockToken123")
                .usuario("mockUsuario")
                .webHook("https://mockwebhook.com")
                .tipoIntegracao(CredenciaisAcesso.ServicosIntegracao.MERCADO_PAGO)
                .build();
        credenciaisAcesso.setId("1");

        // Mockando o comportamento do repositório de credenciais
        when(credenciaisIntegracaoRepository.findAll()).thenReturn(List.of(credenciaisAcesso));

        // Mockando o comportamento do repositório de loja
        when(lojaMercadoLivreRepository.save(any(EscopoLojaMercadoPago.class))).thenReturn(escopoLojaMercadoPago);

        // Mockando a chamada para MercadoPago
        Object respostaMock = 123L; // Simulando uma resposta válida de MercadoPago
        when(requestServicesMock.requestToMercadoPago(any(), any(), any(), eq(HttpMethod.POST), any()))
                .thenReturn(respostaMock);

        // Act
        EscopoLojaMercadoPago result = integracaoService.cadastroLojaMercadoLivre(escopoLojaMercadoPago);

        // Assert
        assertNotNull(result);
        assertEquals("123", result.getUserId());  // Verificando se o userId foi corretamente atribuído
        verify(lojaMercadoLivreRepository, times(1)).save(any(EscopoLojaMercadoPago.class));  // Verifica se o save foi chamado
    }

    @Disabled
    void naoDeveCadastrarLojaQuandoNaoRetornarUserId() {
        // Arrange
        EscopoLojaMercadoPago escopoLojaMercadoPago = new EscopoLojaMercadoPago();
        escopoLojaMercadoPago.setName("Loja Teste");

        CredenciaisAcesso credenciaisAcesso = new CredenciaisAcesso();
        credenciaisAcesso.setUsuario("usuarioTeste");

        // Mocking o comportamento do repositório
        when(credenciaisIntegracaoRepository.findAll()).thenReturn(List.of(credenciaisAcesso));
        when(lojaMercadoLivreRepository.save(any(EscopoLojaMercadoPago.class))).thenReturn(escopoLojaMercadoPago);

        // Mockando a chamada para MercadoPago
        when(requestServicesMock.requestToMercadoPago(any(), any(), any(), eq(HttpMethod.POST), any()))
                .thenReturn(null);  // Simulando a resposta nula

        // Act
        EscopoLojaMercadoPago result = integracaoService.cadastroLojaMercadoLivre(escopoLojaMercadoPago);

        // Assert
        assertNull(result.getUserId());
        verify(lojaMercadoLivreRepository, times(0)).save(any(EscopoLojaMercadoPago.class));
    }

}

