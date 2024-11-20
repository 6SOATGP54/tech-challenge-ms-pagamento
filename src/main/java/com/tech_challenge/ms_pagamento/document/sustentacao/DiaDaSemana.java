package com.tech_challenge.ms_pagamento.document.sustentacao;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaDaSemana{

    private Dia dia;

    private List<Intervalo> intervalos;

    public enum Dia {
        monday,
        tuesday,
        wednesday,
        thursday,
        friday,
        Saturday,
        sunday
    }
}
