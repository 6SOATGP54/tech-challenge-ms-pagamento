package com.tech_challenge.ms_pagamento.util;


import com.tech_challenge.ms_pagamento.document.sustentacao.Entidade;

public abstract class Utils {

    public static  <T extends Entidade> void prePersistPreUpdate(T entity) {
        if (entity.getId() != null) {
            entity.prePersistSubClasses();
        } else {
            entity.preUpdateSubClasses();
        }
    }
}
