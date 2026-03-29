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

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"positionOwner", "successor"})
@Entity
@Table(name = "succession_plans")
public class SuccessionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "position_owner_employee_id", nullable = false)
    private Employee positionOwner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "successor_employee_id", nullable = false)
    private Employee successor;

    @Column(nullable = false)
    private String readinessLevel;

    @Column(length = 1500)
    private String developmentActions;
}
