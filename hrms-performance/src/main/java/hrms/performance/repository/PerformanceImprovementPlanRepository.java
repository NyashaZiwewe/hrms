package hrms.performance.repository;

import hrms.performance.entity.PerformanceImprovementPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformanceImprovementPlanRepository extends JpaRepository<PerformanceImprovementPlan, Long> {

    List<PerformanceImprovementPlan> findByEmployeeId(Long employeeId);

    boolean existsByReportingPeriodId(Long reportingPeriodId);
}
