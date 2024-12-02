package com.tech_challenge.ms_pagamento.conf;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PagamentoAMQPConfiguration {

    public static final String PEDIDO_EX = "pedido.ex";
    public static final String PEDIDO_EFETUADO = "pedido.efetuado";
    public static final String PAGAMENTO_QRCODE = "pagamento.qrcode";

    @Bean
    public RabbitAdmin criaRabbitAdmin(ConnectionFactory conn) {
        return new RabbitAdmin(conn);
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> inicializaAdmin(RabbitAdmin rabbitAdmin){
        return event -> rabbitAdmin.initialize();
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter){

        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);

        return rabbitTemplate;
    }

    @Bean
    public Queue filaQR(){
        return QueueBuilder.durable(PAGAMENTO_QRCODE).build();
    }

    @Bean
    public Queue filaPagamentoConcluido(){
        return QueueBuilder.durable("pagamento.concluido").build();
    }

    @Bean
    public Queue filaPreparacao(){
        return QueueBuilder.durable("preparacao.iniciar")
                .build();
    }

    @Bean
    public FanoutExchange fanoutExchangePagamento(){
        return ExchangeBuilder.fanoutExchange("pagamento.ex").build();
    }

    @Bean
    public Binding bindingPagamentoConcluido(){
        return BindingBuilder.bind(filaPagamentoConcluido()).to(fanoutExchangePagamento());
    }


    @Bean
    public Queue pedidoQueueEfetuado(){
        return QueueBuilder.durable(PEDIDO_EFETUADO)
                .build();
    }

}
