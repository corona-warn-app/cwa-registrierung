package com.tsystems.mms.cwa.registration.cancellation.adapter.quicktest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class QuicktestPortalService {

    @Value("${quicktest.iam.username}")
    private String username;

    @Value("${quicktest.iam.password}")
    private String password;

    @Value("${quicktest.url}")
    private String portalBaseUrl;

    @Value("${quicktest.iam.url}")
    private String iamBaseUrl;

    private WebClient portalWebClient;
    private WebClient iamWebClient;

    public QuicktestPortalService() {

    }

    @PostConstruct
    public void initialize() {
        iamWebClient = WebClient.builder()
                .baseUrl(iamBaseUrl)
                .build();

        portalWebClient = WebClient.builder()
                .baseUrl(portalBaseUrl)
                .build();
    }

    public LocalDateTime cancelAccount(String partnerId, LocalDateTime cancellationDate) {
        final var token = requestToken();

        final var requestBody = new HashMap<String, Object>();
        requestBody.put("cancellationDate", cancellationDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.GERMANY)));
        requestBody.put("partnerIds", new String[]{partnerId});

        final var response = portalWebClient.post()
                .uri("/api/cancellation")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + token)
                .body(
                        BodyInserters.fromValue(requestBody)
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, String>>>() {
                })
                .block(Duration.ofSeconds(5));

        if (response == null || response.size() != 1) {
            throw new IllegalStateException("Invalid response");
        }

        return LocalDateTime.parse(response.get(0).get("finalDeletion"), DateTimeFormatter.ISO_DATE_TIME);
    }

    private String requestToken() {
        final var response = iamWebClient.post()
                .uri("/realms/qt/protocol/openid-connect/token")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .headers(httpHeaders -> httpHeaders.setBasicAuth(username, password))
                .body(
                        BodyInserters.fromFormData("grant_type", "client_credentials")
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
                })
                .block(Duration.ofSeconds(5));

        if (response == null) {
            throw new IllegalStateException("invalid response");
        }

        return response.get("access_token");
    }
}
