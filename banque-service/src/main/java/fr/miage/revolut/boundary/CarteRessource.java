package fr.miage.revolut.boundary;

import fr.miage.revolut.entity.Carte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarteRessource extends JpaRepository<Carte, String>{

    Carte findByNumero(String numero);

    List<Carte> findByCompte_Uuid(String uuid);

}
