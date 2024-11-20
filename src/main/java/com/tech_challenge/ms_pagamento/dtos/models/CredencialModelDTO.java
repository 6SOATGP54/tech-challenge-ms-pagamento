package com.tech_challenge.ms_pagamento.dtos.models;

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

}
