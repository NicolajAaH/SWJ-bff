package dk.sdu.mmmi.backendforfrontend.service.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class Job {
    private Long id;

    private String title;

    private String location;

    private String description;

    private JobType jobType;

    private Double salary;

    private Date createdAt;

    private Date updatedAt;

    private Date expiresAt;

    private Company company;

}