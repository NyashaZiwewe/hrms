package hrms.employee.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeAddressRequest {

    private String province;
    private String district;
    private String streetAddress;
    private String address;
}
