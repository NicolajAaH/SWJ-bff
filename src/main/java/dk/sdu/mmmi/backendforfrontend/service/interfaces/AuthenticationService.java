package dk.sdu.mmmi.backendforfrontend.service.interfaces;

import dk.sdu.mmmi.backendforfrontend.service.model.LoginRequest;
import dk.sdu.mmmi.backendforfrontend.service.model.LogoutRequest;

public interface AuthenticationService {
    void login(LoginRequest loginRequest);

    void logout(LogoutRequest logoutRequest);
}
