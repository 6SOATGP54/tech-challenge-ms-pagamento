package com.tech_challenge.ms_pagamento.dtos;

public record CaixaDTO(Long category,
                       String external_id,
                       String external_store_id,
                       Boolean fixed_amount,
                       String name, @lombok.NonNull String store_id) {
}
