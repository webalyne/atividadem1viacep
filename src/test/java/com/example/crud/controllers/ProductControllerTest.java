package com.example.crud.controllers;

import com.example.crud.service.AvailabilityService;
import com.example.crud.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private AvailabilityService availabilityService;

    @Test
    void shouldReturnAvailability() throws Exception {
        when(availabilityService.isAvailable("p1", "08773380")).thenReturn(true);

        mockMvc.perform(get("/product/availability/p1").param("cep", "08773380"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void shouldValidateProductInput() throws Exception {
        mockMvc.perform(post("/product")
                        .contentType("application/json")
                        .content("{\"name\":\"\",\"price\":0,\"category\":\"\",\"distributionCenter\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dados de entrada inválidos"))
                .andExpect(jsonPath("$.fields.name").exists())
                .andExpect(jsonPath("$.fields.price").exists())
                .andExpect(jsonPath("$.fields.category").exists())
                .andExpect(jsonPath("$.fields.distributionCenter").exists());
    }
}
