package hrms.leave.repository;

import hrms.leave.entity.OvertimeClaim;
import hrms.leave.model.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OvertimeClaimRepository extends JpaRepository<OvertimeClaim, Long> {

    List<OvertimeClaim> findByEmployeeId(Long employeeId);

    List<OvertimeClaim> findByEmployeeIdAndStatus(Long employeeId, LeaveStatus status);

    List<OvertimeClaim> findByStatusAndPayrollProcessed(LeaveStatus status, boolean payrollProcessed);
}
