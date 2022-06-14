package ikjo.oauthexample.service;

import ikjo.oauthexample.dto.OAuthAccessToken;
import java.util.LinkedHashMap;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class OAuthService {

    private static final String GITHUB_AUTHORIZATION_SERVER_URL = "https://github.com/login/oauth/access_token";
    private static final String SCOPE = "user/emails";
    private static final String GITHUB_EMAIL_API_ACCEPT_HEADER = "application/vnd.github.v3+json";
    private static final String GITHUB_RESOURCE_SERVER_EMAIL_API_URL = "https://api.github.com/" + SCOPE;

    public String authorizeForThirdParty(String code) {
        OAuthAccessToken OAuthAccessToken = getAccessTokenFromAuthServer(code);
        String email = getUserEmailFromResourceServer(OAuthAccessToken);

        return email;
    }

    private OAuthAccessToken getAccessTokenFromAuthServer(String code) {
        MultiValueMap<String, String> requestHeader = getRequestHeader();
        MultiValueMap<String, String> requestPayload = getRequestPayload(code);
        HttpEntity<?> request = new HttpEntity<>(requestPayload, requestHeader);

        ResponseEntity<?> response = new RestTemplate().postForEntity(
            GITHUB_AUTHORIZATION_SERVER_URL, request, OAuthAccessToken.class);

        return (OAuthAccessToken) response.getBody();
    }

    private MultiValueMap<String, String> getRequestHeader() {
        MultiValueMap<String, String> requestHeader = new LinkedMultiValueMap<>();
        requestHeader.set("Accept", "application/json");
        return requestHeader;
    }

    private MultiValueMap<String, String> getRequestPayload(String code) {
        MultiValueMap<String, String> requestPayload = new LinkedMultiValueMap<>();
        requestPayload.set("client_id", System.getenv("CLIENT_ID"));
        requestPayload.set("client_secret", System.getenv("CLIENT_SECRET"));
        requestPayload.set("code", code);
        return requestPayload;
    }

    private String getUserEmailFromResourceServer(OAuthAccessToken OAuthAccessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Accept", GITHUB_EMAIL_API_ACCEPT_HEADER);
        httpHeaders.set("Authorization", OAuthAccessToken.getAuthorizationValue());

        HttpEntity<?> request = new HttpEntity<>(httpHeaders);
        ResponseEntity<Object[]> response = new RestTemplate().exchange(
            GITHUB_RESOURCE_SERVER_EMAIL_API_URL,
            HttpMethod.GET,
            request,
            Object[].class
        );

        Object[] userEmails = response.getBody();

        return (String) ((LinkedHashMap) userEmails[0]).get("email");
    }
}
