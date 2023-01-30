package dk.sdu.mmmi.backendforfrontend.inbound;

import com.google.gson.Gson;
import dk.sdu.mmmi.backendforfrontend.service.interfaces.AuthenticationService;
import dk.sdu.mmmi.backendforfrontend.service.interfaces.CompanyService;
import dk.sdu.mmmi.backendforfrontend.service.interfaces.JobService;
import dk.sdu.mmmi.backendforfrontend.service.model.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/api/bff")
@AllArgsConstructor
@Slf4j
@CrossOrigin
public class BFFController {
    private final CompanyService companyService;

    private final JobService jobService;

    private final AuthenticationService authenticationService;

    private final DTOMapper dtoMapper = DTOMapper.INSTANCE;

    private Gson gson = new Gson();

    @GetMapping("company/{id}")
    public ResponseEntity<Company> getCompany(@PathVariable("id") long id) {
        log.info("Get company: " + id);
        Company company = companyService.findById(id);

        if (company == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        company.setJobs(new HashSet<>(jobService.getJobsByCompanyId(id)));
        return new ResponseEntity<>(company, HttpStatus.OK);
    }

    @PostMapping("/companies/register")
    public void registerCompany(@RequestBody Company company) {
        log.info("Company registered: " + company);
        companyService.create(company);
    }

    @PostMapping("/auth/register")
    public void registerUser(@RequestBody User user) {
        log.info("User registered: " + user);
        authenticationService.register(user);
    }

    @PostMapping("/auth/login")
    public TokenResponse login(@RequestBody LoginRequest loginRequest) {
        log.info("Logged in: " + loginRequest);
        return authenticationService.login(loginRequest);
    }

    @PostMapping("/auth/logout")
    public void logout(@RequestBody LogoutRequest logoutRequest) {
        log.info("Company logged out: " + logoutRequest);
        authenticationService.logout(logoutRequest);
    }

    @PostMapping("/job/{email}")
    public Job postJob(@PathVariable String email, @RequestBody Job job) {
        log.info("Job posted: " + job);
        if (job.getCompanyId() == null) {
            job.setCompanyId(companyService.getCompanyByEmail(email).getId());
        }
        return jobService.createJob(job);
    }

    @GetMapping("/job/{id}")
    public ResponseEntity<JobDTO> getJobWithCompany(@PathVariable("id") long id) {
        log.info("Get job: " + id);
        Job job = jobService.getJob(id);
        if (job == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        JobDTO jobDTO = dtoMapper.toJobDTO(job);
        jobDTO.setCompany(companyService.findById(job.getCompanyId()));
        return new ResponseEntity<>(jobDTO, HttpStatus.OK);
    }

    @PostMapping("/job/{id}/apply")
    public ResponseEntity<Void> applyForJob(@PathVariable("id") long id, @RequestBody Application application) {
        log.info("Apply for job: " + id);
        //UserId is token
        String userId = getUserIdFromToken(application.getUserId());

        //set new userId
        application.setUserId(userId);

        Job job = jobService.getJob(id);
        if (job == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        jobService.applyForJob(id, application);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private String getUserIdFromToken(String token) {
        String[] split_string = token.split("\\.");
        String base64EncodedBody = split_string[1];
        Base64.Decoder decoder = Base64.getDecoder();
        String body = new String(decoder.decode(base64EncodedBody));
        return gson.fromJson(body, JWT.class).getUserId();
    }

    @GetMapping("/job")
    public ResponseEntity<List<Job>> getAllJobs() {
        log.info("Get all jobs");
        if (jobService.getAllJobs().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(jobService.getAllJobs(), HttpStatus.OK);
    }

    @PutMapping("company/{id}")
    public void updateCompany(@RequestBody Company company, @PathVariable Long id) {
        log.info("Company updated: " + company);
        companyService.update(id, company);
    }
}
