package hrms.employee.repository;

import hrms.employee.entity.EmployeeContract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeContractRepository extends JpaRepository<EmployeeContract, Long> {

    List<EmployeeContract> findByEmployeeId(Long employeeId);
}
