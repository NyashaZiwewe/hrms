package hrms.employee.dto;

import hrms.employee.model.RelatedContactType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeRelatedContactRequest {

    private RelatedContactType contactType;
    private String fullName;
    private String relationshipDescription;
    private String phoneNumber;
    private String emailAddress;
    private String address;
}
