package dk.sdu.mmmi.backendforfrontend.service;

import com.google.gson.Gson;
import dk.sdu.mmmi.backendforfrontend.service.mapper.DTOMapper;
import dk.sdu.mmmi.backendforfrontend.service.interfaces.AuthenticationService;
import dk.sdu.mmmi.backendforfrontend.service.interfaces.BFFService;
import dk.sdu.mmmi.backendforfrontend.service.interfaces.CompanyService;
import dk.sdu.mmmi.backendforfrontend.service.interfaces.JobService;
import dk.sdu.mmmi.backendforfrontend.service.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BFFServiceImplementation implements BFFService {
    private final CompanyService companyService;

    private final JobService jobService;

    private final AuthenticationService authenticationService;

    private final DTOMapper dtoMapper = DTOMapper.INSTANCE;

    private Gson gson = new Gson();

    @Override
    public Company getCompany(String email) {
        log.info("Get company: " + email);
        return companyService.findByEmail(email);
    }

    @Override
    public void registerCompany(Company company) {
        log.info("Company registered: " + company);
        companyService.create(company);
    }

    @Override
    public void registerUser(User user) {
        log.info("User registered: " + user);
        authenticationService.register(user);
    }

    @Override
    public TokenResponse login(LoginRequest loginRequest) {
        log.info("Logged in: " + loginRequest);
        return authenticationService.login(loginRequest);
    }

    @Override
    public void logout(LogoutRequest logoutRequest) {
        log.info("Company logged out: " + logoutRequest);
        authenticationService.logout(logoutRequest);
    }

    @Override
    public Job postJob(String email, Job job) {
        log.info("Job posted: " + job);
        if (job.getCompanyId() == null) {
            job.setCompanyId(companyService.getCompanyByEmail(email).getId());
        }
        return jobService.createJob(job);
    }

    @Override
    public JobDTO getJobWithCompany(long id) {
        log.info("Get job: " + id);
        Job job = jobService.getJob(id);
        if (job == null) {
            return null;
        }
        JobDTO jobDTO = dtoMapper.toJobDTO(job);
        jobDTO.setCompany(companyService.findById(job.getCompanyId()));
        return jobDTO;
    }

    @Override
    public void applyForJob(long id, Application application) {
        log.info("Apply for job: " + id);
        //UserId is token
        String userId = getUserIdFromToken(application.getUserId());

        //set new userId
        application.setUserId(userId);

        Job job = jobService.getJob(id);
        if (job == null) {
            throw new RuntimeException("Job not found");
        }
        jobService.applyForJob(id, application);
    }

    @Override
    public Page<Job> getAllJobs(Pageable pageable) {
        log.info("Get all jobs");
        return jobService.getAllJobs(pageable.getPageNumber(), pageable.getPageSize());
    }

    @Override
    public void updateCompany(Company company, Long id) {
        log.info("Company updated: " + company);
        companyService.update(id, company);
    }

    @Override
    public List<ApplicationDTO> getApplicationsForJob(long id) {
        log.info("Get applications for job: " + id);
        List<Application> applications = jobService.getApplicationsForJob(id);
        if(applications == null) {
            log.error("Received null from jobService.getApplicationsForJob(id)");
            return null;
        }
        if (applications.isEmpty()) {
            return Collections.emptyList();
        }
        return mapApplicationToApplicationDTOsWithUsers(applications);
    }

    @Override
    public void updateApplication(ApplicationDTO application, Long id) {
        log.info("Application updated: " + application);
        jobService.updateApplication(id, application);
    }

    @Override
    public List<ApplicationDTO> getApplicationsForUser(String userId) {
        log.info("Get applications for user: " + userId);
        List<Application> applications = jobService.getApplicationsForUser(userId);
        if(applications == null) {
            log.error("Received null from jobService.getApplicationsForUser(userId)");
            throw new RuntimeException("Applications not found");
        }
        return mapApplicationToApplicationDTOsWithUsers(applications);
    }

    @Override
    public Page<Job> searchJobs(String searchTerm, Pageable pageable) {
        log.info("Search jobs: " + searchTerm);
        return jobService.searchJobs(searchTerm, pageable.getPageNumber(), pageable.getPageSize());
    }

    @Override
    public Page<Job> filterJobs(Map<String, String> allRequestParams, Pageable pageable) {
        log.info("Filter jobs: " + allRequestParams);
        return jobService.filterJobs(allRequestParams, pageable.getPageNumber(), pageable.getPageSize());
    }

    @Override
    public void updateJob(Job job, Long id) {
        log.info("Job updated: " + job);
        jobService.update(id, job);
    }

    @Override
    public void updateUser(String id, User user) {
        log.info("User updated: " + id);
        authenticationService.updateUser(id, user);
    }

    @Override
    public User getUser(String id) {
        log.info("getUser({})", id);
        return authenticationService.getUser(id);
    }


    //Helper methods
    private List<ApplicationDTO> mapApplicationToApplicationDTOsWithUsers(List<Application> applications) {
        List<ApplicationDTO> applicationDTOS = applications.stream().map(dtoMapper::toApplicationDTO).toList();
        for (ApplicationDTO applicationDTO : applicationDTOS) {
            User user = authenticationService.getUser(applicationDTO.getUserId());
            applicationDTO.setUser(dtoMapper.toUserDTO(user));
        }
        return applicationDTOS;
    }

    /**
     * Extracts the userId from the token
     *
     * @param token the token
     * @return the userId
     */
    private String getUserIdFromToken(String token) {
        String[] split_string = token.split("\\.");
        String base64EncodedBody = split_string[1];
        Base64.Decoder decoder = Base64.getDecoder();
        String body = new String(decoder.decode(base64EncodedBody));
        return gson.fromJson(body, JWT.class).getUserId();
    }
}
