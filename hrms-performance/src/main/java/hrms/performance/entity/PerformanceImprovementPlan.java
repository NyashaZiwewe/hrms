package hrms.performance.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hrms.employee.entity.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"employee", "reportingPeriod"})
@Entity
@Table(name = "performance_improvement_plans")
public class PerformanceImprovementPlan {

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
    private String targetArea;

    @Column(length = 1000)
    private String concern;

    @Column(length = 1000)
    private String expectedStandard;

    @Column(length = 1000)
    private String agreedAction;

    @Column(length = 1000)
    private String requiredSupport;

    @Column(length = 1000)
    private String reviewNotes;

    @Column(nullable = false)
    private Integer progress = 0;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date endDate;
}
