package faang.school.postservice.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AiModerationService {

    private static final String API_URL = "https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze?key=YOUR_API_KEY";
    private static final Logger log = LoggerFactory.getLogger(AiModerationService.class);

    public boolean isToxic(String text) {
        if (text == null || text.isBlank()) {
            log.warn("Bad words list contains null or empty string.");
            return false;
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            JSONObject request = new JSONObject();
            request.put("comment", new JSONObject().put("text", text));
            request.put("languages", new String[]{"en"});
            request.put("requestedAttributes", new JSONObject().put("TOXICITY", new JSONObject()));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);

            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
            JSONObject jsonResponse = new JSONObject(response.getBody());
            double toxicityScore = jsonResponse.getJSONObject("attributeScores")
                    .getJSONObject("TOXICITY")
                    .getJSONObject("summaryScore")
                    .getDouble("value");
            log.info("Toxicity score: {}", toxicityScore);
            return toxicityScore > 0.7;
        } catch (Exception e) {
            log.error("Error while requesting to AiModerationService: {}", e.getMessage(), e);
            return false;
        }
    }
}
