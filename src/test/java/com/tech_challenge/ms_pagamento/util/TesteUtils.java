package com.tech_challenge.ms_pagamento.util;

import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;

public class TesteUtils {


    public static void waitForContainers(MongoDBContainer mongoDBContainer, RabbitMQContainer rabbitMQContainer) {
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
}

