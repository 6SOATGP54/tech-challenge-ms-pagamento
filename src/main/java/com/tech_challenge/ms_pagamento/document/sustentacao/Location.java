package com.tech_challenge.ms_pagamento.document.sustentacao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Location {

    private String streetNumber;

    private String streetName;

    private String cityName;

    private String stateName;

    private double latitude;

    private double longitude;

    private String reference;

}
