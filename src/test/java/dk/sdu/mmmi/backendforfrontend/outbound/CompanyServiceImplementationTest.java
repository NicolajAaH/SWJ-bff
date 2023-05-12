package dk.sdu.mmmi.backendforfrontend.outbound;

import dk.sdu.mmmi.backendforfrontend.TestObjects;
import dk.sdu.mmmi.backendforfrontend.service.interfaces.CompanyService;
import dk.sdu.mmmi.backendforfrontend.service.model.Company;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CompanyServiceImplementationTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CompanyService companyService = new CompanyServiceImplementation();

    @Test
    void findAll() {
        when(restTemplate.getForObject(anyString(), any())).thenReturn(new Company[]{
            TestObjects.createMockCompany()
        });
        List<Company> companies = companyService.findAll();
        assertThat(companies).isNotNull();
        assertThat(companies.size()).isEqualTo(1);
    }

    @Test
    void create() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(new ResponseEntity<>(TestObjects.createMockCompany(), HttpStatus.OK));
        Company company = companyService.create(TestObjects.createMockCompany());
        assertThat(company).isNotNull();
    }

    @Test
    void findById() {
        when(restTemplate.getForEntity(anyString(), any())).thenReturn(new ResponseEntity<>(TestObjects.createMockCompany(), HttpStatus.OK));
        Company company = companyService.findById(1L);
        assertThat(company).isNotNull();
    }

    @Test
    void testUpdateId() {
        when(restTemplate.exchange(anyString(), any(), any(), any(Class.class))).thenReturn(new ResponseEntity<>(TestObjects.createMockCompany(), HttpStatus.OK));
        Company company = companyService.update(1L, TestObjects.createMockCompany());
        assertThat(company).isNotNull();
    }

    @Test
    void testUpdateEmail() {
        when(restTemplate.exchange(anyString(), any(), any(), any(Class.class))).thenReturn(new ResponseEntity<>(TestObjects.createMockCompany(), HttpStatus.OK));
        Company company = companyService.update("test@test.dk", TestObjects.createMockCompany());
        assertThat(company).isNotNull();
    }

    @Test
    void delete() {
        when(restTemplate.exchange(anyString(), any(), any(), any(Class.class))).thenReturn(new ResponseEntity<>(TestObjects.createMockCompany(), HttpStatus.OK));
        companyService.delete(1L);
    }

    @Test
    void getCompanyByEmail() {
        when(restTemplate.getForEntity(anyString(), any())).thenReturn(new ResponseEntity<>(TestObjects.createMockCompany(), HttpStatus.OK));
        Company company = companyService.getCompanyByEmail("test@test.dk");
        assertThat(company).isNotNull();
    }
}