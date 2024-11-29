package com.tech_challenge.ms_pagamento.controller;

import com.tech_challenge.ms_pagamento.document.CredenciaisAcesso;
import com.tech_challenge.ms_pagamento.document.EscopoCaixaMercadoPago;
import com.tech_challenge.ms_pagamento.document.EscopoLojaMercadoPago;
import com.tech_challenge.ms_pagamento.dtos.models.CredencialModelDTO;
import com.tech_challenge.ms_pagamento.services.IntegracaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/integracoes/mercadoPago")
public class ConfiguracaoIntegracoes {

    @Autowired
    IntegracaoService integracaoService;


    @PostMapping("/cadastroCredenciais")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Boolean> cadastroCredenciais(@Validated @RequestBody CredencialModelDTO credenciaisAcesso) {
        return ResponseEntity.ok(integracaoService.cadastroCredenciais(credenciaisAcesso));
    }


    @PostMapping("/cadastroLojaMercadoPago")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EscopoLojaMercadoPago> cadastroLojaMercadoLivre(@Validated @RequestBody EscopoLojaMercadoPago escopoLojaMercadoPago) {
        return ResponseEntity.ok(integracaoService.cadastroLojaMercadoLivre(escopoLojaMercadoPago));
    }


    @PostMapping("/cadastrarCaixaLojaMercadoLivre")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EscopoCaixaMercadoPago> cadastrarCaixaLojaMercadoLivre(@Validated @RequestBody EscopoCaixaMercadoPago escopoCaixaMercadoPago) {
        return ResponseEntity.ok(integracaoService.cadastrarCaixaLojaMercadoLivre(escopoCaixaMercadoPago));
    }

    @PostMapping("/pagamentoRecebido")
    @ResponseStatus(HttpStatus.CREATED)
    public void cadastrarCaixaLojaMercadoLivre(@RequestParam("id") Object id,
                                               @RequestParam("topic") Object type) {
        integracaoService.consultarPagamento(id,type);
    }





}
