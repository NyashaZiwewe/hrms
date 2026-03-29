package hrms.performance.repository;

import hrms.performance.entity.Perspective;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerspectiveRepository extends JpaRepository<Perspective, Long> {
}
