package dk.sdu.mmmi.backendforfrontend.service;

import dk.sdu.mmmi.backendforfrontend.TestObjects;
import dk.sdu.mmmi.backendforfrontend.service.interfaces.AuthenticationService;
import dk.sdu.mmmi.backendforfrontend.service.interfaces.BFFService;
import dk.sdu.mmmi.backendforfrontend.service.interfaces.CompanyService;
import dk.sdu.mmmi.backendforfrontend.service.interfaces.JobService;
import dk.sdu.mmmi.backendforfrontend.service.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class BFFServiceImplementationTest {

    @Mock
    private CompanyService companyService;

    @Mock
    private JobService jobService;

    @Mock
    private AuthenticationService authenticationService;

    private BFFService bffService;


    @BeforeEach
    void setUp() {
        bffService = new BFFServiceImplementation(companyService, jobService, authenticationService);
    }

    @Test
    void getCompany() {
        when(companyService.findByEmail(anyString())).thenReturn(TestObjects.createMockCompany());
        Company company = bffService.getCompany("test@test.dk");
        assertThat(company).isNotNull();
        assertThat(company.getName()).isEqualTo(TestObjects.createMockCompany().getName());
    }

    @Test
    void registerCompany() {
        when(companyService.create(any(Company.class))).thenReturn(TestObjects.createMockCompany());
        bffService.registerCompany(TestObjects.createMockCompany());

        verify(companyService, times(1)).create(any(Company.class));
        verifyNoMoreInteractions(companyService);
    }

    @Test
    void registerUser() {
        doNothing().when(authenticationService).register(any(User.class));

        bffService.registerUser(TestObjects.createMockUser());

        verify(authenticationService, times(1)).register(any(User.class));
        verifyNoMoreInteractions(authenticationService);
    }

    @Test
    void login() {
        when(authenticationService.login(any(LoginRequest.class))).thenReturn(TestObjects.createMockTokenResponse());

        TokenResponse tokenResponse = bffService.login(TestObjects.createMockLoginRequest());

        assertThat(tokenResponse).isNotNull();
        assertThat(tokenResponse.getToken()).isEqualTo(TestObjects.createMockTokenResponse().getToken());
    }

    @Test
    void logout() {
        doNothing().when(authenticationService).logout(any(LogoutRequest.class));

        bffService.logout(TestObjects.createMockLogoutRequest());

        verify(authenticationService, times(1)).logout(any(LogoutRequest.class));
        verifyNoMoreInteractions(authenticationService);
    }

    @Test
    void postJob() {
        when(jobService.createJob(any(Job.class))).thenReturn(TestObjects.createMockJob());
        when(companyService.findByEmail(anyString())).thenReturn(TestObjects.createMockCompany());

        Job job = bffService.postJob("test@test.dk", TestObjects.createMockJob());

        assertThat(job).isNotNull();
        assertThat(job.getTitle()).isEqualTo(TestObjects.createMockJob().getTitle());
    }

    @Test
    void getJobWithCompany() {
        when(jobService.getJob(anyLong())).thenReturn(TestObjects.createMockJob());
        when(companyService.findByEmail(anyString())).thenReturn(TestObjects.createMockCompany());

        JobDTO job = bffService.getJobWithCompany(2L);

        assertThat(job).isNotNull();
        assertThat(job.getTitle()).isEqualTo(TestObjects.createMockJob().getTitle());
    }

    @Test
    void applyForJob() {
        when(jobService.getJob(anyLong())).thenReturn(TestObjects.createMockJob());
        doNothing().when(jobService).applyForJob(anyLong(), any(Application.class));

        bffService.applyForJob(2L, TestObjects.createMockApplication());

        verify(jobService, times(1)).applyForJob(anyLong(), any(Application.class));
        verify(jobService, times(1)).getJob(anyLong());
        verifyNoMoreInteractions(jobService);
    }

    @Test
    void getAllJobs() {
        when(jobService.getAllJobs(1, 1)).thenReturn(new PageImpl<>(TestObjects.createMockJobList()));

        Page<Job> jobs = bffService.getAllJobs(Pageable.ofSize(1).withPage(1));

        assertThat(jobs).isNotNull();
        assertThat(jobs.getTotalElements()).isEqualTo(1);
    }

    @Test
    void updateCompany() {
        when(companyService.update(anyLong(), any(Company.class))).thenReturn(TestObjects.createMockCompany());

        bffService.updateCompany(TestObjects.createMockCompany(), 1L);

        verify(companyService, times(1)).update(anyLong(), any(Company.class));
        verifyNoMoreInteractions(companyService);
    }

    @Test
    void testUpdateCompany() {
        when(companyService.update(anyString(), any(Company.class))).thenReturn(TestObjects.createMockCompany());

        bffService.updateCompany(TestObjects.createMockCompany(), "test@company.dk");

        verify(companyService, times(1)).update(anyString(), any(Company.class));
        verifyNoMoreInteractions(companyService);
    }

    @Test
    void getApplicationsForJob() {
        when(jobService.getApplicationsForJob(anyLong())).thenReturn(new ArrayList<>() {{
            add(TestObjects.createMockApplication());
        }});

        List<ApplicationDTO> applications = bffService.getApplicationsForJob(2L);

        assertThat(applications).isNotNull();
        assertThat(applications.size()).isEqualTo(1);
        assertThat(applications.get(0).getApplication()).isEqualTo(TestObjects.createMockApplication().getApplication());
    }

    @Test
    void updateApplication() {
        doNothing().when(jobService).updateApplication(anyLong(), any(ApplicationDTO.class));

        bffService.updateApplication(TestObjects.createMockApplicationDTO(), 1L);

        verify(jobService, times(1)).updateApplication(anyLong(), any(ApplicationDTO.class));
        verifyNoMoreInteractions(jobService);
    }

    @Test
    void getApplicationsForUser() {
        when(jobService.getApplicationsForUser(anyString())).thenReturn(new ArrayList<>() {{
            add(TestObjects.createMockApplication());
        }});

        List<ApplicationDTO> applications = bffService.getApplicationsForUser("userId");

        assertThat(applications).isNotNull();
        assertThat(applications.size()).isEqualTo(1);
        assertThat(applications.get(0).getApplication()).isEqualTo(TestObjects.createMockApplication().getApplication());
    }

    @Test
    void searchJobs() {
        when(jobService.searchJobs(anyString(), anyInt(), anyInt())).thenReturn(new PageImpl<>(TestObjects.createMockJobList()));

        Page<Job> jobs = bffService.searchJobs("test", Pageable.ofSize(1).withPage(1));

        assertThat(jobs).isNotNull();
        assertThat(jobs.getTotalElements()).isEqualTo(1);
    }

    @Test
    void filterJobs() {
        Map<String, String> filter = new HashMap<>();
        filter.put("test", "test");
        when(jobService.filterJobs(anyMap(), anyInt(), anyInt())).thenReturn(new PageImpl<>(TestObjects.createMockJobList()));

        Page<Job> jobs = bffService.filterJobs(filter, Pageable.ofSize(1).withPage(1));

        assertThat(jobs).isNotNull();
        assertThat(jobs.getTotalElements()).isEqualTo(1);
    }

    @Test
    void updateJob() {
        doNothing().when(jobService).update(anyLong(), any(Job.class));

        bffService.updateJob(TestObjects.createMockJob(), 1L);

        verify(jobService, times(1)).update(anyLong(), any(Job.class));
        verifyNoMoreInteractions(jobService);
    }

    @Test
    void updateUser() {
        doNothing().when(authenticationService).updateUser(anyString(), any(User.class));

        bffService.updateUser("userId", TestObjects.createMockUser());

        verify(authenticationService, times(1)).updateUser(anyString(), any(User.class));
        verifyNoMoreInteractions(authenticationService);
    }

    @Test
    void getUser() {
        when(authenticationService.getUser(anyString())).thenReturn(TestObjects.createMockUser());

        User user = bffService.getUser("userId");

        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(TestObjects.createMockUser().getEmail());
    }

    @Test
    void deleteUser() {
        doNothing().when(authenticationService).deleteUser(anyString());

        bffService.deleteUser("userId");

        verify(authenticationService, times(1)).deleteUser(anyString());
        verifyNoMoreInteractions(authenticationService);
    }

    @Test
    void testGetCompany() {
        when(companyService.findByEmail(anyString())).thenReturn(TestObjects.createMockCompany());

        Company company = bffService.getCompany("test@company.dk");

        assertThat(company).isNotNull();
        assertThat(company.getName()).isEqualTo(TestObjects.createMockCompany().getName());
    }
}