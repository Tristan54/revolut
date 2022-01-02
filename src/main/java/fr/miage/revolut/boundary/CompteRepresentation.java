package fr.miage.revolut.boundary;

import fr.miage.revolut.dto.input.CompteSignIn;
import fr.miage.revolut.dto.input.OperationInput;
import fr.miage.revolut.entity.Compte;
import fr.miage.revolut.dto.input.CompteInput;
import fr.miage.revolut.dto.validator.CompteValidator;
import fr.miage.revolut.assembler.CompteAssembler;
import fr.miage.revolut.entity.Operation;
import fr.miage.revolut.service.CompteService;
import fr.miage.revolut.service.IbanGenerator;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value="/comptes", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Compte.class)
public class CompteRepresentation {

    private static final Logger log = LoggerFactory.getLogger(CompteRepresentation.class);

    @Autowired
    private Environment env;

    private final CompteService service;
    private final CompteAssembler assembler;
    private final CompteValidator validator;

    public CompteRepresentation(CompteService service, CompteAssembler assembler, CompteValidator validator) {
        this.service = service;
        this.assembler = assembler;
        this.validator = validator;
    }

    @GetMapping(value="/{compteId}")
    @PreAuthorize(value = "authentication.name.equals(#id)")
    public ResponseEntity<?> getOneCompte(@PathVariable("compteId") String id) {
        return Optional.ofNullable(service.findById(id)).filter(Optional::isPresent)
                .map(c -> ResponseEntity.ok(assembler.toModel(c.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping()
    @Transactional
    public ResponseEntity<?> createCompte(@RequestBody @Valid CompteInput compte) {

        Keycloak keycloak = KeycloakBuilder.builder().serverUrl(env.getProperty("keycloak.auth-server-url"))
                .grantType(OAuth2Constants.PASSWORD).realm("master").clientId("admin-cli")
                .username(env.getProperty("app.keycloak.username")).password(env.getProperty("app.keycloak.password"))
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()).build();

        keycloak.tokenManager().getAccessToken();

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(compte.getNumTel());
        user.setFirstName(compte.getPrenom());
        user.setLastName(compte.getNom());

        RealmResource realmResource = keycloak.realm(env.getProperty("keycloak.realm"));
        UsersResource usersRessource = realmResource.users();

        Response response = usersRessource.create(user);

        String userId = null;
        if (response.getStatus() == 201) {

            userId = CreatedResponseUtil.getCreatedId(response);

            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(compte.getMotDePasse());

            UserResource userResource = usersRessource.get(userId);

            userResource.resetPassword(passwordCred);

            RoleRepresentation realmRoleUser = realmResource.roles().get(env.getProperty("app.keycloak.role")).toRepresentation();

            userResource.roles().realmLevel().add(Arrays.asList(realmRoleUser));
        }else{
            Compte saved = service.findByNumTel(compte.getNumTel());
            URI location = linkTo(CompteRepresentation.class).slash(saved.getUuid()).toUri();
            return ResponseEntity.status(HttpStatus.CONFLICT).header(HttpHeaders.LOCATION, location.toString()).build();
        }

        Compte compte2Save = new Compte(
                userId,
                compte.getNom(),
                compte.getPrenom(),
                compte.getDateNaissance(),
                compte.getPays(),
                compte.getNumPasseport(),
                compte.getNumTel(),
                IbanGenerator.generate(compte.getPays()),
                0
        );

        Compte saved = service.save(compte2Save);
        URI location = linkTo(CompteRepresentation.class).slash(saved.getUuid()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PostMapping(path = "/connexion")
    public ResponseEntity<?> connexion(@RequestBody @Valid CompteSignIn compte) {

        Map<String, Object> clientCredentials = new HashMap<>();
        clientCredentials.put("secret", env.getProperty("app.keycloak.clientSecret"));
        clientCredentials.put("grant_type", "password");

        Configuration configuration =
                new Configuration(env.getProperty("keycloak.auth-server-url"), env.getProperty("keycloak.realm"), env.getProperty("keycloak.resource"), clientCredentials, null);
        AuthzClient authzClient = AuthzClient.create(configuration);

        AccessTokenResponse response = authzClient.obtainAccessToken(compte.getNumTel(), compte.getMotDePasse());

        return ResponseEntity.ok(response);
    }

}
