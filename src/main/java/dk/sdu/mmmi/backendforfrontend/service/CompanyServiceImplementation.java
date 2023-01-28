package dk.sdu.mmmi.backendforfrontend.service;

import dk.sdu.mmmi.backendforfrontend.service.interfaces.CompanyService;
import dk.sdu.mmmi.backendforfrontend.service.model.Company;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyServiceImplementation implements CompanyService {

    @Value("${url.companyservice}")
    private String JOB_SERVICE_URL;

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<Company> findAll() {
        log.info("--> findAll");
        //Company[] companies = restTemplate.getForObject(JOB_SERVICE_URL + "/companies", List.class);
        return null;
    }

    @Override
    public Company create(Company company) {
        log.info("--> create: {}", company);
        return null;
    }


    @Override
    public Company findById(Long id) {
        log.info("--> findById: {}", id);
        ResponseEntity<Company> response = restTemplate.getForEntity(JOB_SERVICE_URL + "/" + id, Company.class);
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
        return null;
    }

    @Override
    public void delete(Long id) {
        log.info("--> delete: {}", id);
    }
}
