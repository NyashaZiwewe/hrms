package hrms.employee.repository;

import hrms.employee.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstitutionRepository extends JpaRepository<Institution, Long> {

    Optional<Institution> findByNameIgnoreCase(String name);
}
