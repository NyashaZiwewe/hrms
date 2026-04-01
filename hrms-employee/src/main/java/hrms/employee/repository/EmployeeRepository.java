package hrms.employee.repository;

import hrms.employee.entity.*;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeNumber(String employeeNumber);

    Optional<Employee> findByEmailIgnoreCase(String email);

    Optional<Employee> findByNationalId(String nationalId);

    List<Employee> findByDepartment_NameIgnoreCase(String department);

    List<Employee> findByHireDateBetween(LocalDate from, LocalDate to);
}
