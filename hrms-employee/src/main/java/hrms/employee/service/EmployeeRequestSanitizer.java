package hrms.employee.service;

import hrms.employee.dto.EmployeeRequest;

public interface EmployeeRequestSanitizer {

    void sanitize(EmployeeRequest request);
}
