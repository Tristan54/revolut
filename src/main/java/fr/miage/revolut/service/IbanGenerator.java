package fr.miage.revolut.service;

import org.apache.commons.lang3.RandomStringUtils;

public class IbanGenerator {

    public static String generate(String pays){
        String[] indice = pays.split("");

        return indice[0].toUpperCase() + indice[1].toUpperCase() + RandomStringUtils.randomAlphanumeric(20).toUpperCase();
    }
}
