package fr.miage.revolut.boundary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.miage.revolut.dto.input.*;
import fr.miage.revolut.entity.Carte;
import fr.miage.revolut.entity.Compte;
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

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


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

	@MockBean
	ConversionService conversionService;

    private String access_token = null;
	private Compte compte;
	private Carte carte;
	private Carte carteVirtuelle;

	@BeforeEach
	public void setupContext() {
		RestAssured.port = 8080;
		carteRessource.deleteAll();
		compteRessource.deleteAll();

		compte = new Compte("9a6ebcef-8ed7-4185-b6d1-301194d79051","Nom", "Prénom", LocalDate.parse("1999-11-12"), "France", "FR98156470", "+339136735", Generator.generateIban("FR"), BigDecimal.valueOf(0));
        compte.credit(BigDecimal.valueOf(1000));
		compteRessource.save(compte);

		carte = new Carte(UUID.randomUUID().toString(), Generator.generateNumeroCarte(), Generator.generateCodeCarte(), Generator.generateCryptogrammeCarte(), 1000, false, false, true, true, false, compte);
		carteVirtuelle = new Carte(UUID.randomUUID().toString(), Generator.generateNumeroCarte(), Generator.generateCodeCarte(), Generator.generateCryptogrammeCarte(), 1500, false, false, true, true, true, compte);

		carteRessource.save(carte);
		carteRessource.save(carteVirtuelle);
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
		String jsonAsString = response.asString();
		assertThat(jsonAsString,containsString("fait"));
	}

	private String toJsonString(Object o) throws Exception {

		ObjectMapper map = new ObjectMapper()
				.registerModule(new JavaTimeModule());
		return map.writeValueAsString(o);
	}
}