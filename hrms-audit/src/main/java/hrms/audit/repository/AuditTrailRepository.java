package hrms.audit.repository;

import hrms.audit.entity.AuditTrail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditTrailRepository extends JpaRepository<AuditTrail, Long> {

    List<AuditTrail> findTop50ByOrderByChangedAtDesc();

    List<AuditTrail> findByModuleNameOrderByChangedAtDesc(String moduleName);
}
