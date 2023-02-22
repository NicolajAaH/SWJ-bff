package dk.sdu.mmmi.backendforfrontend.service.interfaces;

import dk.sdu.mmmi.backendforfrontend.service.model.LoginRequest;
import dk.sdu.mmmi.backendforfrontend.service.model.LogoutRequest;
import dk.sdu.mmmi.backendforfrontend.service.model.TokenResponse;
import dk.sdu.mmmi.backendforfrontend.service.model.User;

public interface AuthenticationService {
    TokenResponse login(LoginRequest loginRequest);

    void logout(LogoutRequest logoutRequest);

    void register(User user);

    User getUser(String userId);
}
