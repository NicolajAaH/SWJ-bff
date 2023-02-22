package dk.sdu.mmmi.backendforfrontend.service;

import dk.sdu.mmmi.backendforfrontend.service.interfaces.CompanyService;
import dk.sdu.mmmi.backendforfrontend.service.model.Company;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyServiceImplementation implements CompanyService {

    @Value("${url.companyservice}")
    private String COMPANY_SERVICE_URL;

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<Company> findAll() {
        log.info("--> findAll");
        Company[] companies = restTemplate.getForObject(COMPANY_SERVICE_URL + "/companies", Company[].class);
        if (companies == null){
            log.error("Error getting companies");
            return Collections.emptyList();
        }
        return List.of(companies);
    }

    @Override
    public Company create(Company company) {
        log.info("--> create: {}", company);
        ResponseEntity<Company> response = restTemplate.postForEntity(COMPANY_SERVICE_URL + "/register", company, Company.class);
        if(!response.getStatusCode().is2xxSuccessful()){
            log.error("Error creating job: {}", response.getStatusCode());
            return null;
        }
        return response.getBody();
    }


    @Override
    public Company findById(Long id) {
        log.info("--> findById: {}", id);
        ResponseEntity<Company> response = restTemplate.getForEntity(COMPANY_SERVICE_URL + "/" + id, Company.class);
        if(!response.getStatusCode().is2xxSuccessful()){
            log.error("Error getting job: {}", response.getStatusCode());
            return null;
        }
        return response.getBody();
    }

    @Override
    public Company update(Long id, Company company) {
        log.info("--> update: {}", company);
        company.setUpdatedAt(new Date());
        ResponseEntity<Company> response = restTemplate.exchange(COMPANY_SERVICE_URL + "/" + id, HttpMethod.PUT, new HttpEntity<>(company), Company.class);
        if(!response.getStatusCode().is2xxSuccessful()){
            log.error("Error updating job: {}", response.getStatusCode());
            return null;
        }
        return response.getBody();
    }

    @Override
    public void delete(Long id) {
        log.info("--> delete: {}", id);
        restTemplate.delete(COMPANY_SERVICE_URL + "/" + id);
    }

    @Override
    public Company getCompanyByEmail(String email) {
        log.info("--> getCompanyByEmail: {}", email);
        ResponseEntity<Company> response = restTemplate.getForEntity(COMPANY_SERVICE_URL + "/byEmail/" + email, Company.class);
        if(!response.getStatusCode().is2xxSuccessful()){
            log.error("Error getting company: {}", response.getStatusCode());
            return null;
        }
        return response.getBody();
    }

    @Override
    public Company findByEmail(long email) {
        log.info("--> findByEmail: {}", email);
        ResponseEntity<Company> response = restTemplate.getForEntity(COMPANY_SERVICE_URL + "/byEmail/" + email, Company.class);
        if(!response.getStatusCode().is2xxSuccessful()){
            log.error("Error getting company: {}", response.getStatusCode());
            return null;
        }
        return response.getBody();
    }
}
