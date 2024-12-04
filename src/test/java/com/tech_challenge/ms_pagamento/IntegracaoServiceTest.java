package com.tech_challenge.ms_pagamento;

import com.tech_challenge.ms_pagamento.document.*;
import com.tech_challenge.ms_pagamento.document.sustentacao.DiaDaSemana;
import com.tech_challenge.ms_pagamento.document.sustentacao.Intervalo;
import com.tech_challenge.ms_pagamento.document.sustentacao.Location;
import com.tech_challenge.ms_pagamento.dtos.CaixaDTO;
import com.tech_challenge.ms_pagamento.dtos.EscopoLojaMercadoPagoDTO;
import com.tech_challenge.ms_pagamento.dtos.ItemOrdemVendaDTO;
import com.tech_challenge.ms_pagamento.dtos.OrdemVendaMercadoPagoDTO;
import com.tech_challenge.ms_pagamento.dtos.models.CredencialModelDTO;
import com.tech_challenge.ms_pagamento.services.IntegracaoService;
import org.junit.jupiter.api.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.tech_challenge.ms_pagamento.util.TesteUtils.waitForContainers;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
class IntegracaoServiceTest {

    public static final String TOKEN = "TEST-1305516718099336-072118-0539e11bd167f921453fdf836c6de4ec-274249767";

    public static final String USUARIO = "274249767";

    private static String GERARUUID  =  UUID.randomUUID().toString();

    EscopoLojaMercadoPago escopoLojaMercadoPago = null;

    @Autowired
    private IntegracaoService integracaoService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Container
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongodb/mongodb-community-server:6.0-ubi8")
            .waitingFor(Wait.forListeningPort())
            .withStartupTimeout(java.time.Duration.ofMinutes(2));

    @Container
    static final RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:4.0-management")
            .withExposedPorts(5672)
            .waitingFor(Wait.forListeningPort());



    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) throws IOException {
        mongoDBContainer.start();
        rabbitMQContainer.start();

        Process startContainer = new ProcessBuilder(
                "docker", "run", "-d", "webhooksite/cli",
                "--", "whcli", "forward",
                "--token=3336dc93-3464-4da9-8399-c4f127a8d9f8",
                "--target=http://localhost:8091"
        ).start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(startContainer.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        waitForContainers(mongoDBContainer,rabbitMQContainer);

        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);

        registry.add("spring.data.mongodb.database", () -> "testdb");
        registry.add("spring.data.mongodb.port", () -> rabbitMQContainer.getFirstMappedPort().toString());
        // RabbitMQ properties
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", () -> rabbitMQContainer.getFirstMappedPort().toString());

        registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);

        registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);
    }

    @BeforeAll
    void setUp() throws IOException {
        System.out.println("MongoDB rodando: " + mongoDBContainer.isRunning());
        System.out.println("RabbitMQ rodando: " + rabbitMQContainer.isRunning());

        Location location = new Location();
        location.setStreetNumber("3039");
        location.setStreetName("Av. Paulista");
        location.setCityName("São Paulo");
        location.setStateName("São Paulo");
        location.setLatitude(-21.7108032);
        location.setLongitude(-46.6004147);
        location.setReference("Near to Mercado Pago");

        Intervalo intervalo = new Intervalo();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime open = LocalTime.parse("08:00", timeFormatter);
        LocalTime close = LocalTime.parse("16:00", timeFormatter);

        intervalo.setOpen(open);
        intervalo.setClose(close);

        List<Intervalo> intervalos = new ArrayList<>();
        intervalos.add(intervalo);

        DiaDaSemana diaDaSemana = new DiaDaSemana();
        diaDaSemana.setDia(DiaDaSemana.Dia.friday);
        diaDaSemana.setIntervalos(intervalos);

        List<DiaDaSemana> diaDaSemanas = new ArrayList<>();
        diaDaSemanas.add(diaDaSemana);

        escopoLojaMercadoPago = new EscopoLojaMercadoPago();
        escopoLojaMercadoPago.setName("Tech Challenge Filial - SP - ".concat(GERARUUID));
        escopoLojaMercadoPago.setBusinessHours(diaDaSemanas);
        escopoLojaMercadoPago.setLocation(location);
        escopoLojaMercadoPago.setExternalId("TECHLOJA".concat(GERARUUID));


    }

    @Test
    @Order(1)
    void deveCadastrarCredenciaisQuandoDadosForemValidos() {

        CredencialModelDTO credencialDTO = new CredencialModelDTO(TOKEN,
                USUARIO,
                "http://3336dc93-3464-4da9-8399-c4f127a8d9f8.webhook.site",
                CredenciaisAcesso.ServicosIntegracao.MERCADO_PAGO);
        Boolean resultado = integracaoService.cadastroCredenciais(credencialDTO);

        assertNotNull(resultado);
    }

    @Test
    @Order(2)
    void deveCadastrarLojaMercadoLivre() {
        EscopoLojaMercadoPago resultado = integracaoService.cadastroLojaMercadoLivre(escopoLojaMercadoPago);

        assertNotNull(resultado);
        assertNotNull(resultado.getUserId());
    }


    @Test
    @Order(3)
    void deveCadastrarCaixaLojaMercadoLivre() {

        EscopoCaixaMercadoPago escopoCaixaMercadoPago = new EscopoCaixaMercadoPago();

        escopoCaixaMercadoPago.setCategory(null);
        escopoCaixaMercadoPago.setExternal_id(escopoLojaMercadoPago.getExternalId());
        escopoCaixaMercadoPago.setExternal_store_id(escopoLojaMercadoPago.getUserId());
        escopoCaixaMercadoPago.setStore_id(escopoLojaMercadoPago.getUserId());

        escopoCaixaMercadoPago.setName("TECHCX0".concat(GERARUUID));
        EscopoCaixaMercadoPago resultado = integracaoService.cadastrarCaixaLojaMercadoLivre(escopoCaixaMercadoPago);

        assertNotNull(resultado);

        assertNotNull(resultado.getIdAPI());

    }

    @Test
    @Order(4)
    void GerarQR() {

        List<ItemOrdemVendaDTO> itemOrdemVendaDTOS = new ArrayList<>();

        ItemOrdemVendaDTO itemOrdemVendaDTO = new ItemOrdemVendaDTO(
                null,
                "Teste".concat(GERARUUID),
                "Teste".concat(GERARUUID),
                "Teste".concat(GERARUUID),
                BigDecimal.valueOf(100),
                1,
                null,
                BigDecimal.valueOf(100));

        itemOrdemVendaDTOS.add(itemOrdemVendaDTO);

        OrdemVendaMercadoPagoDTO ordemVendaMercadoPagoDTO = new OrdemVendaMercadoPagoDTO(
                "Descrição".concat(GERARUUID),
                GERARUUID,
                itemOrdemVendaDTOS,
                "Teste".concat(GERARUUID),
                BigDecimal.valueOf(100),
                null);

        Object o = rabbitTemplate.receiveAndConvert("pagamento.qrcode", 10000);

        String qrcode = (String) o;
        System.out.println(qrcode);

        assertNotNull(qrcode);

    }


}
