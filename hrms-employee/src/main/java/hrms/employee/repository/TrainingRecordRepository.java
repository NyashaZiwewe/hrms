package hrms.employee.repository;

import hrms.employee.entity.*;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingRecordRepository extends JpaRepository<TrainingRecord, Long> {

    List<TrainingRecord> findByEmployeeId(Long employeeId);
}
