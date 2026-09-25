package com.example.crud.service;

import com.example.crud.infra.exception.CepNotFoundException;
import com.example.crud.infra.exception.InvalidCepException;
import com.example.crud.infra.exception.ViaCepUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ViaCepServiceTest {

    private MockRestServiceServer server;
    private ViaCepService service;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.createServer(restTemplate);
        service = new ViaCepService(restTemplate, "https://viacep.com.br/ws");
    }

    @Test
    void shouldReturnTheCityForAValidCep() {
        server.expect(once(), requestTo("https://viacep.com.br/ws/08773380/json/"))
                .andRespond(withSuccess(
                        "{\"cep\":\"08773-380\",\"localidade\":\"Mogi das Cruzes\",\"uf\":\"SP\"}",
                        MediaType.APPLICATION_JSON
                ));

        var response = service.findAddress("08773-380");

        assertEquals("Mogi das Cruzes", response.localidade());
        server.verify();
    }

    @Test
    void shouldRejectAMalformedCepWithoutCallingViaCep() {
        assertThrows(InvalidCepException.class, () -> service.findAddress("123"));
        server.verify();
    }

    @Test
    void shouldReportANonexistentCep() {
        server.expect(once(), requestTo("https://viacep.com.br/ws/00000000/json/"))
                .andRespond(withSuccess("{\"erro\":true}", MediaType.APPLICATION_JSON));

        assertThrows(CepNotFoundException.class, () -> service.findAddress("00000000"));
        server.verify();
    }

    @Test
    void shouldHandleViaCepFailure() {
        server.expect(once(), requestTo("https://viacep.com.br/ws/08773380/json/"))
                .andRespond(withServerError());

        assertThrows(ViaCepUnavailableException.class, () -> service.findAddress("08773380"));
        server.verify();
    }
}
