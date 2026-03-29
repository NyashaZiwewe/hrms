package hrms.employee.repository;

import hrms.employee.entity.EmploymentConfirmationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmploymentConfirmationRequestRepository extends JpaRepository<EmploymentConfirmationRequest, Long> {

    List<EmploymentConfirmationRequest> findByEmployeeIdOrderByRequestedDateDesc(Long employeeId);
}
