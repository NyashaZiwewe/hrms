package hrms.performance.repository;

import hrms.performance.entity.ReportingPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportingPeriodRepository extends JpaRepository<ReportingPeriod, Long> {

    Optional<ReportingPeriod> findByActiveTrue();

    boolean existsByActiveTrueAndIdNot(Long id);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
