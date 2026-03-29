package hrms.employee.entity;

import hrms.employee.model.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"employee"})
@Entity
@Table(name = "offboarding_records")
public class OffboardingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private LocalDate lastWorkingDate;

    @Column(nullable = false)
    private boolean exitInterviewCompleted;

    @Column(nullable = false)
    private boolean assetsReturned;

    @Column(nullable = false)
    private boolean financeClearanceCompleted;

    @Column(nullable = false)
    private boolean recordsClearanceCompleted;

    @Column(nullable = false)
    private boolean accountDeactivationCompleted;

    @Column(length = 1500)
    private String notes;
}
