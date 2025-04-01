package com.example.crud.service;

import com.example.crud.domain.product.Product;
import com.example.crud.domain.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.crud.domain.address.Address;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AddressFinder {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;

    public AddressFinder(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public boolean findAddress(String id, String cep) {
        String url = "https://viacep.com.br/ws/{cep}/json/";

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("cep", cep);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, uriVariables);
        try {
            Address address = objectMapper.readValue(response.getBody(), Address.class);
            String city = address.getLocalidade();
            Optional<Product> product = productRepository.findById(id);
            if(product.isPresent()){
                Product foundProduct = product.get();
                return foundProduct.getDistribution_center().equals(city);
            }
            return false;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
    }
}
