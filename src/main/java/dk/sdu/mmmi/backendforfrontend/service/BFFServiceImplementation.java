package dk.sdu.mmmi.backendforfrontend.service;

import com.google.gson.Gson;
import dk.sdu.mmmi.backendforfrontend.service.exceptions.ExpiredException;
import dk.sdu.mmmi.backendforfrontend.service.exceptions.NotFoundException;
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

import java.util.*;

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
        return companyService.getCompanyByEmail(email);
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

        //set userId
        application.setUserId(userId);

        Job job = jobService.getJob(id);
        if (job == null) {
            throw new NotFoundException("Job not found");
        }
        if (job.getExpiresAt().before(new Date())) {
            log.error("Job expired - date: {}", job.getExpiresAt());
            throw new ExpiredException("Job expired");
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
    public void updateCompany(Company company, String email) {
        log.info("Company updated: " + company);
        companyService.update(email, company);
    }

    @Override
    public List<ApplicationDTO> getApplicationsForJob(long id) {
        log.info("Get applications for job: " + id);
        List<Application> applications = jobService.getApplicationsForJob(id);
        if(applications == null) {
            log.error("Received null from jobService.getApplicationsForJob(id)");
            return Collections.emptyList();
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
            throw new NotFoundException("Applications not found");
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

    @Override
    public void deleteUser(String id) {
        log.info("deleteUser({})", id);
        authenticationService.deleteUser(id);
    }

    @Override
    public Company getCompany(Long id) {
        log.info("getCompany({})", id);
        Company company = companyService.findById(id);
        if(company == null) {
            log.error("Received null from companyService.findById(id)");
            return null;
        }
        //Fetch jobs related to the company
        List<Job> jobs = jobService.getJobsByCompanyId(id);
        if(jobs == null) {
            log.error("Received null from jobService.getJobsByCompanyId(id)");
            return company;
        }
        company.setJobs(new HashSet<>(jobs));
        return company;
    }


    // ----- Helper methods ------

    /**
     * Maps a list of applications to a list of applicationDTOs and adds the user to each applicationDTO
     * @param applications the applications
     * @return the applicationDTOs
     */
    private List<ApplicationDTO> mapApplicationToApplicationDTOsWithUsers(List<Application> applications) {
        List<ApplicationDTO> applicationDTOS = applications.stream().map(dtoMapper::toApplicationDTO).toList();
        for (ApplicationDTO applicationDTO : applicationDTOS) {
            User user = authenticationService.getUser(applicationDTO.getUserId()); //Get the user from the userId
            applicationDTO.setUser(dtoMapper.toUserDTO(user));
        }
        return applicationDTOS;
    }

    /**
     * Extracts the userId from the token
     * @param token the token
     * @return the userId
     */
    private String getUserIdFromToken(String token) {
        String[] splitString = token.split("\\."); //Split at .
        String base64EncodedBody = splitString[1]; //Get the body
        Base64.Decoder decoder = Base64.getDecoder();
        String body = new String(decoder.decode(base64EncodedBody)); //Decode the body
        return gson.fromJson(body, JWT.class).getUserId(); //Get the userId from the body
    }
}
