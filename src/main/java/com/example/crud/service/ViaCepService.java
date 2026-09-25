package com.example.crud.service;

import com.example.crud.domain.address.ViaCepResponse;
import com.example.crud.infra.exception.CepNotFoundException;
import com.example.crud.infra.exception.InvalidCepException;
import com.example.crud.infra.exception.ViaCepUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class ViaCepService {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ViaCepService(RestTemplate restTemplate, @Value("${viacep.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public ViaCepResponse findAddress(String cep) {
        String normalizedCep = normalizeCep(cep);

        try {
            var response = restTemplate.getForEntity(
                    baseUrl + "/{cep}/json/",
                    ViaCepResponse.class,
                    normalizedCep
            );

            HttpStatusCode status = response.getStatusCode();
            ViaCepResponse address = response.getBody();

            if (!status.is2xxSuccessful() || address == null) {
                throw new ViaCepUnavailableException("A ViaCEP retornou uma resposta inválida");
            }
            if (address.notFound()) {
                throw new CepNotFoundException(normalizedCep);
            }
            if (address.localidade() == null || address.localidade().isBlank()) {
                throw new ViaCepUnavailableException("A ViaCEP não informou a cidade do CEP");
            }

            return address;
        } catch (CepNotFoundException | ViaCepUnavailableException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new ViaCepUnavailableException("Não foi possível consultar a ViaCEP", exception);
        }
    }

    private String normalizeCep(String cep) {
        if (cep == null || !cep.trim().matches("\\d{5}-?\\d{3}")) {
            throw new InvalidCepException();
        }
        return cep.replace("-", "");
    }
}
