package dk.sdu.mmmi.backendforfrontend.service;

import dk.sdu.mmmi.backendforfrontend.service.interfaces.JobService;
import dk.sdu.mmmi.backendforfrontend.service.model.Application;
import dk.sdu.mmmi.backendforfrontend.service.model.ApplicationDTO;
import dk.sdu.mmmi.backendforfrontend.service.model.Job;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImplementation implements JobService {

    @Value("${url.jobservice}")
    private String JOB_SERVICE_URL;

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public Job createJob(Job job) {
        log.info("--> createJob: {}", job);
        ResponseEntity<Job> response = restTemplate.postForEntity(JOB_SERVICE_URL, job, Job.class);
        if(!response.getStatusCode().is2xxSuccessful()){
            log.error("Error creating job: {}", response.getStatusCode());
            return null;
        }
        return response.getBody();
    }

    @Override
    public Job getJob(long id) {
        log.info("--> getJob: {}", id);
        ResponseEntity<Job> response = restTemplate.getForEntity(JOB_SERVICE_URL + "/" + id, Job.class);
        if(!response.getStatusCode().is2xxSuccessful()){
            log.error("Error getting job: {}", response.getStatusCode());
            return null;
        }
        return response.getBody();
    }

    @Override
    public Job updateJob(long id, Job job) {
        log.info("--> updateJob: {}", job);
        ResponseEntity<Job> response = restTemplate.exchange(JOB_SERVICE_URL + "/" + id, HttpMethod.PUT, new HttpEntity<>(job), Job.class);
        if(!response.getStatusCode().is2xxSuccessful()){
            log.error("Error updating job: {}", response.getStatusCode());
            return null;
        }
        return response.getBody();
    }

    @Override
    public void deleteJob(long id) {
        log.info("--> deleteJob: {}", id);
        ResponseEntity<Void> response = restTemplate.exchange(JOB_SERVICE_URL + "/" + id, HttpMethod.DELETE, null, Void.class);
        if(!response.getStatusCode().is2xxSuccessful()){
            log.error("Error deleting job: {}", response.getStatusCode());
        }
    }

    @Override
    public List<Job> getJobsByCompanyId(long id) {
        log.info("--> getJobsByCompanyId: {}", id);
        ResponseEntity<Job[]> response = restTemplate.getForEntity(JOB_SERVICE_URL + "/companies/" + id, Job[].class);
        if(!response.getStatusCode().is2xxSuccessful()){
            log.error("Error getting jobs: {}", response.getStatusCode());
            return null;
        }
        return List.of(response.getBody());
    }

    @Override
    public List<Job> getAllJobs() {
        log.info("--> getAllJobs");
        ResponseEntity<Job[]> response = restTemplate.getForEntity(JOB_SERVICE_URL, Job[].class);
        if(!response.getStatusCode().is2xxSuccessful()){
            log.error("Error getting jobs: {}", response.getStatusCode());
            return null;
        }
        return List.of(response.getBody());
    }

    @Override
    public void applyForJob(long id, Application application) {
        log.info("--> applyForJob: {}", id);
        ResponseEntity<Void> response = restTemplate.exchange(JOB_SERVICE_URL + "/" + id + "/apply", HttpMethod.POST, new HttpEntity<>(application), Void.class);
        if(!response.getStatusCode().is2xxSuccessful()){
            log.error("Error applying for job: {}", response.getStatusCode());
        }
    }

    @Override
    public List<Application> getApplicationsForJob(long id) {
        log.info("--> getApplicationsForJob: {}", id);
        ResponseEntity<Application[]> response = restTemplate.getForEntity(JOB_SERVICE_URL + "/" + id + "/applications", Application[].class);
        if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null){
            log.error("Error getting applications: {}", response.getStatusCode());
            return Collections.emptyList();
        }
        if(response.getBody().length == 0){
            return Collections.emptyList();
        }
        return List.of(response.getBody());
    }

    @Override
    public void updateApplication(Long id, ApplicationDTO application) {
        log.info("--> updateApplication: {}", id);
        ResponseEntity<Void> response = restTemplate.exchange(JOB_SERVICE_URL + "/application/" + id, HttpMethod.PUT, new HttpEntity<>(application), Void.class);
        if(!response.getStatusCode().is2xxSuccessful()){
            log.error("Error updating application: {}", response.getStatusCode());
        }
    }

    @Override
    public List<Application> getApplicationsForUser(String userId) {
        log.info("--> getApplicationsForUser: {}", userId);
        ResponseEntity<Application[]> response = restTemplate.getForEntity(JOB_SERVICE_URL + "/applications/" + userId, Application[].class);
        if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null){
            log.error("Error getting applications: {}", response.getStatusCode());
        }
        if(response.getBody().length == 0){
            return Collections.emptyList();
        }
        return List.of(response.getBody());
    }

    @Override
    public List<Job> searchJobs(String searchTerm) {
        log.info("--> searchJobs: {}", searchTerm);
        ResponseEntity<Job[]> response = restTemplate.getForEntity(JOB_SERVICE_URL + "/search/" + searchTerm, Job[].class);
        if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null){
            log.error("Error getting jobs: {}", response.getStatusCode());
            return Collections.emptyList();
        }
        if(response.getBody().length == 0){
            return Collections.emptyList();
        }
        return List.of(response.getBody());
    }

    @Override
    public List<Job> filterJobs(Map<String, String> allRequestParams) {
        log.info("--> filterJobs: {}", allRequestParams);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(JOB_SERVICE_URL + "/filter");
        allRequestParams.forEach(builder::queryParam);
        ResponseEntity<Job[]> response = restTemplate.getForEntity(builder.toUriString(), Job[].class);
        if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null){
            log.error("Error getting jobs: {}", response.getStatusCode());
            return Collections.emptyList();
        }
        if(response.getBody().length == 0){
            return Collections.emptyList();
        }
        return List.of(response.getBody());
    }

    @Override
    public void update(Long id, Job job) {
        log.info("--> update: {}", job);
        ResponseEntity<Job> response = restTemplate.exchange(JOB_SERVICE_URL + "/" + id, HttpMethod.PUT, new HttpEntity<>(job), Job.class);
        if(!response.getStatusCode().is2xxSuccessful()){
            log.error("Error updating job: {}", response.getStatusCode());
        }
    }

}
