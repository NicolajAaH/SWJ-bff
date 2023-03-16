package dk.sdu.mmmi.backendforfrontend.service.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class User {

    private String email;

    @ToString.Exclude
    private String password;

    private String name;

    private UserRole role;

    private Date createdAt;

    private Integer phone;
}
