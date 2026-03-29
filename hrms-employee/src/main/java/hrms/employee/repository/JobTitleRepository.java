package hrms.employee.repository;

import hrms.employee.entity.JobTitle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobTitleRepository extends JpaRepository<JobTitle, Long> {

    Optional<JobTitle> findByNameIgnoreCase(String name);
}
