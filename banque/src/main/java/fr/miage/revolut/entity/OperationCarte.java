package fr.miage.revolut.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class OperationCarte implements Serializable {

    private static final long serialVersionUID = -7657356784064548821L;

    @OneToOne
    @JoinColumn(name = "operation_uuid", referencedColumnName = "uuid", unique = true)
    private Operation operation;

    @ManyToOne
    @JoinColumn(name = "carte_uuid", referencedColumnName = "uuid")
    private Carte carte;
}
