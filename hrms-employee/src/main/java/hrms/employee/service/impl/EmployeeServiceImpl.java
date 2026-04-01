package hrms.employee.service.impl;

import hrms.audit.service.AuditTrailService;
import hrms.common.exception.ResourceNotFoundException;
import hrms.common.util.StringUtils;
import hrms.employee.dto.EmployeeDependentRequest;
import hrms.employee.dto.EmployeeDisabilityRequest;
import hrms.employee.dto.EmployeeContractRequest;
import hrms.employee.dto.EmployeeQualificationRequest;
import hrms.employee.dto.EmployeeRelatedContactRequest;
import hrms.employee.dto.EmployeeAddressRequest;
import hrms.employee.dto.EmployeeRequest;
import hrms.employee.entity.EmployeeAddress;
import hrms.employee.entity.EmployeeDependent;
import hrms.employee.entity.EmployeeDisability;
import hrms.employee.entity.Employee;
import hrms.employee.entity.EmployeeContract;
import hrms.employee.entity.EmployeeQualification;
import hrms.employee.entity.EmployeeRelatedContact;
import hrms.employee.model.EmploymentStatus;
import hrms.employee.repository.EmployeeRepository;
import hrms.employee.service.EmployeeNumberGenerator;
import hrms.employee.service.EmployeeReferenceService;
import hrms.employee.service.EmployeeRequestMapper;
import hrms.employee.service.EmployeeRequestSanitizer;
import hrms.employee.service.EmployeeService;
import hrms.employee.service.EmployeeValidator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AuditTrailService auditTrailService;
    private final EmployeeRequestSanitizer employeeRequestSanitizer;
    private final EmployeeValidator employeeValidator;
    private final EmployeeRequestMapper employeeRequestMapper;
    private final EmployeeNumberGenerator employeeNumberGenerator;
    private final EmployeeReferenceService employeeReferenceService;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               AuditTrailService auditTrailService,
                               EmployeeRequestSanitizer employeeRequestSanitizer,
                               EmployeeValidator employeeValidator,
                               EmployeeRequestMapper employeeRequestMapper,
                               EmployeeNumberGenerator employeeNumberGenerator,
                               EmployeeReferenceService employeeReferenceService) {
        this.employeeRepository = employeeRepository;
        this.auditTrailService = auditTrailService;
        this.employeeRequestSanitizer = employeeRequestSanitizer;
        this.employeeValidator = employeeValidator;
        this.employeeRequestMapper = employeeRequestMapper;
        this.employeeNumberGenerator = employeeNumberGenerator;
        this.employeeReferenceService = employeeReferenceService;
    }

    public Employee create(EmployeeRequest request) {
        employeeRequestSanitizer.sanitize(request);
        employeeValidator.validateForCreate(request);
        Employee employee = new Employee();
        employee.setEmployeeNumber(StringUtils.hasText(request.getEmployeeNumber())
                ? request.getEmployeeNumber()
                : employeeNumberGenerator.nextEmployeeNumber());
        employeeRequestMapper.apply(employee, request);
        Employee saved = saveEmployee(employee);
        auditTrailService.log("EMPLOYEE", "Employee", String.valueOf(saved.getId()), "CREATE",
                "Created employee profile for " + saved.getEmployeeNumber());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Employee> search(String query,
                                 String department,
                                 String jobTitle,
                                 EmploymentStatus status,
                                 LocalDate hiredFrom,
                                 LocalDate hiredTo) {
        List<Employee> employees = new ArrayList<Employee>(employeeRepository.findAll());
        if (StringUtils.hasText(query)) {
            String normalized = query.toLowerCase(Locale.ENGLISH);
            employees.removeIf(employee -> !matchesQuery(employee, normalized));
        }
        if (StringUtils.hasText(department)) {
            String normalized = department.toLowerCase(Locale.ENGLISH);
            employees.removeIf(employee -> employee.getDepartment() == null
                    || employee.getDepartment().getName() == null
                    || !employee.getDepartment().getName().toLowerCase(Locale.ENGLISH).contains(normalized));
        }
        if (StringUtils.hasText(jobTitle)) {
            String normalized = jobTitle.toLowerCase(Locale.ENGLISH);
            employees.removeIf(employee -> employee.getJobTitle() == null
                    || employee.getJobTitle().getName() == null
                    || !employee.getJobTitle().getName().toLowerCase(Locale.ENGLISH).contains(normalized));
        }
        if (status != null) {
            employees.removeIf(employee -> employee.getStatus() != status);
        }
        if (hiredFrom != null) {
            employees.removeIf(employee -> employee.getHireDate() == null || toLocalDate(employee.getHireDate()).isBefore(hiredFrom));
        }
        if (hiredTo != null) {
            employees.removeIf(employee -> employee.getHireDate() == null || toLocalDate(employee.getHireDate()).isAfter(hiredTo));
        }
        return employees;
    }

    @Transactional(readOnly = true)
    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
    }

    public Employee update(Long id, EmployeeRequest request) {
        employeeRequestSanitizer.sanitize(request);
        Employee employee = findById(id);
        employeeValidator.validateForUpdate(id, request);
        employeeRequestMapper.apply(employee, request);
        Employee saved = saveEmployee(employee);
        auditTrailService.log("EMPLOYEE", "Employee", String.valueOf(saved.getId()), "UPDATE",
                "Updated employee profile for " + saved.getEmployeeNumber());
        return saved;
    }

    public EmployeeAddress addAddress(Long employeeId, EmployeeAddressRequest request) {
        Employee employee = findById(employeeId);
        if (!StringUtils.hasText(request.getProvince())
                && !StringUtils.hasText(request.getDistrict())
                && !StringUtils.hasText(request.getStreetAddress())
                && !StringUtils.hasText(request.getAddress())) {
            throw new IllegalArgumentException("At least one address field is required");
        }
        EmployeeAddress address = new EmployeeAddress();
        address.setEmployee(employee);
        address.setProvince(trimToNull(request.getProvince()));
        address.setDistrict(trimToNull(request.getDistrict()));
        address.setStreetAddress(trimToNull(request.getStreetAddress()));
        address.setFullAddress(buildAddress(request));
        employee.getAddresses().add(address);
        employeeRepository.save(employee);
        auditTrailService.log("EMPLOYEE", "EmployeeAddress", String.valueOf(employeeId), "CREATE",
                "Added address for employee " + employee.getEmployeeNumber());
        return address;
    }

    public void delete(Long id) {
        Employee employee = findById(id);
        employeeRepository.delete(employee);
        auditTrailService.log("EMPLOYEE", "Employee", String.valueOf(id), "DELETE",
                "Deleted employee profile for " + employee.getEmployeeNumber());
    }

    public EmployeeQualification addQualification(Long employeeId, EmployeeQualificationRequest request) {
        Employee employee = findById(employeeId);
        if (request.getEducationLevelId() == null) {
            throw new IllegalArgumentException("Education level is required");
        }
        if (request.getInstitutionId() == null) {
            throw new IllegalArgumentException("Institution is required");
        }
        if (!StringUtils.hasText(request.getQualificationName())) {
            throw new IllegalArgumentException("Qualification name is required");
        }
        EmployeeQualification qualification = new EmployeeQualification();
        qualification.setEmployee(employee);
        qualification.setEducationLevel(employeeReferenceService.findEducationLevel(request.getEducationLevelId()));
        qualification.setInstitution(employeeReferenceService.findInstitution(request.getInstitutionId()));
        qualification.setQualificationName(request.getQualificationName().trim());
        qualification.setCompletionYear(request.getCompletionYear());
        qualification.setPeriodStudied(request.getPeriodStudied());
        employee.getQualifications().add(qualification);
        employeeRepository.save(employee);
        auditTrailService.log("EMPLOYEE", "EmployeeQualification", String.valueOf(employeeId), "CREATE",
                "Added qualification for employee " + employee.getEmployeeNumber());
        return qualification;
    }

    public EmployeeDependent addDependent(Long employeeId, EmployeeDependentRequest request) {
        Employee employee = findById(employeeId);
        if (!StringUtils.hasText(request.getFullName())) {
            throw new IllegalArgumentException("Dependent name is required");
        }
        EmployeeDependent dependent = new EmployeeDependent();
        dependent.setEmployee(employee);
        dependent.setFullName(request.getFullName().trim());
        dependent.setRelationship(request.getRelationship());
        dependent.setDateOfBirth(request.getDateOfBirth());
        dependent.setNotes(request.getNotes());
        employee.getDependents().add(dependent);
        employeeRepository.save(employee);
        auditTrailService.log("EMPLOYEE", "EmployeeDependent", String.valueOf(employeeId), "CREATE",
                "Added dependent for employee " + employee.getEmployeeNumber());
        return dependent;
    }

    public EmployeeDisability addDisability(Long employeeId, EmployeeDisabilityRequest request) {
        Employee employee = findById(employeeId);
        if (!StringUtils.hasText(request.getDisabilityName())) {
            throw new IllegalArgumentException("Disability name is required");
        }
        EmployeeDisability disability = new EmployeeDisability();
        disability.setEmployee(employee);
        disability.setDisabilityName(request.getDisabilityName().trim());
        disability.setNotes(request.getNotes());
        employee.getDisabilities().add(disability);
        employeeRepository.save(employee);
        auditTrailService.log("EMPLOYEE", "EmployeeDisability", String.valueOf(employeeId), "CREATE",
                "Added disability record for employee " + employee.getEmployeeNumber());
        return disability;
    }

    public EmployeeRelatedContact addRelatedContact(Long employeeId, EmployeeRelatedContactRequest request) {
        Employee employee = findById(employeeId);
        if (request.getContactType() == null) {
            throw new IllegalArgumentException("Contact type is required");
        }
        if (!StringUtils.hasText(request.getFullName())) {
            throw new IllegalArgumentException("Contact name is required");
        }
        EmployeeRelatedContact contact = new EmployeeRelatedContact();
        contact.setEmployee(employee);
        contact.setContactType(request.getContactType());
        contact.setFullName(request.getFullName().trim());
        contact.setRelationshipDescription(request.getRelationshipDescription());
        contact.setPhoneNumber(request.getPhoneNumber());
        contact.setEmailAddress(request.getEmailAddress());
        contact.setAddress(request.getAddress());
        employee.getRelatedContacts().add(contact);
        employeeRepository.save(employee);
        auditTrailService.log("EMPLOYEE", "EmployeeRelatedContact", String.valueOf(employeeId), "CREATE",
                "Added related contact for employee " + employee.getEmployeeNumber());
        return contact;
    }

    public EmployeeContract addContract(Long employeeId, EmployeeContractRequest request) {
        Employee employee = findById(employeeId);
        if (!StringUtils.hasText(request.getContractName())) {
            throw new IllegalArgumentException("Contract name is required");
        }
        if (request.getEndDate() != null && request.getStartDate() != null
                && toLocalDate(request.getEndDate()).isBefore(toLocalDate(request.getStartDate()))) {
            throw new IllegalArgumentException("Contract end date cannot be before start date");
        }
        if (request.isActive()) {
            for (EmployeeContract contract : employee.getContracts()) {
                contract.setActive(false);
            }
        }
        EmployeeContract contract = new EmployeeContract();
        contract.setEmployee(employee);
        contract.setContractName(request.getContractName().trim());
        contract.setStartDate(request.getStartDate());
        contract.setEndDate(request.getEndDate());
        contract.setDocumentPath(request.getDocumentPath());
        contract.setFileName(request.getFileName());
        contract.setActive(request.isActive() || employee.getContracts().isEmpty());
        contract.setNotes(request.getNotes());
        employee.getContracts().add(contract);
        employeeRepository.save(employee);
        auditTrailService.log("EMPLOYEE", "EmployeeContract", String.valueOf(employeeId), "CREATE",
                "Added contract for employee " + employee.getEmployeeNumber());
        return contract;
    }

    private Employee saveEmployee(Employee employee) {
        try {
            return employeeRepository.save(employee);
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalArgumentException("Employee record could not be saved. Check duplicate values such as employee number, email, or national ID.");
        }
    }

    private LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private boolean matchesQuery(Employee employee, String normalized) {
        return contains(employee.getEmployeeNumber(), normalized)
                || contains(employee.getFirstName(), normalized)
                || contains(employee.getMiddleName(), normalized)
                || contains(employee.getLastName(), normalized)
                || contains(employee.getEmail(), normalized)
                || contains(employee.getNationalId(), normalized);
    }

    private boolean contains(String value, String normalized) {
        return value != null && value.toLowerCase(Locale.ENGLISH).contains(normalized);
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String buildAddress(EmployeeAddressRequest request) {
        List<String> parts = new ArrayList<String>();
        if (StringUtils.hasText(request.getStreetAddress())) {
            parts.add(request.getStreetAddress().trim());
        }
        if (StringUtils.hasText(request.getDistrict())) {
            parts.add(request.getDistrict().trim());
        }
        if (StringUtils.hasText(request.getProvince())) {
            parts.add(request.getProvince().trim());
        }
        if (parts.isEmpty() && StringUtils.hasText(request.getAddress())) {
            return request.getAddress().trim();
        }
        return String.join(", ", parts);
    }
}
