package com.tech_challenge.ms_pagamento;

import com.tech_challenge.ms_pagamento.document.CredenciaisAcesso;
import com.tech_challenge.ms_pagamento.enums.EndpointsIntegracaoEnum;
import com.tech_challenge.ms_pagamento.services.RequestServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
public class RequestServicesTest {

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        ReflectionTestUtils.setField(RequestServices.class, "restTemplate", restTemplate);
    }

    @Disabled
    void deveRetornarIdParaCriarCaixaOuLoja() throws Exception {
        // Arrange
        CredenciaisAcesso credenciaisAcesso = CredenciaisAcesso.builder()
                .token("mockToken")
                .build();

        String endpoint = "https://mockapi.com/caixa";
        HttpMethod httpMethod = HttpMethod.POST;

        String mockResponseBody = "{ \"body\": { \"id\": 12345 } }";

        mockServer.expect(requestTo(endpoint))
                .andExpect(method(httpMethod))
                .andRespond(withSuccess(mockResponseBody, MediaType.APPLICATION_JSON));

        // Act
        Object result = RequestServices.requestToMercadoPago(
                new Object(), credenciaisAcesso, endpoint, httpMethod, EndpointsIntegracaoEnum.CRIAR_CAIXA
        );

        // Assert
        assertNotNull(result);
        assertEquals(12345L, result);
    }

    @Disabled
    void deveRetornarQrCodeParaGerarQRCode() throws Exception {
        // Arrange
        CredenciaisAcesso credenciaisAcesso = CredenciaisAcesso.builder()
                .token("mockToken")
                .build();

        String endpoint = "https://mockapi.com/qrcode";
        HttpMethod httpMethod = HttpMethod.POST;

        String mockResponseBody = "{ \"body\": { \"qr_data\": \"mockQrCodeData\" } }";

        mockServer.expect(requestTo(endpoint))
                .andExpect(method(httpMethod))
                .andRespond(withSuccess(mockResponseBody, MediaType.APPLICATION_JSON));

        // Act
        Object result = RequestServices.requestToMercadoPago(
                new Object(), credenciaisAcesso, endpoint, httpMethod, EndpointsIntegracaoEnum.GERARQRCODE
        );

        // Assert
        assertNotNull(result);
        assertEquals("mockQrCodeData", result);
    }

    @Disabled
    void deveLancarRuntimeExceptionEmErroDeRequisicao() {
        // Arrange
        CredenciaisAcesso credenciaisAcesso = CredenciaisAcesso.builder()
                .token("mockToken")
                .build();

        String endpoint = "https://mockapi.com/error";
        HttpMethod httpMethod = HttpMethod.POST;

        mockServer.expect(requestTo(endpoint))
                .andExpect(method(httpMethod))
                .andRespond(withServerError());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            RequestServices.requestToMercadoPago(
                    new Object(), credenciaisAcesso, endpoint, httpMethod, EndpointsIntegracaoEnum.CRIAR_CAIXA
            );
        });

        assertEquals("500 Internal Server Error", exception.getMessage());
    }
}
