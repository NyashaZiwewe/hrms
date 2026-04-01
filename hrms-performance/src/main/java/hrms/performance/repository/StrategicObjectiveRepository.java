package hrms.performance.repository;

import hrms.performance.entity.StrategicObjective;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StrategicObjectiveRepository extends JpaRepository<StrategicObjective, Long> {

    List<StrategicObjective> findAllByOrderByReportingPeriodStartDateDescPerspectiveNameAscNameAsc();

    List<StrategicObjective> findByReportingPeriodIdOrderByPerspectiveNameAscNameAsc(Long reportingPeriodId);

    boolean existsByReportingPeriodId(Long reportingPeriodId);
}
