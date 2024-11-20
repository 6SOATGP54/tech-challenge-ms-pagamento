package com.tech_challenge.ms_pagamento.repository;


import com.tech_challenge.ms_pagamento.document.EscopoLojaMercadoPago;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LojaMercadoLivreRepository extends MongoRepository<EscopoLojaMercadoPago,String> {

    EscopoLojaMercadoPago findByUserId(String userId);
}
