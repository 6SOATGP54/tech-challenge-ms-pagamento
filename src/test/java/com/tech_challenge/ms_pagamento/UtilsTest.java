package com.tech_challenge.ms_pagamento;

import com.tech_challenge.ms_pagamento.document.sustentacao.Entidade;
import com.tech_challenge.ms_pagamento.util.Utils;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    // Testa o comportamento quando a entidade ainda não foi persistida (dataCriacao é null)
    @Test
    void testPrePersist() {
        // Criar uma instância mock da Entidade
        Entidade entidadeMock = mock(Entidade.class);

        // Simular o comportamento quando getDataCriacao retornar null
        when(entidadeMock.getDataCriacao()).thenReturn(null);

        // Chamar o metodo estatico
        Utils.prePersistPreUpdate(entidadeMock);

        // Verificar se o metodo prePersistSubClasses foi chamado
        verify(entidadeMock, times(1)).prePersistSubClasses();
        verify(entidadeMock, never()).preUpdateSubClasses();
    }

    // Testa o comportamento quando a entidade já foi persistida (dataCriacao não é null)
    @Test
    void testPreUpdate() {
        // Criar uma instância mock da Entidade
        Entidade entidadeMock = mock(Entidade.class);

        // Simular o comportamento quando getDataCriacao retornar um valor não nulo
        when(entidadeMock.getDataCriacao()).thenReturn(LocalDateTime.parse("2024-12-08T10:15:30")); // Qualquer valor não nulo

        // Chamar o metodo estatico
        Utils.prePersistPreUpdate(entidadeMock);

        // Verificar se o metodo preUpdateSubClasses foi chamado
        verify(entidadeMock, never()).prePersistSubClasses();
        verify(entidadeMock, times(1)).preUpdateSubClasses();
    }
}
