package dk.sdu.mmmi.backendforfrontend;

import dk.sdu.mmmi.backendforfrontend.service.model.*;

import java.util.Date;

public class TestObjects {

    public static Job createMockJob(){
        Job job = new Job();
        job.setTitle("Test Title");
        job.setDescription("This is a test job");
        job.setLocation("Test City");
        job.setSalary(100000.00);
        job.setJobType(JobType.BACKEND);
        job.setCreatedAt(new java.util.Date());
        job.setCompanyId(1L);
        job.setId(1L);
        job.setExpiresAt(new java.util.Date());
        job.setUpdatedAt(new java.util.Date());
        return job;
    }

    public static Company createMockCompany(){
        Company company = new Company();
        company.setName("Mock Company");
        company.setWebsite("https://test.dk");
        company.setCreatedAt(new java.util.Date());
        company.setUpdatedAt(new java.util.Date());
        company.setId(1L);
        company.setPhone(12345678);
        return company;
    }

    public static User createMockUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@email.dk");
        user.setRole(UserRole.APPLICANT);
        user.setPassword("password");
        user.setPhone(12345678);
        user.setCreatedAt(new Date());
        return user;
    }

    public static LoginRequest createMockLoginRequest() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@email.dk");
        loginRequest.setPassword("password");
        return loginRequest;
    }

    public static Application createMockApplication() {
        Application application = new Application();
        application.setJobId(1L);
        application.setUserId("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InRlc3RAZW1haWwiLCJ1c2VySWQiOiIxMjMxMjNka2siLCJyb2xlIjoiRlJPTlRFTkQiLCJpYXQiOjE1MTYyMzkwMjJ9.d53lOw8k51us5bwR7vpKFCvmPBgdG38ND_xbpZ6oFkQ");
        application.setCreatedAt(new java.util.Date());
        application.setUpdatedAt(new java.util.Date());
        application.setId(1L);
        return application;
    }

    public static LogoutRequest createMockLogoutRequest() {
        LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.setToken("token");
        return logoutRequest;
    }
}
