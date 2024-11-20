package com.tech_challenge.ms_pagamento.document;

import com.tech_challenge.ms_pagamento.document.sustentacao.Entidade;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("escopo_caixa")
public class EscopoCaixaMercadoPago extends Entidade {

    private Long category;

    @NonNull
    private String external_id;

    private String external_store_id;

    private Boolean fixed_amount;

    private String name;

    @NonNull
    private String store_id;

    private String idAPI;

    @Override
    protected void customSubClasses() {
        setCategory(5611203L);
        setFixed_amount(Boolean.FALSE);
    }
}
