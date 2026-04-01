package hrms.employee.entity;

import hrms.employee.model.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "service_level_agreements")
public class ServiceLevelAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String agreementName;

    @Column(nullable = false)
    private String counterparty;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date signedDate;

    @Column(nullable = false)
    private boolean signed;

    @Column(nullable = false)
    private String documentPath;

    @Column(length = 1500)
    private String notes;
}
