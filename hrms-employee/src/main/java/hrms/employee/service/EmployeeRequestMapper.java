package hrms.employee.service;

import hrms.employee.dto.EmployeeRequest;
import hrms.employee.entity.Employee;

public interface EmployeeRequestMapper {

    void apply(Employee employee, EmployeeRequest request);
}
