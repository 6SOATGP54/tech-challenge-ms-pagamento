package com.tech_challenge.ms_pagamento;

import com.tech_challenge.ms_pagamento.document.*;
import com.tech_challenge.ms_pagamento.dtos.OrdemVendaMercadoPagoDTO;
import com.tech_challenge.ms_pagamento.dtos.models.CredencialModelDTO;
import com.tech_challenge.ms_pagamento.repository.*;
import com.tech_challenge.ms_pagamento.services.IntegracaoService;
import com.tech_challenge.ms_pagamento.services.RequestServices;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
class IntegracaoServiceTest {

    @Autowired
    private IntegracaoService integracaoService;


    @Container
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongodb/mongodb-community-server:6.0-ubi8")
            .waitingFor(Wait.forListeningPort())
            .withStartupTimeout(java.time.Duration.ofMinutes(2));

    @Container
    static final RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:4.0-management")
            .withExposedPorts(5672)
            .waitingFor(Wait.forListeningPort());

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        mongoDBContainer.start();
        rabbitMQContainer.start();

        waitForContainers();


        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);

        registry.add("spring.data.mongodb.database", () -> "testdb");
        registry.add("spring.data.mongodb.port", () -> rabbitMQContainer.getFirstMappedPort().toString());
        // RabbitMQ properties
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", () -> rabbitMQContainer.getFirstMappedPort().toString());

        registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);

        registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);
    }

    private static void waitForContainers() {
        int maxRetries = 30; // Máximo de 30 segundos
        int retryInterval = 1000; // 1 segundo

        while ((!mongoDBContainer.isRunning() || !rabbitMQContainer.isRunning()) && maxRetries > 0) {
            try {
                System.out.println("Aguardando containers estarem prontos...");
                Thread.sleep(retryInterval);
                maxRetries--;
            } catch (InterruptedException e) {
                throw new RuntimeException("Erro ao aguardar containers", e);
            }
        }

        if (!mongoDBContainer.isRunning() || !rabbitMQContainer.isRunning()) {
            throw new IllegalStateException("Os containers não ficaram prontos a tempo!");
        }

        System.out.println("Containers prontos!");
    }

    @BeforeAll
    void setUp() {
        System.out.println("MongoDB rodando: " + mongoDBContainer.isRunning());
        System.out.println("RabbitMQ rodando: " + rabbitMQContainer.isRunning());
    }

    @Test
    void deveCadastrarCredenciaisQuandoDadosForemValidos() {
        CredencialModelDTO credencialDTO = new CredencialModelDTO("token_test",
                "user_test",
                "",
                CredenciaisAcesso.ServicosIntegracao.MERCADO_PAGO);
        Boolean resultado = integracaoService.cadastroCredenciais(credencialDTO);

        assertNotNull(resultado);
    }

//    @Test
//    void deveCadastrarLojaMercadoLivre() {
//        EscopoLojaMercadoPago escopoLojaMercadoPago = new EscopoLojaMercadoPago();
//        escopoLojaMercadoPago.setName("Loja Teste");
//
//        CredenciaisAcesso credenciaisAcesso = new CredenciaisAcesso();
//        credenciaisAcesso.setUsuario("usuario_teste");
//
//        EscopoLojaMercadoPago lojaSalva = new EscopoLojaMercadoPago();
//        lojaSalva.setUserId("12345");
//
//        when(credenciaisIntegracaoRepository.findAll()).thenReturn(List.of(credenciaisAcesso));
//        when(lojaMercadoLivreRepository.save(any(EscopoLojaMercadoPago.class))).thenReturn(lojaSalva);
//
//        EscopoLojaMercadoPago resultado = integracaoService.cadastroLojaMercadoLivre(escopoLojaMercadoPago);
//
//        assertNotNull(resultado);
//        assertEquals("12345", resultado.getUserId());
//        verify(lojaMercadoLivreRepository, times(1)).save(any(EscopoLojaMercadoPago.class));
//    }
//
//    @Test
//    void deveCadastrarCaixaLojaMercadoLivre() {
//        EscopoCaixaMercadoPago caixa = new EscopoCaixaMercadoPago();
//        caixa.setStore_id("12345");
//
//        EscopoLojaMercadoPago loja = new EscopoLojaMercadoPago();
//        loja.setUserId("12345");
//        loja.setExternalId("98765");
//
//        CredenciaisAcesso credenciaisAcesso = new CredenciaisAcesso();
//        credenciaisAcesso.setUsuario("usuario_teste");
//        credenciaisAcesso.setToken("token_teste");
//
//        when(lojaMercadoLivreRepository.findByUserId("12345")).thenReturn(loja);
//        when(credenciaisIntegracaoRepository.findAll()).thenReturn(List.of(credenciaisAcesso));
//        when(caixaMercadoPagoRepository.save(any(EscopoCaixaMercadoPago.class))).thenReturn(caixa);
//
//        EscopoCaixaMercadoPago resultado = integracaoService.cadastrarCaixaLojaMercadoLivre(caixa);
//
//        assertNotNull(resultado);
//        assertEquals("12345", resultado.getStore_id());
//        verify(caixaMercadoPagoRepository, times(1)).save(any(EscopoCaixaMercadoPago.class));
//    }
//
//    @Test
//    void deveConsultarPagamentoEEnviarMensagemQuandoPagamentoEfetuado() {
//        Object id = "123";
//        Object type = "payment";
//
//        CredenciaisAcesso credenciaisAcesso = new CredenciaisAcesso();
//        credenciaisAcesso.setUsuario("usuario_teste");
//        credenciaisAcesso.setToken("token_teste");
//
//        when(credenciaisIntegracaoRepository.findAll()).thenReturn(List.of(credenciaisAcesso));
//        when(RequestServices.requestToMercadoPago(any(), any(), anyString(), any(), any()))
//                .thenReturn("{\"status\":\"approved\",\"external_reference\":\"12345\"}");
//
//        integracaoService.consultarPagamento(id, type);
//
//        verify(rabbitTemplate, times(1)).convertAndSend(eq("pagamento.ex"), eq(""), any(Message.class));
//    }
//
//    @Test
//    void deveGerarQRCodeEPublicarNaFilaQuandoBemSucedido() {
//        OrdemVendaMercadoPagoDTO ordemVendaMercadoPagoDTO = new OrdemVendaMercadoPagoDTO(
//                "Descrição",
//                "12345",
//                List.of(),
//                "Título",
//                BigDecimal.valueOf(100),
//                "webhook_url"
//        );
//
//        CredenciaisAcesso credenciaisAcesso = new CredenciaisAcesso();
//        credenciaisAcesso.setUsuario("usuario_teste");
//        credenciaisAcesso.setToken("token_teste");
//
//        EscopoCaixaMercadoPago caixa = new EscopoCaixaMercadoPago();
//        caixa.setExternal_id("caixa_teste");
//
//        when(credenciaisIntegracaoRepository.findAll()).thenReturn(List.of(credenciaisAcesso));
//        when(caixaMercadoPagoRepository.findByUsuario("usuario_teste")).thenReturn(Optional.of(caixa));
//        when(RequestServices.requestToMercadoPago(any(), any(), anyString(), any(), any()))
//                .thenReturn("QRCodeData");
//
//        integracaoService.gerarQR(ordemVendaMercadoPagoDTO);
//
//        verify(rabbitTemplate, times(1)).convertAndSend(eq("pagamento.qrcode"), eq("QRCodeData"));
//    }
//
//    @Test
//    void deveLancarExcecaoQuandoNaoForPossivelGerarQRCode() {
//        OrdemVendaMercadoPagoDTO ordemVendaMercadoPagoDTO = new OrdemVendaMercadoPagoDTO(
//                "Descrição",
//                "12345",
//                List.of(),
//                "Título",
//                BigDecimal.valueOf(100),
//                "webhook_url"
//        );
//
//        CredenciaisAcesso credenciaisAcesso = new CredenciaisAcesso();
//        credenciaisAcesso.setUsuario("usuario_teste");
//        credenciaisAcesso.setToken("token_teste");
//
//        EscopoCaixaMercadoPago caixa = new EscopoCaixaMercadoPago();
//        caixa.setExternal_id("caixa_teste");
//
//        when(credenciaisIntegracaoRepository.findAll()).thenReturn(List.of(credenciaisAcesso));
//        when(caixaMercadoPagoRepository.findByUsuario("usuario_teste")).thenReturn(Optional.of(caixa));
//        when(RequestServices.requestToMercadoPago(any(), any(), anyString(), any(), any()))
//                .thenReturn(null);
//
//        assertThrows(RuntimeException.class, () -> integracaoService.gerarQR(ordemVendaMercadoPagoDTO));
//
//        verify(rabbitTemplate, times(1)).convertAndSend(eq("pagamento.ex"), eq(""), any(Message.class));
//    }
}
