package com.tech_challenge.ms_pagamento.document;

import com.tech_challenge.ms_pagamento.document.sustentacao.DiaDaSemana;
import com.tech_challenge.ms_pagamento.document.sustentacao.Entidade;
import com.tech_challenge.ms_pagamento.document.sustentacao.Location;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Document
public class EscopoLojaMercadoPago extends Entidade {

    private String name;

    private List<DiaDaSemana> businessHours;

    private Location location;

    private String externalId;

    @Field(name = "user_id")
    private String userId;

    private String credenciaisId;

}
