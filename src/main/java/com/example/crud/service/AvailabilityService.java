package com.example.crud.service;

import com.example.crud.domain.product.Product;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Locale;

@Service
public class AvailabilityService {

    private final ProductService productService;
    private final ViaCepService viaCepService;

    public AvailabilityService(ProductService productService, ViaCepService viaCepService) {
        this.productService = productService;
        this.viaCepService = viaCepService;
    }

    public boolean isAvailable(String productId, String cep) {
        Product product = productService.findActiveById(productId);
        String city = viaCepService.findAddress(cep).localidade();

        return normalize(city).equals(normalize(product.getDistributionCenter()));
    }

    private String normalize(String value) {
        String withoutAccents = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return withoutAccents.trim().toLowerCase(Locale.ROOT);
    }
}
