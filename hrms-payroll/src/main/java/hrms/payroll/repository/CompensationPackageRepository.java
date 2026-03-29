package hrms.payroll.repository;

import hrms.payroll.entity.CompensationPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompensationPackageRepository extends JpaRepository<CompensationPackage, Long> {

    Optional<CompensationPackage> findByEmployeeId(Long employeeId);
}
