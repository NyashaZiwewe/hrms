package hrms.performance.repository;

import hrms.performance.entity.PerformanceGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformanceGoalRepository extends JpaRepository<PerformanceGoal, Long> {

    List<PerformanceGoal> findByContractId(Long contractId);
}
