package fr.miage.revolut.boundary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.miage.revolut.dto.input.CompteInput;
import fr.miage.revolut.dto.input.CompteSignIn;
import fr.miage.revolut.dto.input.OperationInput;
import fr.miage.revolut.entity.Compte;
import fr.miage.revolut.entity.Operation;
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
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;


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

    private String access_token = null;
	private Compte compte;

	@BeforeEach
	public void setupContext() {
		RestAssured.port = 8080;
		compteRessource.deleteAll();
		operationRessource.deleteAll();

		compte = new Compte("42a10afb-b9f1-480c-91f8-df3fa870f127","Nom", "Prénom", LocalDate.parse("1999-11-12"), "France", "FR98156470", "+339136735", Generator.generateIban("FR"), BigDecimal.valueOf(0));
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
		Operation operation = new Operation(UUID.randomUUID().toString(), compte.getUuid(), LocalDateTime.now(), "virement à test", BigDecimal.valueOf(100), BigDecimal.valueOf(0), "test", "test", "virement", "France");
		operationRessource.save(operation);
		Response response = given()
                .header(getHeaderAuthorization())
				.when().get("/comptes/"+compte.getUuid()+"/operations")
				.then()
				.statusCode(HttpStatus.SC_OK)
				.extract()
				.response();
		String jsonAsString = response.asString();
		assertThat(jsonAsString,containsString("test"));
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

	private String toJsonString(Object o) throws Exception {

		ObjectMapper map = new ObjectMapper()
				.registerModule(new JavaTimeModule());
		return map.writeValueAsString(o);
	}
}