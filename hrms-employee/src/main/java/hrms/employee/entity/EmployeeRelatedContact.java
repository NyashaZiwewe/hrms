package hrms.employee.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hrms.employee.model.RelatedContactType;
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

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"employee", "hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "employee_related_contacts")
public class EmployeeRelatedContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RelatedContactType contactType;

    @Column(nullable = false)
    private String fullName;

    @Column(length = 100)
    private String relationshipDescription;

    @Column(length = 100)
    private String phoneNumber;

    @Column(length = 255)
    private String emailAddress;

    @Column(length = 1000)
    private String address;
}
