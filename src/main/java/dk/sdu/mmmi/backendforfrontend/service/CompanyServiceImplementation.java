package dk.sdu.mmmi.backendforfrontend.service;

import dk.sdu.mmmi.backendforfrontend.service.interfaces.CompanyService;
import dk.sdu.mmmi.backendforfrontend.service.model.Company;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyServiceImplementation implements CompanyService {


    @Override
    public List<Company> findAll() {
        log.info("--> findAll");
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
        return null;
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
