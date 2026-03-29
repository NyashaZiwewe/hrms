package hrms.employee.repository;

import hrms.employee.entity.*;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeCaseRepository extends JpaRepository<EmployeeCase, Long> {

    List<EmployeeCase> findByEmployeeId(Long employeeId);
}
