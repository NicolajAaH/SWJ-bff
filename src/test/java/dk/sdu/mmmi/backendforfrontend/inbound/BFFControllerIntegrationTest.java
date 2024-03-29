package dk.sdu.mmmi.backendforfrontend.inbound;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.sdu.mmmi.backendforfrontend.JwtTestUtils;
import dk.sdu.mmmi.backendforfrontend.TestObjects;
import dk.sdu.mmmi.backendforfrontend.service.application.BackendForFrontendApplication;
import dk.sdu.mmmi.backendforfrontend.service.interfaces.AuthenticationService;
import dk.sdu.mmmi.backendforfrontend.service.interfaces.CompanyService;
import dk.sdu.mmmi.backendforfrontend.service.interfaces.JobService;
import dk.sdu.mmmi.backendforfrontend.service.model.Job;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = BackendForFrontendApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("unitTest")
class BFFControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JobService jobService;

    @MockBean
    private CompanyService companyService;


    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getCompanyEmail() throws Exception {
        String email = "test@test.dk";
        String jwt = JwtTestUtils.createMockJwt("testuser", "COMPANY");

        when(companyService.getCompanyByEmail(anyString())).thenReturn(TestObjects.createMockCompany());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/bff/company/" + email)
                .header("Authorization", "Bearer " + jwt)
        ).andExpect(status().is2xxSuccessful());
    }

    @Test
    void getCompanyNullEmail() throws Exception {
        String wrongEmail = "wrongMail";
        when(companyService.findById(anyLong())).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/bff/company/" + wrongEmail)).andExpect(status().is4xxClientError());
    }

    @Test
    void registerCompany() throws Exception {
        String jwt = JwtTestUtils.createMockJwt("testuser", "COMPANY");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/bff/companies/register")
                .contentType("application/json")
                        .header("Authorization", "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(TestObjects.createMockCompany())))
                .andExpect(status().isCreated());
    }

    @Test
    void getUser() throws Exception {
        when(authenticationService.getUser(anyString())).thenReturn(TestObjects.createMockUser());
        String jwt = JwtTestUtils.createMockJwt("testuser", "APPLICANT");
        mockMvc.perform(MockMvcRequestBuilders.get("/api/bff/auth/user/12345abc")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk());
    }

    @Test
    void getUserDeny() throws Exception {
        String jwt = JwtTestUtils.createMockJwt("testuser", "APPLICANT");
        mockMvc.perform(MockMvcRequestBuilders.get("/api/bff/auth/user/10")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isForbidden());
    }

    @Test
    void registerCompanyNoBody() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/bff/companies/register"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void registerUser() throws Exception{
        String jwt = JwtTestUtils.createMockJwt("testuser", "COMPANY");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/bff/auth/register")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(TestObjects.createMockUser())))
                        .andExpect(status().isCreated());
    }

    @Test
    void registerUserNoBody() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.post("/api/bff/auth/register"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void login() throws Exception{
        when(authenticationService.login(any())).thenReturn(TestObjects.createMockTokenResponse());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/bff/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(TestObjects.createMockLoginRequest())))
                .andExpect(status().isOk());
    }

    @Test
    void loginNoBody() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.post("/api/bff/auth/login"))
                .andExpect(status().is4xxClientError());
    }


    @Test
    void logout() throws Exception {
        String jwt = JwtTestUtils.createMockJwt("testuser", "COMPANY");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/bff/auth/logout")
                        .header("Authorization", "Bearer " + jwt)
                .contentType("application/json").content(objectMapper.writeValueAsString(TestObjects.createMockLogoutRequest()))).andExpect(status().isOk());
    }

    @Test
    void logoutNoBody() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/bff/auth/logout")).andExpect(status().is4xxClientError());
    }

    @Test
    void postJob() throws Exception {
        when(jobService.createJob(any(Job.class))).thenReturn(TestObjects.createMockJob());
        String email = "email";
        String jwt = JwtTestUtils.createMockJwt("testuser", "COMPANY");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/bff/job/" + email)
                .contentType("application/json")
                        .header("Authorization", "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(TestObjects.createMockJob())))
                .andExpect(status().isOk());
    }

    @Test
    void postJobNoBody() throws Exception {
        String email = "email";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/bff/job/" + email))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getJobWithCompany() throws Exception {
        String jwt = JwtTestUtils.createMockJwt("testuser", "COMPANY");
        when(jobService.getJob(anyLong())).thenReturn(TestObjects.createMockJob());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/bff/job/1")
                .header("Authorization", "Bearer " + jwt)
        ).andExpect(status().isOk());
    }

    @Test
    void getJobWithCompanyNull() throws Exception {
        when(jobService.getJob(anyLong())).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/bff/job/1")).andExpect(status().is4xxClientError());
    }

    @Test
    void applyForJob() throws Exception {
        String jwt = JwtTestUtils.createMockJwt("testuser", "APPLICANT");
        when(jobService.getJob(anyLong())).thenReturn(TestObjects.createMockJob());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/bff/job/1/apply").contentType("application/json")
                .header("Authorization", "Bearer " + jwt)
                .content(objectMapper.writeValueAsString(TestObjects.createMockApplication()))).andExpect(status().isOk());
    }

    @Test
    void applyForJobNoBody() throws Exception {
        when(jobService.getJob(anyLong())).thenReturn(TestObjects.createMockJob());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/bff/job/1/apply").contentType("application/json")).andExpect(status().is4xxClientError());
    }

    @Test
    void getAllJobs() throws Exception {
        when(jobService.getAllJobs(anyInt(), anyInt())).thenReturn(new PageImpl<>(List.of(TestObjects.createMockJob())));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/bff/job")).andExpect(status().is2xxSuccessful());
    }

    @Test
    void updateCompany() throws Exception {
        String jwt = JwtTestUtils.createMockJwt("testuser", "COMPANY");
        mockMvc.perform(MockMvcRequestBuilders.put("/api/bff/company/1")
                .contentType("application/json")
                        .header("Authorization", "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(TestObjects.createMockCompany())))
                .andExpect(status().isOk());
    }

    @Test
    void updateCompanyNoBody() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/bff/company/1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getApplicationsForJob() throws Exception {
        String jwt = JwtTestUtils.createMockJwt("testuser", "COMPANY");
        when(jobService.getApplicationsForJob(anyLong())).thenReturn(new ArrayList<>(){{
            TestObjects.createMockApplication();
        }});
        mockMvc.perform(MockMvcRequestBuilders.get("/api/bff/job/1/applications")
                .header("Authorization", "Bearer " + jwt)
        ).andExpect(status().is2xxSuccessful());
    }

    @Test
    void getApplicationsForUser() throws Exception {
        String jwt = JwtTestUtils.createMockJwt("testuser", "APPLICANT");
        when(jobService.getApplicationsForJob(anyLong())).thenReturn(new ArrayList<>(){{
            TestObjects.createMockApplication();
        }});
        mockMvc.perform(MockMvcRequestBuilders.get("/api/bff/applications/userId")
                .header("Authorization", "Bearer " + jwt)
        ).andExpect(status().is2xxSuccessful());
    }

    @Test
    void getApplicationsForJobNoApplications() throws Exception {
        when(jobService.getApplicationsForJob(anyLong())).thenReturn(Collections.emptyList());
        String jwt = JwtTestUtils.createMockJwt("testuser", "COMPANY");
        mockMvc.perform(MockMvcRequestBuilders.get("/api/bff/job/1/applications")
                .header("Authorization", "Bearer " + jwt)).andExpect(status().is2xxSuccessful());
    }

    @Test
    void getApplicationsForJobNull() throws Exception {
        String jwt = JwtTestUtils.createMockJwt("testuser", "COMPANY");
        when(jobService.getApplicationsForJob(anyLong())).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/bff/job/1/applications")
                .header("Authorization", "Bearer " + jwt)
        ).andExpect(status().isNoContent());
    }

    @Test
    @DirtiesContext
    void deleteUser() throws Exception {
        String jwt = JwtTestUtils.createMockJwt("testuser", "COMPANY");
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/bff/auth/user/12345abc")
                .header("Authorization", "Bearer " + jwt)
        ).andExpect(status().isNoContent());
    }

    @Test
    void getCompany() throws Exception {
        when(companyService.findById(anyLong())).thenReturn(TestObjects.createMockCompany());
        when(jobService.getJobsByCompanyId(anyLong())).thenReturn(new ArrayList<>(){{
            TestObjects.createMockJob();
        }});
        mockMvc.perform(MockMvcRequestBuilders.get("/api/bff/company/byId/" + 1)).andExpect(status().is2xxSuccessful());
    }

    @Test
    @DirtiesContext
    void updateApplication() throws Exception{
        doNothing().when(jobService).updateApplication(anyLong(), any());
        String jwt = JwtTestUtils.createMockJwt("testuser", "COMPANY");
        mockMvc.perform(MockMvcRequestBuilders.put("/api/bff/application/1")
                .contentType("application/json")
                .header("Authorization", "Bearer " + jwt)
                .content(objectMapper.writeValueAsString(TestObjects.createMockApplication()))
        ).andExpect(status().isOk());
    }

    @Test
    @DirtiesContext
    void updateJob() throws Exception {
        doNothing().when(jobService).update(anyLong(), any());
        String jwt = JwtTestUtils.createMockJwt("testuser", "COMPANY");
        mockMvc.perform(MockMvcRequestBuilders.put("/api/bff/job/1")
                .contentType("application/json")
                .header("Authorization", "Bearer " + jwt)
                .content(objectMapper.writeValueAsString(TestObjects.createMockJob()))
        ).andExpect(status().isOk());
    }

    @Test
    void getCompanyNull() throws Exception {
        when(companyService.findById(anyLong())).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/bff/company/byId/" + 999)).andExpect(status().is4xxClientError());
    }

    @Test
    void filterJobs() throws Exception {
        when(jobService.filterJobs(anyMap(), anyInt(), anyInt())).thenReturn(new PageImpl<>(List.of(TestObjects.createMockJob())));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/bff/job/filter?location=Denmark&page=0&size=10")).andExpect(status().is2xxSuccessful());
    }

    @Test
    void filterJobsNoPageAndSize() throws Exception {
        when(jobService.filterJobs(anyMap(), anyInt(), anyInt())).thenReturn(new PageImpl<>(List.of(TestObjects.createMockJob())));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/bff/job/filter?location=Denmark")).andExpect(status().is2xxSuccessful());
    }

    @Test
    void filterJobsNotNumberPageAndSize() throws Exception {
        when(jobService.filterJobs(anyMap(), anyInt(), anyInt())).thenReturn(new PageImpl<>(List.of(TestObjects.createMockJob())));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/bff/job/filter?location=Denmark&page=zero&size=five")).andExpect(status().is2xxSuccessful());
    }


    @Test
    void searchJobs() throws Exception {
        when(jobService.searchJobs(anyString(), anyInt(), anyInt())).thenReturn(new PageImpl<>(List.of(TestObjects.createMockJob())));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/bff/job/search/Java?page=0&size=10")).andExpect(status().is2xxSuccessful());
    }

}