package fr.miage.revolut.service;

import org.apache.commons.lang3.RandomStringUtils;

public class Generator {

    public static String generateIban(String pays){
        String[] indice = pays.split("");

        return indice[0].toUpperCase() + indice[1].toUpperCase() + RandomStringUtils.randomAlphanumeric(20).toUpperCase();
    }

    public static String generateNumeroCarte(){
        return RandomStringUtils.randomNumeric(16);
    }

    public static String  generateCodeCarte(){
        return RandomStringUtils.randomNumeric(4);
    }

    public static String generateCryptogrammeCarte(){
        return RandomStringUtils.randomNumeric(3);
    }
}
