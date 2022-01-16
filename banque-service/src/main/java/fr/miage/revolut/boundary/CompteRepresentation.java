package fr.miage.revolut.boundary;

import fr.miage.revolut.assembler.CompteAssembler;
import fr.miage.revolut.dto.input.CompteInput;
import fr.miage.revolut.dto.input.CompteSignIn;
import fr.miage.revolut.dto.output.CompteOutput;
import fr.miage.revolut.entity.Compte;
import fr.miage.revolut.service.CompteService;
import org.keycloak.representations.AccessTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.core.HttpHeaders;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value="/api/v1/comptes", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Compte.class)
public class CompteRepresentation {

    private static final Logger log = LoggerFactory.getLogger(CompteRepresentation.class);

    @Autowired
    private Environment env;

    private final CompteService service;
    private final CompteAssembler assembler;

    public CompteRepresentation(CompteService service, CompteAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping(value="/{compteId}")
    @PreAuthorize(value = "authentication.name.equals(#id)")
    public ResponseEntity<EntityModel<CompteOutput>> getOneCompte(@PathVariable("compteId") String id) {
        return Optional.ofNullable(service.findById(id)).filter(Optional::isPresent)
                .map(c -> ResponseEntity.ok(assembler.toModel(c.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping()
    @Transactional
    public ResponseEntity<URI> createCompte(@RequestBody @Valid CompteInput compte) {
        String id = service.createCompte(compte);

        if(id.isEmpty()){
            Compte saved = service.findByNumTel(compte.getNumTel());
            URI location = linkTo(CompteRepresentation.class).slash(saved.getUuid()).toUri();
            return ResponseEntity.status(HttpStatus.CONFLICT).header(HttpHeaders.LOCATION, location.toString()).build();
        }else{
            Compte saved = service.save(compte, id);
            URI location = linkTo(CompteRepresentation.class).slash(saved.getUuid()).toUri();
            return ResponseEntity.created(location).build();
        }

    }

    @PostMapping(path = "/connexion")
    public ResponseEntity<?> connexion(@RequestBody @Valid CompteSignIn compte) {

        AccessTokenResponse response;
        try {
            response = service.connexion(compte);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Identifiant ou mot de passe incorrect");
        }

    }

}
