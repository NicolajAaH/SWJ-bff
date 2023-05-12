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
import org.springframework.security.access.prepost.PreAuthorize;
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

    /**
     * Get company by email
     * @apiNote All requests to this endpoint are permitted
     * @param email company email
     * @return company wrapped in response entity
     */
    @GetMapping("/company/{email}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Company> getCompany(@PathVariable("email") String email) {
        log.info("Get company: " + email);
        Company company = bffService.getCompany(email);

        if (company == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(company, HttpStatus.OK);
    }

    /**
     * Get company by id with all its jobs
     * @apiNote All requests to this endpoint are permitted
     * @param id company id
     * @return company with jobs and status code
     */
    @GetMapping("/company/byId/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Company> getCompany(@PathVariable("id") Long id) {
        log.info("Get company: " + id);
        Company company = bffService.getCompany(id);

        if (company == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(company, HttpStatus.OK);
    }

    /**
     * Update company by id
     * @apiNote Needs company authority
     * @param company company to update
     * @param id company id
     * @return response entity with status code
     */
    @PutMapping("/company/{id}")
    @PreAuthorize("hasAuthority('COMPANY')")
    public ResponseEntity<Void> updateCompany(@RequestBody Company company, @PathVariable Long id) {
        log.info("Company updated: " + company);
        bffService.updateCompany(company, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Update company by email
     * @apiNote Needs company authority
     * @param company company to update
     * @param email company email
     * @return response entity with status code
     */
    @PutMapping("/company/byEmail/{email}")
    @PreAuthorize("hasAuthority('COMPANY')")
    public ResponseEntity<Void> updateCompany(@RequestBody Company company, @PathVariable String email) {
        log.info("Company updated: " + company);
        bffService.updateCompany(company, email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Register company
     * @apiNote All requests to this endpoint are permitted
     * @param company company to register
     * @return response entity with status code - 201 if created
     */
    @PostMapping("/companies/register")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Void> registerCompany(@RequestBody Company company) {
        log.info("Company registered: " + company);
        bffService.registerCompany(company);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    // ----- AUTHENTICATION -----

    /**
     * Register user
     * @apiNote All requests to this endpoint are permitted
     * @param user user to register
     * @return response entity with status code
     */
    @PostMapping("/auth/register")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Void> registerUser(@RequestBody User user) {
        log.info("User registered: " + user);
        bffService.registerUser(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Login user
     * @apiNote All requests to this endpoint are permitted
     * @param loginRequest login request
     * @return response entity with token
     */
    @PostMapping("/auth/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        log.info("Logged in: " + loginRequest);
        TokenResponse tokenResponse = bffService.login(loginRequest);
        if (tokenResponse == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
    }

    /**
     * Logout user
     * @apiNote Needs company or applicant authority
     * @param logoutRequest logout request
     * @return response entity with status code
     */
    @PostMapping("/auth/logout")
    @PreAuthorize("hasAuthority('COMPANY') || hasAuthority('APPLICANT')")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest logoutRequest) {
        log.info("Company logged out: " + logoutRequest);
        bffService.logout(logoutRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Update user by id
     * @apiNote Needs to be logged in as the user
     * @param id user id
     * @param user user to update
     * @return response entity with status code
     */
    @PutMapping("/auth/user/{id}")
    @PreAuthorize("#id.equals(authentication.principal.username)")
    public ResponseEntity<Void> updateUser(@PathVariable String id, @RequestBody User user){
        log.info("User updated: " + id);
        bffService.updateUser(id, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Get user by id
     * @apiNote Needs to be logged in as the user
     * @param id user id
     * @return response entity with user
     */
    @GetMapping("/auth/user/{id}")
    @PreAuthorize("#id.equals(authentication.principal.username)")
    public ResponseEntity<User> getUser(@PathVariable String id){
        log.info("Get user: " + id);
        User user = bffService.getUser(id);
        if(user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Delete user by id
     * @apiNote Needs to be logged in as the user
     * @param id user id of the user to delete
     * @return response entity with status code - 204 if deleted
     */
    @DeleteMapping("/auth/user/{id}")
    @PreAuthorize("#id.equals(authentication.principal.username)")
    public ResponseEntity<Void> deleteUser(@PathVariable String id){
        log.info("Delete user: " + id);
        bffService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    // ----- JOB -----

    /**
     * Post a job
     * @apiNote Needs company authority
     * @param email company email
     * @param job job to post
     * @return response entity with created job
     */
    @PostMapping("/job/{email}")
    @PreAuthorize("hasAuthority('COMPANY')")
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

    /**
     * Get job with company
     * @apiNote All requests to this endpoint are permitted
     * @param id job id
     * @return response entity with job
     */
    @GetMapping("/job/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<JobDTO> getJobWithCompany(@PathVariable("id") long id) {
        log.info("Get job: " + id);
        JobDTO job = bffService.getJobWithCompany(id);
        if (job == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(job, HttpStatus.OK);
    }

    /**
     * Get all jobs with pagination
     * @apiNote All requests to this endpoint are permitted
     * @param page page number - default 0
     * @param size page size - default 10
     * @return response entity with page of jobs
     */
    @GetMapping("/job")
    @PreAuthorize("permitAll()")
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

    /**
     * Search all jobs with pagination and search
     * @apiNote All requests to this endpoint are permitted
     * @param searchTerm search term
     * @param page page number - default 0
     * @param size page size - default 10
     * @return response entity with page of jobs
     */
    @GetMapping("/job/search/{searchTerm}")
    @PreAuthorize("permitAll()")
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

    /**
     * Filter all jobs with pagination and filter
     * @apiNote All requests to this endpoint are permitted
     * @param allRequestParams filter params
     * @return response entity with page of filtered jobs
     */
    @GetMapping("/job/filter")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<Job>> filterJobs(@RequestParam Map<String, String> allRequestParams) {
        log.info("Filter all jobs with params: " + allRequestParams);

        int page = 0; // Default page is 0
        int size = 10; // Default size is 10
        if (allRequestParams.containsKey("page")) { // If it is not specified, it is 0 by default
            try {
                page = Integer.parseInt(allRequestParams.get("page"));
            } catch (NumberFormatException e) {
                log.error("Page parameter is not a number: " + allRequestParams.get("page"));
                log.error("Using default page: 0");
            } finally {
                allRequestParams.remove("page"); // Remove page param from map as it is not a filter param
            }
        }
        if (allRequestParams.containsKey("size")) { // If it is not specified, default size is 10
            try {
                size = Integer.parseInt(allRequestParams.get("size"));
            } catch (NumberFormatException e) {
                log.error("Size parameter is not a number: " + allRequestParams.get("size"));
                log.error("Using default size: 10");
            } finally {
                allRequestParams.remove("size"); // Remove size param from map as it is not a filter param
            }
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<Job> jobs = bffService.filterJobs(allRequestParams, pageable);
        if (!jobs.hasContent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(jobs, HttpStatus.OK);
    }

    /**
     * Update job
     * @apiNote Needs company authority
     * @param job job to update
     * @param id job id
     * @return response entity with status code
     */
    @PutMapping("/job/{id}")
    @PreAuthorize("hasAuthority('COMPANY')")
    public ResponseEntity<Void> updateJob(@RequestBody Job job, @PathVariable Long id) {
        log.info("Job updated: " + job);
        bffService.updateJob(job, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    // ----- APPLICATION -----

    /**
     * Apply for a job
     * @apiNote Needs applicant authority
     * @param id job id
     * @param application application to apply
     * @return response entity with status code
     */
    @PostMapping("/job/{id}/apply")
    @PreAuthorize("hasAuthority('APPLICANT')")
    public ResponseEntity<Void> applyForJob(@PathVariable("id") long id, @RequestBody Application application) {
        log.info("Apply for job: " + id);
        bffService.applyForJob(id, application);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Get all applications for a job
     * @apiNote Needs company authority
     * @param id job id
     * @return response entity with list of applications
     */
    @GetMapping("/job/{id}/applications")
    @PreAuthorize("hasAuthority('COMPANY')")
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

    /**
     * Update application
     * @apiNote Needs company authority
     * @param application application to update
     * @param id application id
     * @return response entity with status code
     */
    @PutMapping("/application/{id}")
    @PreAuthorize("hasAuthority('COMPANY')")
    public ResponseEntity<Void> updateApplication(@RequestBody ApplicationDTO application, @PathVariable Long id) {
        log.info("Application updated: " + application);
        bffService.updateApplication(application, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Get all applications for a user
     * @apiNote Needs applicant authority
     * @param userId user id
     * @return response entity with list of applications
     */
    @GetMapping("/applications/{userId}")
    @PreAuthorize("hasAuthority('APPLICANT')")
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
            // Job is included in the application
            application.setJob(bffService.getJobWithCompany(application.getJobId()));
        }
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }
}
