package dk.sdu.mmmi.backendforfrontend.service.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JWT {

    private String email;

    private String userId;

    private String role;

    private Integer phone;

    private String name;

    private String iat;

    private String exp;
}
