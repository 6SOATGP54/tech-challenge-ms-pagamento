package com.tech_challenge.ms_pagamento.util;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class Assembler {

   private static ModelMapper modelMapper;

   @Autowired
    public Assembler(ModelMapper modelMapper) {
        Assembler.modelMapper = modelMapper;
    }

    public static <T> T toEntity(Object obj, Class<T> objTranformado ) {
        return modelMapper.map(obj, objTranformado);
    }

    public static <T> T toModel(Object obj, Class<T> objTranformado ) {
        return modelMapper.map(obj, objTranformado);
    }

}
