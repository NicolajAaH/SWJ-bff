package dk.sdu.mmmi.backendforfrontend.service.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@ToString
public class Company {
    private Long id;

    private String name;

    private String email;

    private String website;

    private Date createdAt;

    private Date updatedAt;

    private Integer phone;

    @ToString.Exclude
    private Set<Job> jobs;
}
