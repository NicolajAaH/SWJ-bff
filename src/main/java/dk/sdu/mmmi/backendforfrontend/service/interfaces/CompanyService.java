package dk.sdu.mmmi.backendforfrontend.service.interfaces;

import dk.sdu.mmmi.backendforfrontend.service.model.Company;

import java.util.List;

public interface CompanyService {
    List<Company> findAll();

    Company create(Company company);

    Company findById(Long id);

    Company update(Long id, Company company);

    void delete(Long id);

    Company getCompanyByEmail(String email);

    Company findByEmail(String email);
}
