package com.tech_challenge.ms_pagamento.repository;

import com.tech_challenge.ms_pagamento.document.EscopoCaixaMercadoPago;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CaixaMercadoPagoRepository extends MongoRepository<EscopoCaixaMercadoPago,String> {
}
