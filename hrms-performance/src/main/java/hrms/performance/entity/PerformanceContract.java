package hrms.performance.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hrms.employee.entity.Employee;
import hrms.performance.model.ApprovalStatus;
import hrms.performance.model.LockStatus;
import hrms.performance.model.PerformanceStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"employee", "reportingPeriod"})
@Entity
@Table(name = "performance_contracts")
public class PerformanceContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporting_period_id", nullable = false)
    private ReportingPeriod reportingPeriod;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerformanceStatus status = PerformanceStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approvalStatus = ApprovalStatus.NEW;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LockStatus lockStatus = LockStatus.OPEN;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal employeeScore = BigDecimal.ZERO;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal managerScore = BigDecimal.ZERO;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal agreedScore = BigDecimal.ZERO;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal moderatedScore = BigDecimal.ZERO;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal weightedScore = BigDecimal.ZERO;

    @Column(length = 1500)
    private String employeeComment;

    @Column(length = 1500)
    private String managerComment;

    @Column(length = 1500)
    private String moderatorComment;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
