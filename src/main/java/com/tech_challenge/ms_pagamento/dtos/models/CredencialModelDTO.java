package com.tech_challenge.ms_pagamento.dtos.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tech_challenge.ms_pagamento.document.CredenciaisAcesso;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CredencialModelDTO {

    private String token;

    private String usuario;

    private String webHook;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private CredenciaisAcesso.ServicosIntegracao tipoIntegracao;

}
