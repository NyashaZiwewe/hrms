package hrms.employee.entity;

import hrms.employee.model.EmploymentConfirmationStatus;
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
@Entity
@Table(name = "employment_confirmation_requests")
public class EmploymentConfirmationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private LocalDate requestedDate;

    @Column(length = 1000)
    private String purpose;

    @Column(nullable = false)
    private String deliveryEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentConfirmationStatus status = EmploymentConfirmationStatus.REQUESTED;

    @Column
    private Long signedByEmployeeId;

    @Column
    private LocalDate signedDate;

    @Column
    private String signedDocumentPath;

    @Column
    private String signedDocumentFileName;
}
