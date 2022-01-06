package fr.miage.revolut.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.math.BigDecimal;

@FeignClient(value = "conversion-service", url = "${app.conversion.service.url}")
public interface ConversionService {

    @RequestMapping(method = RequestMethod.GET, value = "/conversion/source/{source}/cible/{cible}", consumes = "application/json")
    BigDecimal conversion(@PathVariable("source") String source, @PathVariable("cible") String cible);
}
