package me.iiaii.completablefuture.config;

import org.springframework.web.client.RestTemplate;

public class ApiCaller {

    private final String baseUrl;

    private final RestTemplate restTemplate;

    public ApiCaller(final String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

    public <T> T fetch(final String uri, Class<T> returnType) {
        return restTemplate.getForEntity(baseUrl + uri, returnType)
                .getBody();
    }

}
