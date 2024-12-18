package com.tech_challenge.ms_pagamento.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record EscopoLojaMercadoPagoDTO (String name,
                                    @JsonProperty("business_hours") Map<Object, Object> businessHours,
                                    Map<Object, Object> location,
                                    @JsonProperty("external_id") String externalId) {
}