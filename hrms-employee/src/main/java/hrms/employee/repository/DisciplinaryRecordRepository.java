package hrms.employee.repository;

import hrms.employee.entity.DisciplinaryRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisciplinaryRecordRepository extends JpaRepository<DisciplinaryRecord, Long> {

    List<DisciplinaryRecord> findByEmployeeIdOrderByRecordDateDesc(Long employeeId);
}
