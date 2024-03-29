package dk.sdu.mmmi.backendforfrontend.service.interfaces;

import dk.sdu.mmmi.backendforfrontend.service.model.Application;
import dk.sdu.mmmi.backendforfrontend.service.model.ApplicationDTO;
import dk.sdu.mmmi.backendforfrontend.service.model.Job;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface JobService {
    Job createJob(Job job);

    Job getJob(long id);

    void deleteJob(long id);

    List<Job> getJobsByCompanyId(long id);

    Page<Job> getAllJobs(int pageNumber, int pageSize);

    void applyForJob(long id, Application application);

    List<Application> getApplicationsForJob(long id);

    void updateApplication(Long id, ApplicationDTO application);

    List<Application> getApplicationsForUser(String userId);

    Page<Job> searchJobs(String searchTerm, int pageNumber, int pageSize);

    Page<Job> filterJobs(Map<String, String> allRequestParams, int pageNumber, int pageSize);

    void update(Long id, Job job);
}
