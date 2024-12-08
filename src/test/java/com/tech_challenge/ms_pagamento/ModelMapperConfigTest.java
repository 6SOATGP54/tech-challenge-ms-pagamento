package com.tech_challenge.ms_pagamento;

import com.tech_challenge.ms_pagamento.conf.ModelMapperConfig;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfiguration
@Import(ModelMapperConfig.class)
public class ModelMapperConfigTest {

    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void testModelMapperBean() {
        modelMapper = new ModelMapper();
        assertNotNull(modelMapper, "O bean ModelMapper deve ser injetado corretamente.");
    }
}
