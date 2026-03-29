package hrms.employee.repository;

import hrms.employee.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {

    Optional<Grade> findByNameIgnoreCase(String name);
}
