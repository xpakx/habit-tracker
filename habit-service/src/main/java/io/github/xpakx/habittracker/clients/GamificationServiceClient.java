package io.github.xpakx.habittracker.clients;

import io.github.xpakx.habittracker.clients.dto.CompletionDto;
import io.github.xpakx.habittracker.habit.HabitCompletion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GamificationServiceClient {
    private final RestTemplate restTemplate;
    private final String gamificationServiceUri;

    public GamificationServiceClient(RestTemplate restTemplate, @Value("${service.gamification.host}") String gamificationServiceUri) {
        this.restTemplate = restTemplate;
        this.gamificationServiceUri = gamificationServiceUri;
    }

    public boolean sendCompletion(HabitCompletion completion) {
        CompletionDto completionDto = new CompletionDto();
        completionDto.setCompletionId(completion.getId());
        completionDto.setHabitId(completion.getHabit().getId());
        completionDto.setUserId(1L);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    gamificationServiceUri + "/attempt",
                    completionDto,
                    String.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch(Exception e) {
            return false;
        }
    }
}
