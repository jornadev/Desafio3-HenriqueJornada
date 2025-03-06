package com.compass.model;

import lombok.Data;

@Data
public class Address {
    private String cep;
    private String logradouro;
    private String bairro;
    private String localidade;
    private String uf;
}
