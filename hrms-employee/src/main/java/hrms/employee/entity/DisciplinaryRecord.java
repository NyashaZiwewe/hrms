package hrms.employee.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hrms.employee.model.DisciplinaryRecordType;
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
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"employee", "hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "disciplinary_records")
public class DisciplinaryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DisciplinaryRecordType recordType;

    @Column(nullable = false)
    private LocalDate recordDate;

    @Column
    private LocalDate effectiveFrom;

    @Column
    private LocalDate effectiveTo;

    @Column(length = 255)
    private String subject;

    @Column(length = 2000)
    private String details;

    @Column(length = 1000)
    private String outcome;
}
