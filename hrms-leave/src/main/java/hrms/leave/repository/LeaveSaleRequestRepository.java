package hrms.leave.repository;

import hrms.leave.entity.LeaveSaleRequest;
import hrms.leave.model.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveSaleRequestRepository extends JpaRepository<LeaveSaleRequest, Long> {

    List<LeaveSaleRequest> findByEmployeeId(Long employeeId);

    List<LeaveSaleRequest> findByEmployeeIdAndStatus(Long employeeId, LeaveStatus status);

    List<LeaveSaleRequest> findByStatusAndPayrollProcessed(LeaveStatus status, boolean payrollProcessed);
}
