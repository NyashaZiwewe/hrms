package hrms.employee.repository;

import hrms.employee.entity.EducationLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EducationLevelRepository extends JpaRepository<EducationLevel, Long> {

    Optional<EducationLevel> findByNameIgnoreCase(String name);
}
