package hrms.payroll.repository;

import hrms.payroll.entity.PayrollEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayrollEntryRepository extends JpaRepository<PayrollEntry, Long> {

    List<PayrollEntry> findByPayrollRunId(Long payrollRunId);
}
