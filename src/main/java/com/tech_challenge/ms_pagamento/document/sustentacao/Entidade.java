package com.tech_challenge.ms_pagamento.document.sustentacao;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Getter
@Setter
public class Entidade {

    @Id
    private String id;

    private LocalDateTime dataCriacao;

    private LocalDateTime dataAtualizacao;

    protected void customSubClasses(){

    }

    public void prePersistSubClasses() {
        dataCriacao = LocalDateTime.now();
        customSubClasses();
    }

    public void preUpdateSubClasses() {
        dataAtualizacao = LocalDateTime.now();
        customSubClasses();
    }
}
