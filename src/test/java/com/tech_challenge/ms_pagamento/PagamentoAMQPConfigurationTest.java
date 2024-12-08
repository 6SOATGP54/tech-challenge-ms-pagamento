package com.tech_challenge.ms_pagamento;

import com.tech_challenge.ms_pagamento.conf.PagamentoAMQPConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

import static com.tech_challenge.ms_pagamento.conf.PagamentoAMQPConfiguration.PAGAMENTO_QRCODE;
import static com.tech_challenge.ms_pagamento.conf.PagamentoAMQPConfiguration.PEDIDO_EFETUADO;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(PagamentoAMQPConfiguration.class)
public class PagamentoAMQPConfigurationTest {

    @MockBean
    private ConnectionFactory connectionFactory;

    @MockBean
    private RabbitAdmin rabbitAdmin;

    @MockBean
    private Jackson2JsonMessageConverter messageConverter;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PagamentoAMQPConfiguration pagamentoAMQPConfiguration;

    @BeforeEach
    void setUp() {
        // Simulando o comportamento dos mocks conforme necessário
        when(rabbitTemplate.getConnectionFactory()).thenReturn(connectionFactory);
    }

    @Test
    void testCriaRabbitAdmin() {
        // Arrange & Act
        RabbitAdmin rabbitAdmin = pagamentoAMQPConfiguration.criaRabbitAdmin(connectionFactory);

        // Assert
        assertNotNull(rabbitAdmin);
    }

    @Test
    void testInicializaAdmin() {
        // Arrange
        ApplicationReadyEvent event = mock(ApplicationReadyEvent.class);

        // Act
        ApplicationListener<ApplicationReadyEvent> listener = pagamentoAMQPConfiguration.inicializaAdmin(rabbitAdmin);
        listener.onApplicationEvent(event);

        // Assert
        verify(rabbitAdmin).initialize();
    }

    @Test
    void testMessageConverter() {
        // Act
        Jackson2JsonMessageConverter converter = pagamentoAMQPConfiguration.messageConverter();

        // Assert
        assertNotNull(converter);
    }

    @Test
    void testRabbitTemplate() {
        // Act
        RabbitTemplate template = pagamentoAMQPConfiguration.rabbitTemplate(connectionFactory, messageConverter);

        // Assert
        assertNotNull(template);
        assertEquals(connectionFactory, template.getConnectionFactory());
    }

    @Test
    void testFilaQR() {
        // Act
        Queue filaQR = pagamentoAMQPConfiguration.filaQR();

        // Assert
        assertNotNull(filaQR);
        assertEquals(PAGAMENTO_QRCODE, filaQR.getName());
    }

    @Test
    void testFilaPagamentoConcluido() {
        // Act
        Queue filaPagamentoConcluido = pagamentoAMQPConfiguration.filaPagamentoConcluido();

        // Assert
        assertNotNull(filaPagamentoConcluido);
        assertEquals("pagamento.concluido", filaPagamentoConcluido.getName());
    }

    @Test
    void testFilaPreparacao() {
        // Act
        Queue filaPreparacao = pagamentoAMQPConfiguration.filaPreparacao();

        // Assert
        assertNotNull(filaPreparacao);
        assertEquals("preparacao.iniciar", filaPreparacao.getName());
    }

    @Test
    void testFanoutExchangePagamento() {
        // Act
        FanoutExchange fanoutExchange = pagamentoAMQPConfiguration.fanoutExchangePagamento();

        // Assert
        assertNotNull(fanoutExchange);
        assertEquals("pagamento.ex", fanoutExchange.getName());
    }

    @Test
    void testBindingPagamentoConcluido() {
        // Act
        Binding binding = pagamentoAMQPConfiguration.bindingPagamentoConcluido();

        // Assert
        assertNotNull(binding);
        // Usamos getDestination() em vez de getQueue()
        assertEquals("pagamento.concluido", binding.getDestination());
    }

    @Test
    void testPedidoQueueEfetuado() {
        // Act
        Queue pedidoQueueEfetuado = pagamentoAMQPConfiguration.pedidoQueueEfetuado();

        // Assert
        assertNotNull(pedidoQueueEfetuado);
        assertEquals(PEDIDO_EFETUADO, pedidoQueueEfetuado.getName());
    }
}
