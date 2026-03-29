package hrms.employee.repository;

import hrms.employee.entity.*;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OffboardingRecordRepository extends JpaRepository<OffboardingRecord, Long> {

    List<OffboardingRecord> findByEmployeeId(Long employeeId);
}
