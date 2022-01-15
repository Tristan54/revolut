package fr.miage.commercantservice.boundary;

import feign.Response;
import fr.miage.commercantservice.entity.Paiement;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(value = "banque-service", url = "${app.banque.service.url}")
public interface BanqueService {

    @RequestMapping(method = RequestMethod.POST, value = "/paiements", consumes = "application/json")
    ResponseEntity<String> payerParCarte(@RequestBody Paiement paiement);
}
