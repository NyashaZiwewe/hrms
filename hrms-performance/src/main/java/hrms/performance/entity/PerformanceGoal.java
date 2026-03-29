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
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"contract", "perspective", "assignedEmployee"})
@Entity
@Table(name = "performance_goals")
public class PerformanceGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contract_id", nullable = false)
    private PerformanceContract contract;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "perspective_id", nullable = false)
    private Perspective perspective;

    @Column(nullable = false)
    private String name;

    @Column(length = 1500)
    private String strategicObjective;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal allocatedWeight = BigDecimal.ZERO;

    @Column(length = 1000)
    private String measure;

    @Column(length = 1000)
    private String targetValue;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal selfScore = BigDecimal.ZERO;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal managerScore = BigDecimal.ZERO;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal agreedScore = BigDecimal.ZERO;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal moderatedScore = BigDecimal.ZERO;

    @Column(length = 1000)
    private String skillGap;

    @Column(length = 1000)
    private String trainingNeed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_employee_id")
    private Employee assignedEmployee;
}
