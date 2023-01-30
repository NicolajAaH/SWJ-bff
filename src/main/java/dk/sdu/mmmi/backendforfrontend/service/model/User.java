package dk.sdu.mmmi.backendforfrontend.service.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class User {

    private String email;

    @ToString.Exclude
    private String password;

    private String name;

    private UserRole role;
}
