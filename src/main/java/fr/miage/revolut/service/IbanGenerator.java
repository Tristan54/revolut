package fr.miage.revolut.service;

import java.util.Random;

public class IbanGenerator {

    public static String generate(String pays){
        Random rand = new Random();
        StringBuilder res = new StringBuilder();

        res.append(pays);

        for (int i = 0; i < 14; i++) {
            res.append(rand.nextInt(10));
        }

        return res.toString();
    }
}
