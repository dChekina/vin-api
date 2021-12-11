package com.example.romaninsurance.ewa;

import com.example.romaninsurance.model.AutoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class EwaRestClient {

    private static final String AUTO_MTIBU_NUMBER = "/auto/mtibu/number";
    private final RestTemplate restTemplate;

    @Value("${ewa.api.url}/${ewa.api.version}")
    private String ewaApiUrl;

    @Value("${ewa.api.email}")
    private String email;

    @Value("${ewa.api.token}")
    private String token;

    public String vinBy(String autoNumber) {
        URI url = URI.create(ewaApiUrl + AUTO_MTIBU_NUMBER + "?query=" + autoNumber);
        RequestEntity<String> entity = new RequestEntity<>(headers(), HttpMethod.GET, url);

        ResponseEntity<List<AutoDto>> result = restTemplate.exchange(entity, new ParameterizedTypeReference<List<AutoDto>>() {});
        if (Objects.isNull(result.getBody()) || result.getBody().isEmpty()) {
            return Strings.EMPTY;
        }

        AutoDto autoDto = result.getBody().get(0);
        return Optional.ofNullable(autoDto.getBodyNumber())
                .orElse(Strings.EMPTY);
    }

    private HttpHeaders headers() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-AUTH-USER", email);
        httpHeaders.add("X-AUTH-TOKEN", token);
        return httpHeaders;
    }
}
