package dk.sdu.mmmi.backendforfrontend.inbound;

import dk.sdu.mmmi.backendforfrontend.service.interfaces.BFFService;
import dk.sdu.mmmi.backendforfrontend.service.model.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @PutMapping("/company/{id}")
    public void updateCompany(@RequestBody Company company, @PathVariable Long id) {
        log.info("Company updated: " + company);
        bffService.updateCompany(company, id);
    }

    @PutMapping("/company/byEmail/{email}")
    public ResponseEntity<Void> updateCompany(@RequestBody Company company, @PathVariable String email) {
        log.info("Company updated: " + company);
        bffService.updateCompany(company, email);
        return new ResponseEntity<>(HttpStatus.OK);
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
    public ResponseEntity<Page<Job>> getAllJobs(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        log.info("Get all jobs with pagination (page={}, size={})", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Job> jobs = bffService.getAllJobs(pageable);

        if (!jobs.hasContent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(jobs, HttpStatus.OK);
    }


    @GetMapping("/job/search/{searchTerm}")
    public ResponseEntity<Page<Job>> searchJobs(@PathVariable("searchTerm") String searchTerm,
            @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size
    ) {
        log.info("Search all jobs with pagination (page={}, size={}) and search {}", page, size, searchTerm);

        Pageable pageable = PageRequest.of(page, size);

        Page<Job> jobs = bffService.searchJobs(searchTerm, pageable);
        if (!jobs.hasContent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(jobs, HttpStatus.OK);
    }

    @GetMapping("/job/filter")
    public ResponseEntity<Page<Job>> filterJobs(@RequestParam Map<String, String> allRequestParams) {
        log.info("Filter all jobs with params: " + allRequestParams);

        int page = 0; // Default page is 0
        int size = 10; // Default size is 10
        if (allRequestParams.containsKey("page")) {
            try {
                page = Integer.parseInt(allRequestParams.get("page"));
            } catch (NumberFormatException e) {
                log.error("Page parameter is not a number: " + allRequestParams.get("page"));
                log.error("Using default page: 0");
            } finally {
                allRequestParams.remove("page");
            }
        }
        if (allRequestParams.containsKey("size")) {
            try {
                size = Integer.parseInt(allRequestParams.get("size"));
            } catch (NumberFormatException e) {
                log.error("Size parameter is not a number: " + allRequestParams.get("size"));
                log.error("Using default size: 10");
            } finally {
                allRequestParams.remove("size");
            }
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<Job> jobs = bffService.filterJobs(allRequestParams, pageable);
        if (!jobs.hasContent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
        if (applications == null) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR); //Something went wrong in the API call
        }
        if (applications.isEmpty()) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NO_CONTENT);
        }
        for (ApplicationDTO application : applications) {
            application.setJob(bffService.getJobWithCompany(application.getJobId()));
        }
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }
}
