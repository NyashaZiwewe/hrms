package hrms.employee.service;

import hrms.employee.dto.EmployeeRequest;
import hrms.employee.dto.EmployeeAddressRequest;
import hrms.employee.dto.EmployeeContractRequest;
import hrms.employee.dto.EmployeeDependentRequest;
import hrms.employee.dto.EmployeeDisabilityRequest;
import hrms.employee.dto.EmployeeQualificationRequest;
import hrms.employee.dto.EmployeeRelatedContactRequest;
import hrms.employee.entity.Employee;
import hrms.employee.entity.EmployeeAddress;
import hrms.employee.entity.EmployeeContract;
import hrms.employee.entity.EmployeeDependent;
import hrms.employee.entity.EmployeeDisability;
import hrms.employee.entity.EmployeeQualification;
import hrms.employee.entity.EmployeeRelatedContact;
import hrms.employee.model.EmploymentStatus;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeService {

    Employee create(EmployeeRequest request);

    List<Employee> findAll();

    List<Employee> search(String query, String department, String jobTitle, EmploymentStatus status, LocalDate hiredFrom, LocalDate hiredTo);

    Employee findById(Long id);

    Employee update(Long id, EmployeeRequest request);

    EmployeeAddress addAddress(Long employeeId, EmployeeAddressRequest request);

    EmployeeQualification addQualification(Long employeeId, EmployeeQualificationRequest request);

    EmployeeDependent addDependent(Long employeeId, EmployeeDependentRequest request);

    EmployeeDisability addDisability(Long employeeId, EmployeeDisabilityRequest request);

    EmployeeRelatedContact addRelatedContact(Long employeeId, EmployeeRelatedContactRequest request);

    EmployeeContract addContract(Long employeeId, EmployeeContractRequest request);

    void delete(Long id);
}
