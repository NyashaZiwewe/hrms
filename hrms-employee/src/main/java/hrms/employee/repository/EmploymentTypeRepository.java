package hrms.employee.repository;

import hrms.employee.entity.EmploymentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmploymentTypeRepository extends JpaRepository<EmploymentType, Long> {

    Optional<EmploymentType> findByNameIgnoreCase(String name);
}
