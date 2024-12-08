package com.tech_challenge.ms_pagamento.util;



import com.tech_challenge.ms_pagamento.document.sustentacao.Entidade;
import com.tech_challenge.ms_pagamento.repository.CredenciaisIntegracaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
public abstract class Utils {

    @Autowired
    CredenciaisIntegracaoRepository credenciaisIntegracaoRepository;

    public static  <T extends Entidade> void prePersistPreUpdate(T entity) {
        if (entity.getDataCriacao() == null) {
            entity.prePersistSubClasses();
        } else {
            entity.preUpdateSubClasses();
        }
    }

}
