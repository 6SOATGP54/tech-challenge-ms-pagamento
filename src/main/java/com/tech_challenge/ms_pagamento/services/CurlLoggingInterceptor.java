package com.tech_challenge.ms_pagamento.services;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CurlLoggingInterceptor implements ClientHttpRequestInterceptor {


    private String generateCurl(HttpRequest request, byte[] body) {
        StringBuilder curlCmd = new StringBuilder();

        // Adiciona o método HTTP e a URL
        curlCmd.append("curl -X ").append(request.getMethod().name()).append(" '").append(request.getURI()).append("'");

        // Adiciona os headers
        request.getHeaders().forEach((key, value) ->
                value.forEach(val -> curlCmd.append(" -H '").append(key).append(": ").append(val).append("'"))
        );

        // Adiciona o corpo da requisição, se houver
        if (body != null && body.length > 0) {
            String bodyContent = new String(body, StandardCharsets.UTF_8);
            curlCmd.append(" -d '").append(bodyContent).append("'");
        }

        return curlCmd.toString();
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // Gera o comando curl com base na requisição
        String curlCmd = generateCurl(request, body);
        System.out.println("Curl: " + curlCmd);

        // Prossegue com a execução da requisição
        return execution.execute(request, body);
    }
}
