package fr.miage.revolut.boundary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.miage.revolut.dto.input.CompteSignIn;
import fr.miage.revolut.dto.input.OperationInput;
import fr.miage.revolut.entity.Compte;
import fr.miage.revolut.entity.Operation;
import fr.miage.revolut.service.ConversionService;
import fr.miage.revolut.service.Generator;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
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
import org.mockito.Mockito.*;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations= "classpath:application-test.properties")
@DirtiesContext
class OperationRepresentationTest {

	@LocalServerPort
	int port;

	@Autowired
	CompteRessource compteRessource;

	@Autowired
	OperationRessource operationRessource;

	@MockBean
	ConversionService conversionService;

    private String access_token = null;
	private Compte compte;

	@BeforeEach
	public void setupContext() {
		RestAssured.port = 8080;
		compteRessource.deleteAll();
		operationRessource.deleteAll();

		compte = new Compte("9a6ebcef-8ed7-4185-b6d1-301194d79051","Nom", "Prénom", LocalDate.parse("1999-11-12"), "France", "FR98156470", "+339136735", Generator.generateIban("FR"), BigDecimal.valueOf(0));
        compte.credit(BigDecimal.valueOf(10000));
		compteRessource.save(compte);

		Operation operation1 = new Operation(UUID.randomUUID().toString(), compte.getUuid(), LocalDateTime.now(), "virement à test de 100", BigDecimal.valueOf(100), BigDecimal.valueOf(0), "test", "test", "virement", "France");
		Operation operation2 = new Operation(UUID.randomUUID().toString(), compte.getUuid(), LocalDateTime.now(), "versement à test de 20", BigDecimal.valueOf(20), BigDecimal.valueOf(0), "test2", "test", "versement", "France");
		Operation operation3 = new Operation(UUID.randomUUID().toString(), compte.getUuid(), LocalDateTime.now(), "virement à test de 80", BigDecimal.valueOf(80), BigDecimal.valueOf(0), "test2", "test", "virement", "France");
		Operation operation4 = new Operation(UUID.randomUUID().toString(), compte.getUuid(), LocalDateTime.now(), "versement à test de 2000", BigDecimal.valueOf(2000), BigDecimal.valueOf(0), "test", "test", "versement", "France");

		operationRessource.save(operation1);
		operationRessource.save(operation2);
		operationRessource.save(operation3);
		operationRessource.save(operation4);
	}

    private Header getHeaderAuthorization() throws Exception {
        if(access_token == null) {
            CompteSignIn compteSignIn = new CompteSignIn();
            compteSignIn.setMotDePasse("password");
            compteSignIn.setNumTel("+339136735");
            Response response = given().body(this.toJsonString(compteSignIn)).contentType(ContentType.JSON).
                    when().post("/comptes/connexion").then().extract().response();
            access_token = response.jsonPath().getString("access_token");
        }
        return new Header("Authorization","bearer "+ access_token);
    }

	@Test
	public void getOperation() throws Exception {
		Operation operation = new Operation(UUID.randomUUID().toString(), compte.getUuid(), LocalDateTime.now(), "une opération", BigDecimal.valueOf(100), BigDecimal.valueOf(0), "test", "test", "virement", "France");
		operationRessource.save(operation);
		Response response = given()
                .header(getHeaderAuthorization())
				.when().get("/comptes/"+compte.getUuid()+"/operations/"+operation.getUuid())
				.then()
				.statusCode(HttpStatus.SC_OK)
				.extract()
				.response();
		String jsonAsString = response.asString();
		assertThat(jsonAsString,containsString("une opération"));
	}

	@Test
	public void getOperationAvecFiltresMontant() throws Exception {
		Response response = given()
				.header(getHeaderAuthorization())
				.when().get("/comptes/"+compte.getUuid()+"/operations?filtres=montant>500")
				.then()
				.statusCode(HttpStatus.SC_OK)
				.extract()
				.response();
		String jsonAsString = response.asString();
		assertThat(jsonAsString,containsString("2000"));
	}

	@Test
	public void getOperationAvecFiltresMontantEgal() throws Exception {
		Response response = given()
				.header(getHeaderAuthorization())
				.when().get("/comptes/"+compte.getUuid()+"/operations?filtres=montant:80")
				.then()
				.statusCode(HttpStatus.SC_OK)
				.extract()
				.response();
		String jsonAsString = response.asString();
		assertThat(jsonAsString,containsString("80"));
	}

	@Test
	public void getOperationAvecFiltresCategorie() throws Exception {
		Response response = given()
				.header(getHeaderAuthorization())
				.when().get("/comptes/"+compte.getUuid()+"/operations?filtres=categorie:virement")
				.then()
				.statusCode(HttpStatus.SC_OK)
				.extract()
				.response();
		String jsonAsString = response.asString();
		assertThat(jsonAsString,containsString("virement à test de 80"));
	}

	@Test
	public void getOperationAvecFiltresEtCompteDifferent() throws Exception {

		Compte compteTest = new Compte(UUID.randomUUID().toString(),"Nom", "Prénom", LocalDate.parse("1999-11-12"), "France", "FR98156470", "+339136735", Generator.generateIban("FR"), BigDecimal.valueOf(0));
		compteTest.credit(BigDecimal.valueOf(10000));
		compteRessource.save(compteTest);

		Operation operation = new Operation(UUID.randomUUID().toString(), compteTest.getUuid(), LocalDateTime.now(), "virement à test de 150", BigDecimal.valueOf(150), BigDecimal.valueOf(0), "test", "test", "virement", "France");
		operationRessource.save(operation);

		Response response = given()
				.header(getHeaderAuthorization())
				.when().get("/comptes/"+compte.getUuid()+"/operations?filtres=montant:150")
				.then()
				.statusCode(HttpStatus.SC_OK)
				.extract()
				.response();
		String jsonAsString = response.asString();

		assertFalse(jsonAsString.contains("150"));
	}

	@Test
	public void getOperationForbidden() throws Exception {
		given().header(getHeaderAuthorization()).when().get("/comptes/12/operations").then().statusCode(HttpStatus.SC_FORBIDDEN);
	}

    @Test
    public void getOperationUnauthorized() throws Exception {
        given().when().get("/comptes/12/operations").then().statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

	@Test
	public void postOperation() throws Exception{
		OperationInput operationInput = new OperationInput("opération", BigDecimal.valueOf(10.0), "créditeur", "iban", "categorie", "France");

		org.mockito.Mockito.when(conversionService.conversion("France", "France")).thenReturn(BigDecimal.ONE);

		Response response = given()
				.header(getHeaderAuthorization())
				.body(this.toJsonString(operationInput))
				.contentType(ContentType.JSON)
				.when()
				.post("/comptes/"+compte.getUuid()+"/operations")
				.then()
				.statusCode(HttpStatus.SC_CREATED)
				.extract()
				.response();

		String location = response.getHeader("Location");
        given().header(getHeaderAuthorization()).when().get(location).then().statusCode(HttpStatus.SC_OK);
	}

	@Test
	public void postOperationTaux() throws Exception{
		OperationInput operationInput = new OperationInput("opération", BigDecimal.valueOf(10.0), "créditeur", "iban", "categorie", "United Kingdom");

		org.mockito.Mockito.when(conversionService.conversion("United Kingdom", "France")).thenReturn(BigDecimal.valueOf(1.14));

		Response response = given()
				.header(getHeaderAuthorization())
				.body(this.toJsonString(operationInput))
				.contentType(ContentType.JSON)
				.when()
				.post("/comptes/"+compte.getUuid()+"/operations")
				.then()
				.statusCode(HttpStatus.SC_CREATED)
				.extract()
				.response();

		String location = response.getHeader("Location");
		Response get = given().header(getHeaderAuthorization()).when().get(location).then().statusCode(HttpStatus.SC_OK).extract().response();
		String jsonAsString = get.asString();

		assertThat(jsonAsString,containsString("11.4"));
	}

	private String toJsonString(Object o) throws Exception {

		ObjectMapper map = new ObjectMapper()
				.registerModule(new JavaTimeModule());
		return map.writeValueAsString(o);
	}
}