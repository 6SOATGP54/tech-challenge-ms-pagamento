package com.tech_challenge.ms_pagamento.dtos;

import java.math.BigDecimal;

public record ItemOrdemVendaDTO(String sku_number,
                                String category,
                                String title,
                                String description,
                                BigDecimal unit_price,
                                int quantity,
                                String unit_measure,
                                BigDecimal total_amount) {
}