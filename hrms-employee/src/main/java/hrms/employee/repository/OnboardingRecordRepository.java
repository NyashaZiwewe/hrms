package hrms.employee.repository;

import hrms.employee.entity.*;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OnboardingRecordRepository extends JpaRepository<OnboardingRecord, Long> {

    List<OnboardingRecord> findByEmployeeId(Long employeeId);
}
