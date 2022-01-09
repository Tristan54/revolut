package fr.miage.revolut.boundary;

import fr.miage.revolut.entity.Carte;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarteRessource extends JpaRepository<Carte, String>{

    Carte findByNumero(String numero);

}
