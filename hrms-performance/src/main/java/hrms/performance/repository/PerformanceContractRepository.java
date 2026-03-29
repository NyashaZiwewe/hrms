package hrms.performance.repository;

import hrms.performance.entity.PerformanceContract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformanceContractRepository extends JpaRepository<PerformanceContract, Long> {

    List<PerformanceContract> findByEmployeeId(Long employeeId);
}
