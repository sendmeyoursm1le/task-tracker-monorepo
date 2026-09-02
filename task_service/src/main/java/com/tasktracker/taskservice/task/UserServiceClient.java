package com.tasktracker.taskservice.task;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class UserServiceClient {

    private final RestClient restClient;

    public UserServiceClient(
            RestClient.Builder restClientBuilder,
            @Value("${services.user-service.url}") String userServiceUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(userServiceUrl)
                .build();
    }

    public void verifyUserExists(Long assigneeId) {
        try {
            restClient.get()
                    .uri("/users/{userId}", assigneeId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound exception) {
            throw new AssigneeNotFoundException(assigneeId);
        } catch (RestClientException exception) {
            throw new UserServiceUnavailableException(assigneeId);
        }
    }
}
