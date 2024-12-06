package com.bank.AccountMs.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class ClienteClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${customerms.url}")
    private String customerMsUrl;

    public boolean existeCliente(Integer clienteId) {
        try {
            String url = customerMsUrl + "/clientes/" + clienteId;
            restTemplate.getForObject(url, String.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}