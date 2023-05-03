package dk.sdu.mmmi.backendforfrontend.outbound;

import dk.sdu.mmmi.backendforfrontend.service.interfaces.AuthenticationService;
import dk.sdu.mmmi.backendforfrontend.service.model.LoginRequest;
import dk.sdu.mmmi.backendforfrontend.service.model.LogoutRequest;
import dk.sdu.mmmi.backendforfrontend.service.model.TokenResponse;
import dk.sdu.mmmi.backendforfrontend.service.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class AuthenticationServiceImplementation implements AuthenticationService {

    @Value("${url.authenticationservice}")
    private String AUTHENTICATION_SERVICE_URL;

    private RestTemplate restTemplate = new RestTemplate();


    @Override
    public TokenResponse login(LoginRequest loginRequest) {
        log.info("--> login: {}", loginRequest);
        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(AUTHENTICATION_SERVICE_URL + "/login", loginRequest, TokenResponse.class);
        if(!response.getStatusCode().is2xxSuccessful()){
            log.error("Error logging in: {}", response.getStatusCode());
            return null;
        }
        return response.getBody();
    }

    @Override
    public void logout(LogoutRequest logoutRequest) {
        log.info("--> logout: {}", logoutRequest);
        ResponseEntity<Void> response = restTemplate.postForEntity(AUTHENTICATION_SERVICE_URL + "/logout", logoutRequest, Void.class);
        if(!response.getStatusCode().is2xxSuccessful()){
            log.error("Error logging out: {}", response.getStatusCode());
        }
    }

    @Override
    public void register(User user) {
        log.info("--> register: {}", user);
        ResponseEntity<Void> response = restTemplate.postForEntity(AUTHENTICATION_SERVICE_URL + "/register", user, Void.class);
        if(!response.getStatusCode().is2xxSuccessful()){
            log.error("Error registering: {}", response.getStatusCode());
        }
    }

    @Override
    public User getUser(String userId) {
        log.info("--> getUser: {}", userId);
        ResponseEntity<User> response = restTemplate.getForEntity(AUTHENTICATION_SERVICE_URL + "/user/" + userId, User.class);
        if(!response.getStatusCode().is2xxSuccessful()){
            log.error("Error getting user: {}", response.getStatusCode());
            return null;
        }
        return response.getBody();
    }

    @Override
    public void updateUser(String id, User user) {
        log.info("--> updateUser: {}", user);
        restTemplate.put(AUTHENTICATION_SERVICE_URL + "/user/" + id, user);
    }

    @Override
    public void deleteUser(String id) {
        log.info("--> deleteUser: {}", id);
        restTemplate.delete(AUTHENTICATION_SERVICE_URL + "/user/" + id);
    }
}
