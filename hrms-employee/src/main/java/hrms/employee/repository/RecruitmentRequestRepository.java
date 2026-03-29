package hrms.employee.repository;

import hrms.employee.entity.*;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitmentRequestRepository extends JpaRepository<RecruitmentRequest, Long> {
}
