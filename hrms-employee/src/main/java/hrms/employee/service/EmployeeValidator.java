package hrms.employee.service;

import hrms.employee.dto.EmployeeRequest;
import hrms.employee.entity.Employee;

public interface EmployeeValidator {

    void validateForCreate(EmployeeRequest request);

    void validateForUpdate(Long employeeId, EmployeeRequest request);

    void validateManagerAssignment(Employee employee, Long managerEmployeeId);

    void validateEmployeeNumber(Long employeeId, String employeeNumber);
}
