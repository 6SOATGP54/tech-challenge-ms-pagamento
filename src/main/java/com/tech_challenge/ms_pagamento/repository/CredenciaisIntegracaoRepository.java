package com.tech_challenge.ms_pagamento.repository;


import com.tech_challenge.ms_pagamento.document.CredenciaisAcesso;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CredenciaisIntegracaoRepository extends MongoRepository<CredenciaisAcesso,String> {

}
