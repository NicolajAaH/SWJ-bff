package dk.sdu.mmmi.backendforfrontend.outbound;

import dk.sdu.mmmi.backendforfrontend.TestObjects;
import dk.sdu.mmmi.backendforfrontend.service.interfaces.JobService;
import dk.sdu.mmmi.backendforfrontend.service.model.Application;
import dk.sdu.mmmi.backendforfrontend.service.model.Job;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class JobServiceImplementationTest {

    @Mock
    private RestTemplate restTemplate;


    @InjectMocks
    private JobService jobService = new JobServiceImplementation();

    @BeforeEach
    public void setup(){
        ReflectionTestUtils.setField(jobService, "JOB_SERVICE_URL", "http://localhost:8080/api/job");
    }

    @Test
    void createJob() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(new ResponseEntity<>(TestObjects.createMockJob(), HttpStatus.CREATED));
        Job job = jobService.createJob(TestObjects.createMockJob());
        assertThat(job).isNotNull();
    }

    @Test
    void getJob() {
        when(restTemplate.getForEntity(anyString(), any())).thenReturn(new ResponseEntity<>(TestObjects.createMockJob(), HttpStatus.OK));
        Job job = jobService.getJob(1L);
        assertThat(job).isNotNull();
    }

    @Test
    void deleteJob() {
        when(restTemplate.exchange(anyString(), any(), any(), any(Class.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        jobService.deleteJob(1L);
    }

    @Test
    void getJobsByCompanyId() {
        when(restTemplate.getForEntity(anyString(), any())).thenReturn(new ResponseEntity<>(new Job[]{
                TestObjects.createMockJob()
        }, HttpStatus.OK));
        List<Job> jobs = jobService.getJobsByCompanyId(1L);
        assertThat(jobs).isNotNull();
        assertThat(jobs.size()).isEqualTo(1);
    }

    @Test
    void getAllJobs() {
        when(restTemplate.exchange(anyString(), any(), eq(null), any(ParameterizedTypeReference.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        Page<Job> jobs = jobService.getAllJobs(0, 10);
        assertThat(jobs).isNotNull();
    }

    @Test
    void applyForJob() {
        when(restTemplate.exchange(anyString(), any(), any(), any(Class.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        jobService.applyForJob(1L, TestObjects.createMockApplication());
    }

    @Test
    void getApplicationsForJob() {
        when(restTemplate.getForEntity(anyString(), any())).thenReturn(new ResponseEntity<>(new Application[]{
                TestObjects.createMockApplication()
        }, HttpStatus.OK));
        List<Application> applications = jobService.getApplicationsForJob(1L);
        assertThat(applications).isNotNull();
        assertThat(applications.size()).isEqualTo(1);
    }

    @Test
    void updateApplication() {
        when(restTemplate.exchange(anyString(), any(), any(), any(Class.class))).thenReturn(new ResponseEntity<>(TestObjects.createMockApplication(), HttpStatus.OK));
        jobService.updateApplication(1L, TestObjects.createMockApplicationDTO());
    }

    @Test
    void getApplicationsForUser() {
        when(restTemplate.getForEntity(anyString(), any())).thenReturn(new ResponseEntity<>(new Application[]{
                TestObjects.createMockApplication()
        }, HttpStatus.OK));
        List<Application> applications = jobService.getApplicationsForUser("userId");
        assertThat(applications).isNotNull();
        assertThat(applications.size()).isEqualTo(1);
    }

    @Test
    void searchJobs() {
        when(restTemplate.exchange(anyString(), any(), eq(null), any(ParameterizedTypeReference.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        Page<Job> jobs = jobService.searchJobs("search", 0, 10);
        assertThat(jobs).isNotNull();
    }

    @Test
    void filterJobs() {
        when(restTemplate.exchange(anyString(), any(), eq(null), any(ParameterizedTypeReference.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        HashMap<String, String> filters = new HashMap<>();
        filters.put("location", "Denmark");
        Page<Job> jobs = jobService.filterJobs(filters, 0, 10);
        assertThat(jobs).isNotNull();
    }

    @Test
    void update() {
        when(restTemplate.exchange(anyString(), any(), any(), any(Class.class))).thenReturn(new ResponseEntity<>(TestObjects.createMockJob(), HttpStatus.OK));
        jobService.update(1L, TestObjects.createMockJob());
    }
}