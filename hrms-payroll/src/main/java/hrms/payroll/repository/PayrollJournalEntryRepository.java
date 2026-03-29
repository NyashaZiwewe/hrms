package hrms.payroll.repository;

import hrms.payroll.entity.PayrollJournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayrollJournalEntryRepository extends JpaRepository<PayrollJournalEntry, Long> {

    List<PayrollJournalEntry> findByPayrollRunId(Long payrollRunId);
}
