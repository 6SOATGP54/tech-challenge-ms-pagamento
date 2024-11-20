package com.tech_challenge.ms_pagamento.document;

import com.tech_challenge.ms_pagamento.document.sustentacao.Entidade;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Document
public class CredenciaisAcesso extends Entidade {

    private ServicosIntegracao tipoIntegracao;

    private String token;

    private String usuario;

    private String webHook;

    @Override
    public void prePersistSubClasses() {
        super.prePersistSubClasses();
    }

    @Override
    public void preUpdateSubClasses() {
        super.preUpdateSubClasses();
    }

    public enum ServicosIntegracao{
        MERCADO_PAGO
    }
}
