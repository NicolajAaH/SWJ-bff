package dk.sdu.mmmi.backendforfrontend.inbound;

import dk.sdu.mmmi.backendforfrontend.service.interfaces.BFFService;
import dk.sdu.mmmi.backendforfrontend.service.model.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bff")
@AllArgsConstructor
@Slf4j
@CrossOrigin
public class BFFController {
    private final BFFService bffService;

    // ----- COMPANY -----
    @GetMapping("/company/{email}")
    public ResponseEntity<Company> getCompany(@PathVariable("email") String email) {
        log.info("Get company: " + email);
        Company company = bffService.getCompany(email);

        if (company == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(company, HttpStatus.OK);
    }

    @PutMapping("company/{id}")
    public void updateCompany(@RequestBody Company company, @PathVariable Long id) {
        log.info("Company updated: " + company);
        bffService.updateCompany(company, id);
    }

    @PostMapping("/companies/register")
    public void registerCompany(@RequestBody Company company) {
        log.info("Company registered: " + company);
        bffService.registerCompany(company);
    }

    // ----- AUTHENTICATION -----

    @PostMapping("/auth/register")
    public void registerUser(@RequestBody User user) {
        log.info("User registered: " + user);
        bffService.registerUser(user);
    }

    @PostMapping("/auth/login")
    public TokenResponse login(@RequestBody LoginRequest loginRequest) {
        log.info("Logged in: " + loginRequest);
        return bffService.login(loginRequest);
    }

    @PostMapping("/auth/logout")
    public void logout(@RequestBody LogoutRequest logoutRequest) {
        log.info("Company logged out: " + logoutRequest);
        bffService.logout(logoutRequest);
    }

    @PutMapping("/auth/user/{id}")
    public void updateUser(@PathVariable String id, @RequestBody User user){
        log.info("User updated: " + id);
        bffService.updateUser(id, user);
    }

    @GetMapping("/auth/user/{id}")
    public User getUser(@PathVariable String id){
        log.info("Get user: " + id);
        return bffService.getUser(id);
    }

    // ----- JOB -----

    @PostMapping("/job/{email}")
    public ResponseEntity<Job> postJob(@PathVariable String email, @RequestBody Job job) {
        log.info("Job posted: " + job);
        if(email == null || job == null || email.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Job postedJob = bffService.postJob(email, job);
        if (postedJob == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(postedJob, HttpStatus.OK);
    }

    @GetMapping("/job/{id}")
    public ResponseEntity<JobDTO> getJobWithCompany(@PathVariable("id") long id) {
        log.info("Get job: " + id);
        JobDTO job = bffService.getJobWithCompany(id);
        if (job == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(job, HttpStatus.OK);
    }

    @PostMapping("/job/{id}/apply")
    public ResponseEntity<Void> applyForJob(@PathVariable("id") long id, @RequestBody Application application) {
        log.info("Apply for job: " + id);
        bffService.applyForJob(id, application);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/job")
    public ResponseEntity<List<Job>> getAllJobs() { //TODO add pagination
        log.info("Get all jobs");
        List<Job> jobs = bffService.getAllJobs();
        if (jobs == null || jobs.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(jobs, HttpStatus.OK);
    }

    @GetMapping("/job/search/{searchTerm}")
    public ResponseEntity<List<Job>> searchJobs(@PathVariable("searchTerm") String searchTerm) {
        log.info("Search jobs: " + searchTerm);
        List<Job> jobs = bffService.searchJobs(searchTerm);
        if (jobs.isEmpty()) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(jobs, HttpStatus.OK);
    }

    @GetMapping("/job/filter")
    public ResponseEntity<List<Job>> filterJobs(@RequestParam Map<String, String> allRequestParams) {
        log.info("Filter jobs: " + allRequestParams);
        List<Job> jobs = bffService.filterJobs(allRequestParams);
        if (jobs.isEmpty()) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(jobs, HttpStatus.OK);
    }

    @PutMapping("/job/{id}")
    public void updateJob(@RequestBody Job job, @PathVariable Long id) {
        log.info("Job updated: " + job);
        bffService.updateJob(job, id);
    }

    // ----- APPLICATION -----
    @GetMapping("/job/{id}/applications")
    public ResponseEntity<List<ApplicationDTO>> getApplicationsForJob(@PathVariable("id") long id) {
        log.info("Get applications for job: " + id);
        List<ApplicationDTO> applications = bffService.getApplicationsForJob(id);
        if (applications == null) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR); //Something went wrong in the API call
        }
        if (applications.isEmpty()) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }

    @PutMapping("/application/{id}")
    public void updateApplication(@RequestBody ApplicationDTO application, @PathVariable Long id) {
        log.info("Application updated: " + application);
        bffService.updateApplication(application, id);
    }

    @GetMapping("/applications/{userId}")
    public ResponseEntity<List<ApplicationDTO>> getApplicationsForUser(@PathVariable("userId") String userId) {
        log.info("Get applications for user: " + userId);
        List<ApplicationDTO> applications = bffService.getApplicationsForUser(userId);
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }
}
