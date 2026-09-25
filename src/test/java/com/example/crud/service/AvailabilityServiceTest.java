package com.example.crud.service;

import com.example.crud.domain.address.ViaCepResponse;
import com.example.crud.domain.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AvailabilityServiceTest {

    private ProductService productService;
    private ViaCepService viaCepService;
    private AvailabilityService availabilityService;

    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        viaCepService = mock(ViaCepService.class);
        availabilityService = new AvailabilityService(productService, viaCepService);
    }

    @Test
    void shouldReturnTrueWhenCityMatchesDistributionCenter() {
        Product product = new Product();
        product.setDistributionCenter("Mogi das Cruzes");
        when(productService.findActiveById("p1")).thenReturn(product);
        when(viaCepService.findAddress("08773380"))
                .thenReturn(new ViaCepResponse("08773-380", "Mogi das Cruzes", "SP", false));

        assertTrue(availabilityService.isAvailable("p1", "08773380"));
    }

    @Test
    void shouldReturnFalseWhenCityDoesNotMatchDistributionCenter() {
        Product product = new Product();
        product.setDistributionCenter("Recife");
        when(productService.findActiveById("p11")).thenReturn(product);
        when(viaCepService.findAddress("08773380"))
                .thenReturn(new ViaCepResponse("08773-380", "Mogi das Cruzes", "SP", false));

        assertFalse(availabilityService.isAvailable("p11", "08773380"));
    }
}
