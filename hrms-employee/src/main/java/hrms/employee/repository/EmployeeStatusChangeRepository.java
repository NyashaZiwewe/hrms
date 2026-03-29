package hrms.employee.repository;

import hrms.employee.entity.*;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeStatusChangeRepository extends JpaRepository<EmployeeStatusChange, Long> {

    List<EmployeeStatusChange> findByEmployeeIdOrderByEffectiveDateDesc(Long employeeId);
}
