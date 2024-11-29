package com.tech_challenge.ms_pagamento.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public record OrdemVendaMercadoPagoDTO(String description,
                                       String external_reference,
                                       List<ItemOrdemVendaDTO> items,
                                       String title,
                                       BigDecimal total_amount,
                                       String notification_url) {
}
