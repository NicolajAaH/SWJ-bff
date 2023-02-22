package dk.sdu.mmmi.backendforfrontend.service.interfaces;

import dk.sdu.mmmi.backendforfrontend.service.model.Application;
import dk.sdu.mmmi.backendforfrontend.service.model.ApplicationDTO;
import dk.sdu.mmmi.backendforfrontend.service.model.Job;

import java.util.List;

public interface JobService {
    Job createJob(Job job);

    Job getJob(long id);

    Job updateJob(long id, Job job);

    void deleteJob(long id);

    List<Job> getJobsByCompanyId(long id);

    List<Job> getAllJobs();

    void applyForJob(long id, Application application);

    List<Application> getApplicationsForJob(long id);

    void updateApplication(Long id, ApplicationDTO application);
}
