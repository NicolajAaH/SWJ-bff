package dk.sdu.mmmi.backendforfrontend.outbound;

import dk.sdu.mmmi.backendforfrontend.TestObjects;
import dk.sdu.mmmi.backendforfrontend.service.interfaces.AuthenticationService;
import dk.sdu.mmmi.backendforfrontend.service.model.LoginRequest;
import dk.sdu.mmmi.backendforfrontend.service.model.TokenResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AuthenticationServiceImplementationTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuthenticationService authenticationService = new AuthenticationServiceImplementation();

    @Test
    void login() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(new ResponseEntity<>(TestObjects.createMockTokenResponse(), HttpStatus.OK));
        TokenResponse tokenResponse = authenticationService.login(TestObjects.createMockLoginRequest());
        assertThat(tokenResponse).isNotNull();
        assertThat(tokenResponse.getToken()).isEqualTo("token");
    }

    @Test
    void logout() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        authenticationService.logout(TestObjects.createMockLogoutRequest());
    }

    @Test
    void register() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        authenticationService.register(TestObjects.createMockUser());
    }

    @Test
    void getUser() {
        when(restTemplate.getForEntity(anyString(), any())).thenReturn(new ResponseEntity<>(TestObjects.createMockUser(), HttpStatus.OK));
        assertThat(authenticationService.getUser("userId")).isNotNull();
    }

    @Test
    void updateUser() {
        doNothing().when(restTemplate).put(anyString(), any());
        authenticationService.updateUser("TestId", TestObjects.createMockUser());
    }

    @Test
    void deleteUser() {
        doNothing().when(restTemplate).delete(anyString());
        authenticationService.deleteUser("TestId");
    }
}