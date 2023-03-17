package dk.sdu.mmmi.backendforfrontend.service.interfaces;

import dk.sdu.mmmi.backendforfrontend.service.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface BFFService {

    Company getCompany(String email);

    void registerCompany(Company company);

    void registerUser(User user);

    TokenResponse login(LoginRequest loginRequest);

    void logout(LogoutRequest logoutRequest);

    Job postJob(String email, Job job);

    JobDTO getJobWithCompany(long id);

    void applyForJob(long id, Application application);

    Page<Job> getAllJobs(Pageable pageable);

    void updateCompany(Company company, Long id);

    List<ApplicationDTO> getApplicationsForJob(long id);

    void updateApplication(ApplicationDTO application, Long id);

    List<ApplicationDTO> getApplicationsForUser(String userId);

    List<Job> searchJobs(String searchTerm);

    List<Job> filterJobs(Map<String, String> allRequestParams);

    void updateJob(Job job, Long id);

    void updateUser(String id, User user);

    User getUser(String id);
}
