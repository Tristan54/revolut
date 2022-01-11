package fr.miage.revolut.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Carte {

    @Id
    private String uuid;
    private String numero;
    private int code;
    private int cryptogramme;
    private Integer plafond;
    private boolean bloque;
    private boolean supprime;
    private boolean localisation;
    private boolean sansContact;
    private boolean virtuelle;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "compte_uuid", referencedColumnName = "uuid")
    private Compte compte;
}
