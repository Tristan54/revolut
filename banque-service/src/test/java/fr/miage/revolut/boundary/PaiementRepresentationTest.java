package fr.miage.revolut.boundary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.miage.revolut.dto.input.*;
import fr.miage.revolut.entity.*;
import fr.miage.revolut.service.ConversionService;
import fr.miage.revolut.service.Generator;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations= "classpath:application-test.properties")
@DirtiesContext
class PaiementRepresentationTest {

	@LocalServerPort
	int port;

	@Autowired
	CompteRessource compteRessource;

	@Autowired
	CarteRessource carteRessource;

    @Autowired
    OperationCarteRessource operationCarteRessource;

    @Autowired
    OperationRessource operationRessource;

	@MockBean
	ConversionService conversionService;

    private String access_token = null;
	private Compte compte;
	private Carte carte;
	private Carte carteVirtuelle;

	@BeforeEach
	public void setupContext() {
		RestAssured.port = 8080;

		compte = new Compte("9a6ebcef-8ed7-4185-b6d1-301194d79051","Nom", "Prénom", LocalDate.parse("1999-11-12"), "France", "FR98156470", "+339136735", Generator.generateIban("FR"), BigDecimal.valueOf(0));
        compte.credit(BigDecimal.valueOf(1000));
		compteRessource.save(compte);

		carte = new Carte(UUID.randomUUID().toString(), Generator.generateNumeroCarte(), Generator.generateCodeCarte(), Generator.generateCryptogrammeCarte(), 1000, false, false, true, true, false, compte);
		carteVirtuelle = new Carte(UUID.randomUUID().toString(), Generator.generateNumeroCarte(), Generator.generateCodeCarte(), Generator.generateCryptogrammeCarte(), 1500, false, false, true, false, true, compte);

		carteRessource.save(carte);
		carteRessource.save(carteVirtuelle);
	}

    @AfterEach
    public void endContext() {
        operationCarteRessource.deleteAll();
        operationRessource.deleteAll();
        carteRessource.deleteAll();
        compteRessource.deleteAll();
    }

	@Test
	public void postPayer() throws Exception{
		PaiementInput paiementInput = new PaiementInput(carte.getNumero(), carte.getCryptogramme(), carte.getCode(), compte.getPays(), "commerçant", Generator.generateIban("FR"), LocalDateTime.now(), BigDecimal.valueOf(100), carte.isSansContact(), "carte");

		org.mockito.Mockito.when(conversionService.conversion("France", "France")).thenReturn(BigDecimal.ONE);

		Response response = given()
				.body(this.toJsonString(paiementInput))
				.contentType(ContentType.JSON)
				.when()
				.post("/paiements")
				.then()
				.statusCode(HttpStatus.SC_OK)
				.extract()
				.response();
	}

    @Test
    public void postPayerVirtuelle() throws Exception{
        PaiementInput paiementInput = new PaiementInput(carteVirtuelle.getNumero(), carteVirtuelle.getCryptogramme(), carteVirtuelle.getCode(), compte.getPays(), "commerçant", Generator.generateIban("FR"), LocalDateTime.now(), BigDecimal.valueOf(100), carteVirtuelle.isSansContact(), "carte");

        org.mockito.Mockito.when(conversionService.conversion("France", "France")).thenReturn(BigDecimal.ONE);

        Response response = given()
                .body(this.toJsonString(paiementInput))
                .contentType(ContentType.JSON)
                .when()
                .post("/paiements")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        Carte carte2Test = carteRessource.getById(carteVirtuelle.getUuid());
        assertTrue(carte2Test.isSupprime());
    }

    @Test
    public void postPayerBadRequestNumeroCarte() throws Exception{
        PaiementInput paiementInput = new PaiementInput("1524378645368975", carte.getCryptogramme(), carte.getCode(), compte.getPays(), "commerçant", Generator.generateIban("FR"), LocalDateTime.now(), BigDecimal.valueOf(100), carte.isSansContact(), "carte");

        org.mockito.Mockito.when(conversionService.conversion("France", "France")).thenReturn(BigDecimal.ONE);

        Response response = given()
                .body(this.toJsonString(paiementInput))
                .contentType(ContentType.JSON)
                .when()
                .post("/paiements")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract()
                .response();

        assertEquals(response.asString(), "Utilisateur introuvable");
    }

    @Test
    public void postPayerBadRequestCryptogramme() throws Exception{
        PaiementInput paiementInput = new PaiementInput(carte.getNumero(), "000", carte.getCode(), compte.getPays(), "commerçant", Generator.generateIban("FR"), LocalDateTime.now(), BigDecimal.valueOf(100), carte.isSansContact(), "carte");

        org.mockito.Mockito.when(conversionService.conversion("France", "France")).thenReturn(BigDecimal.ONE);

        Response response = given()
                .body(this.toJsonString(paiementInput))
                .contentType(ContentType.JSON)
                .when()
                .post("/paiements")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract()
                .response();

        assertEquals(response.asString(), "Cryptogramme invalide");
    }

    @Test
    public void postPayerBadRequestCode() throws Exception{
        PaiementInput paiementInput = new PaiementInput(carte.getNumero(), carte.getCryptogramme(), "0000", compte.getPays(), "commerçant", Generator.generateIban("FR"), LocalDateTime.now(), BigDecimal.valueOf(100), carte.isSansContact(), "carte");

        org.mockito.Mockito.when(conversionService.conversion("France", "France")).thenReturn(BigDecimal.ONE);

        Response response = given()
                .body(this.toJsonString(paiementInput))
                .contentType(ContentType.JSON)
                .when()
                .post("/paiements")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract()
                .response();

        assertEquals(response.asString(), "Code invalide");
    }

    @Test
    public void postPayerBadRequestBloque() throws Exception{
        PaiementInput paiementInput = new PaiementInput(carte.getNumero(), carte.getCryptogramme(), carte.getCode(), compte.getPays(), "commerçant", Generator.generateIban("FR"), LocalDateTime.now(), BigDecimal.valueOf(100), carte.isSansContact(), "carte");

        carte.setBloque(true);
        carteRessource.save(carte);

        org.mockito.Mockito.when(conversionService.conversion("France", "France")).thenReturn(BigDecimal.ONE);

        Response response = given()
                .body(this.toJsonString(paiementInput))
                .contentType(ContentType.JSON)
                .when()
                .post("/paiements")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract()
                .response();

        assertEquals(response.asString(), "La carte est bloquée");
    }

    @Test
    public void postPayerBadRequestSupprime() throws Exception{
        PaiementInput paiementInput = new PaiementInput(carte.getNumero(), carte.getCryptogramme(), carte.getCode(), compte.getPays(), "commerçant", Generator.generateIban("FR"), LocalDateTime.now(), BigDecimal.valueOf(100), carte.isSansContact(), "carte");

        carte.setSupprime(true);
        carteRessource.save(carte);

        org.mockito.Mockito.when(conversionService.conversion("France", "France")).thenReturn(BigDecimal.ONE);

        Response response = given()
                .body(this.toJsonString(paiementInput))
                .contentType(ContentType.JSON)
                .when()
                .post("/paiements")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract()
                .response();

        assertEquals(response.asString(), "La carte n'existe plus");
    }

    @Test
    public void postPayerBadRequestPlafond() throws Exception{
        PaiementInput paiementInput = new PaiementInput(carte.getNumero(), carte.getCryptogramme(), carte.getCode(), compte.getPays(), "commerçant", Generator.generateIban("FR"), LocalDateTime.now(), BigDecimal.valueOf(100000), carte.isSansContact(), "carte");

        org.mockito.Mockito.when(conversionService.conversion("France", "France")).thenReturn(BigDecimal.ONE);

        Response response = given()
                .body(this.toJsonString(paiementInput))
                .contentType(ContentType.JSON)
                .when()
                .post("/paiements")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract()
                .response();

        assertEquals(response.asString(), "Le montant de l'opération dépasse le plafond de la carte");
    }

    @Test
    public void postPayerBadRequestPlafondAvecOperation() throws Exception{
        PaiementInput paiementInput = new PaiementInput(carte.getNumero(), carte.getCryptogramme(), carte.getCode(), compte.getPays(), "commerçant", Generator.generateIban("FR"), LocalDateTime.now(), BigDecimal.valueOf(100), carte.isSansContact(), "carte");

        Operation operation1 = new Operation(UUID.randomUUID().toString(), compte.getUuid(), LocalDateTime.now(), "carte de 800", BigDecimal.valueOf(800), BigDecimal.valueOf(1), "test", "test", "carte", "France");
        Operation operation2 = new Operation(UUID.randomUUID().toString(), compte.getUuid(), LocalDateTime.now(), "carte de 150", BigDecimal.valueOf(150), BigDecimal.valueOf(1), "test2", "test", "carte", "France");

        operationRessource.save(operation1);
        operationRessource.save(operation2);

        operationCarteRessource.save(new PivotOperationCarte(new OperationCarte(operation1, carte)));
        operationCarteRessource.save(new PivotOperationCarte(new OperationCarte(operation2, carte)));

        org.mockito.Mockito.when(conversionService.conversion("France", "France")).thenReturn(BigDecimal.ONE);

        Response response = given()
                .body(this.toJsonString(paiementInput))
                .contentType(ContentType.JSON)
                .when()
                .post("/paiements")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract()
                .response();

        assertEquals(response.asString(), "Le montant de l'opération dépasse le plafond de la carte");
    }

    @Test
    public void postPayePlafondAvecOperation() throws Exception{
        PaiementInput paiementInput = new PaiementInput(carte.getNumero(), carte.getCryptogramme(), carte.getCode(), compte.getPays(), "commerçant", Generator.generateIban("FR"), LocalDateTime.now(), BigDecimal.valueOf(100), carte.isSansContact(), "carte");

        Operation operation1 = new Operation(UUID.randomUUID().toString(), compte.getUuid(), LocalDateTime.now().minusMonths(1), "carte de 800", BigDecimal.valueOf(800), BigDecimal.valueOf(1), "test", "test", "carte", "France");
        Operation operation2 = new Operation(UUID.randomUUID().toString(), compte.getUuid(), LocalDateTime.now(), "carte de 150", BigDecimal.valueOf(150), BigDecimal.valueOf(1), "test2", "test", "carte", "France");

        operationRessource.save(operation1);
        operationRessource.save(operation2);

        operationCarteRessource.save(new PivotOperationCarte(new OperationCarte(operation1, carte)));
        operationCarteRessource.save(new PivotOperationCarte(new OperationCarte(operation2, carte)));

        org.mockito.Mockito.when(conversionService.conversion("France", "France")).thenReturn(BigDecimal.ONE);

        Response response = given()
                .body(this.toJsonString(paiementInput))
                .contentType(ContentType.JSON)
                .when()
                .post("/paiements")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
    }

    @Test
    public void postPayerBadRequestLocalisation() throws Exception{
        PaiementInput paiementInput = new PaiementInput(carte.getNumero(), carte.getCryptogramme(), carte.getCode(), "United Kingdom", "commerçant", Generator.generateIban("FR"), LocalDateTime.now(), BigDecimal.valueOf(100), carte.isSansContact(), "carte");

        org.mockito.Mockito.when(conversionService.conversion("United Kingdom", "France")).thenReturn(BigDecimal.ONE);

        Response response = given()
                .body(this.toJsonString(paiementInput))
                .contentType(ContentType.JSON)
                .when()
                .post("/paiements")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract()
                .response();

        assertEquals(response.asString(), "L'opération à lieu dans un pays différent de celui de compte et la carte n'autorise pas les opérations à l'étranger");
    }

    @Test
    public void postPayerBadRequestSansContact() throws Exception{
        PaiementInput paiementInput = new PaiementInput(carteVirtuelle.getNumero(), carteVirtuelle.getCryptogramme(), carteVirtuelle.getCode(), compte.getPays(), "commerçant", Generator.generateIban("FR"), LocalDateTime.now(), BigDecimal.valueOf(100), true, "carte");

        org.mockito.Mockito.when(conversionService.conversion("United Kingdom", "France")).thenReturn(BigDecimal.ONE);

        Response response = given()
                .body(this.toJsonString(paiementInput))
                .contentType(ContentType.JSON)
                .when()
                .post("/paiements")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract()
                .response();

        assertEquals(response.asString(), "Le sans contact n'est pas autorisé sur cette carte");
    }

	private String toJsonString(Object o) throws Exception {

		ObjectMapper map = new ObjectMapper()
				.registerModule(new JavaTimeModule());
		return map.writeValueAsString(o);
	}
}