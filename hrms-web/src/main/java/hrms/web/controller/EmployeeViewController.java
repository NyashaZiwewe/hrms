package hrms.web.controller;

import hrms.employee.dto.EmployeeRequest;
import hrms.employee.dto.EmployeeContractRequest;
import hrms.employee.dto.EmployeeQualificationRequest;
import hrms.employee.dto.EmployeeDependentRequest;
import hrms.employee.dto.EmployeeDisabilityRequest;
import hrms.employee.dto.EmployeeRelatedContactRequest;
import hrms.employee.dto.DisciplinaryRecordRequest;
import hrms.employee.dto.EmploymentConfirmationApprovalRequest;
import hrms.employee.dto.EmploymentConfirmationRequestInput;
import hrms.employee.entity.Employee;
import hrms.employee.entity.EmployeeContract;
import hrms.employee.entity.EmploymentConfirmationRequest;
import hrms.employee.service.EmployeeReferenceService;
import hrms.employee.service.EmployeeOperationsService;
import hrms.employee.service.EmployeeService;
import hrms.web.constants.Pages;
import hrms.web.service.EmploymentConfirmationDocumentService;
import hrms.web.service.EmploymentConfirmationNotificationService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/employees")
public class EmployeeViewController {

    private final EmployeeService employeeService;
    private final EmployeeReferenceService employeeReferenceService;
    private final EmployeeOperationsService employeeOperationsService;
    private final EmploymentConfirmationDocumentService employmentConfirmationDocumentService;
    private final EmploymentConfirmationNotificationService employmentConfirmationNotificationService;

    public EmployeeViewController(EmployeeService employeeService,
                                  EmployeeReferenceService employeeReferenceService,
                                  EmployeeOperationsService employeeOperationsService,
                                  EmploymentConfirmationDocumentService employmentConfirmationDocumentService,
                                  EmploymentConfirmationNotificationService employmentConfirmationNotificationService) {
        this.employeeService = employeeService;
        this.employeeReferenceService = employeeReferenceService;
        this.employeeOperationsService = employeeOperationsService;
        this.employmentConfirmationDocumentService = employmentConfirmationDocumentService;
        this.employmentConfirmationNotificationService = employmentConfirmationNotificationService;
    }

    @GetMapping
    public ModelAndView employees() {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_EMPLOYEES);
        modelAndView.addObject("pageDomain", "Employee Management");
        modelAndView.addObject("pageName", "Employees");
        modelAndView.addObject("pageTitle", "View Employees");
        modelAndView.addObject("employees", employeeService.findAll());
        return modelAndView;
    }

    @GetMapping("/add-employee")
    public ModelAndView addEmployee() {
        ModelAndView modelAndView = new ModelAndView(Pages.ADD_EMPLOYEE);
        modelAndView.addObject("employeeRequest", new EmployeeRequest());
        populateForm(modelAndView, "Add Employee");
        return modelAndView;
    }

    @PostMapping("/save-employee")
    public ModelAndView saveEmployee(@Valid @ModelAttribute("employeeRequest") EmployeeRequest employeeRequest,
                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.ADD_EMPLOYEE);
            populateForm(modelAndView, "Add Employee");
            return modelAndView;
        }
        Employee employee = employeeService.create(employeeRequest);
        return new ModelAndView("redirect:/employees/view-employee/" + employee.getId());
    }

    @GetMapping("/view-employee/{id}")
    public ModelAndView viewEmployee(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_EMPLOYEE);
        Employee employee = employeeService.findById(id);
        List<Employee> employees = employeeService.findAll();
        modelAndView.addObject("pageDomain", "Employee Management");
        modelAndView.addObject("pageName", "Employees");
        modelAndView.addObject("pageTitle", "View Employee");
        modelAndView.addObject("employee", employee);
        modelAndView.addObject("manager", findManager(employee, employees));
        modelAndView.addObject("directReports", findDirectReports(employee, employees));
        modelAndView.addObject("coworkers", findCoworkers(employee, employees));
        modelAndView.addObject("activeContract", findActiveContract(employee));
        modelAndView.addObject("hrManagers", findHrManagers(employees));
        modelAndView.addObject("confirmationRequests", employeeOperationsService.employmentConfirmations(id));
        modelAndView.addObject("confirmationSignerNames", buildEmployeeNameMap(employees));
        EmploymentConfirmationRequestInput confirmationRequestInput = new EmploymentConfirmationRequestInput();
        confirmationRequestInput.setEmployeeId(id);
        confirmationRequestInput.setDeliveryEmail(employee.getEmail());
        modelAndView.addObject("employmentConfirmationRequestInput", confirmationRequestInput);
        modelAndView.addObject("employmentConfirmationApprovalRequest", new EmploymentConfirmationApprovalRequest());
        DisciplinaryRecordRequest disciplinaryRecordRequest = new DisciplinaryRecordRequest();
        disciplinaryRecordRequest.setEmployeeId(id);
        modelAndView.addObject("disciplinaryRecordRequest", disciplinaryRecordRequest);
        modelAndView.addObject("disciplinaryRecords", employeeOperationsService.disciplinaryRecords(id));
        modelAndView.addObject("disciplinaryTypes", hrms.employee.model.DisciplinaryRecordType.values());
        return modelAndView;
    }

    @GetMapping("/edit-employee/{id}")
    public ModelAndView editEmployee(@PathVariable Long id) {
        Employee employee = employeeService.findById(id);
        EmployeeRequest request = new EmployeeRequest();
        request.setEmployeeNumber(employee.getEmployeeNumber());
        request.setFirstName(employee.getFirstName());
        request.setMiddleName(employee.getMiddleName());
        request.setLastName(employee.getLastName());
        request.setEmail(employee.getEmail());
        request.setPhoneNumber(employee.getPhoneNumber());
        request.setAddress(employee.getAddress());
        request.setNationalId(employee.getNationalId());
        request.setEmergencyContactName(employee.getEmergencyContactName());
        request.setEmergencyContactPhone(employee.getEmergencyContactPhone());
        request.setJobTitleId(employee.getJobTitle() == null ? null : employee.getJobTitle().getId());
        request.setDepartmentId(employee.getDepartment() == null ? null : employee.getDepartment().getId());
        request.setEmploymentTypeId(employee.getEmploymentType() == null ? null : employee.getEmploymentType().getId());
        request.setHireDate(employee.getHireDate());
        request.setTerminationDate(employee.getTerminationDate());
        request.setManagerEmployeeId(employee.getManagerEmployeeId());
        request.setPreferredCurrency(employee.getPreferredCurrency());
        request.setMonthlySalary(employee.getMonthlySalary());
        request.setHourlyRate(employee.getHourlyRate());
        request.setEmploymentHistory(employee.getEmploymentHistory());
        request.setBenefitsSummary(employee.getBenefitsSummary());
        request.setPerformanceSummary(employee.getPerformanceSummary());
        request.setStatus(employee.getStatus());
        for (hrms.employee.entity.EmployeeQualification qualification : employee.getQualifications()) {
            EmployeeQualificationRequest qualificationRequest = new EmployeeQualificationRequest();
            qualificationRequest.setEducationLevelId(qualification.getEducationLevel().getId());
            qualificationRequest.setInstitutionId(qualification.getInstitution().getId());
            qualificationRequest.setQualificationName(qualification.getQualificationName());
            qualificationRequest.setCompletionYear(qualification.getCompletionYear());
            qualificationRequest.setPeriodStudied(qualification.getPeriodStudied());
            request.getQualifications().add(qualificationRequest);
        }
        for (hrms.employee.entity.EmployeeDependent dependent : employee.getDependents()) {
            EmployeeDependentRequest dependentRequest = new EmployeeDependentRequest();
            dependentRequest.setFullName(dependent.getFullName());
            dependentRequest.setRelationship(dependent.getRelationship());
            dependentRequest.setDateOfBirth(dependent.getDateOfBirth());
            dependentRequest.setNotes(dependent.getNotes());
            request.getDependents().add(dependentRequest);
        }
        for (hrms.employee.entity.EmployeeDisability disability : employee.getDisabilities()) {
            EmployeeDisabilityRequest disabilityRequest = new EmployeeDisabilityRequest();
            disabilityRequest.setDisabilityName(disability.getDisabilityName());
            disabilityRequest.setNotes(disability.getNotes());
            request.getDisabilities().add(disabilityRequest);
        }
        for (hrms.employee.entity.EmployeeRelatedContact contact : employee.getRelatedContacts()) {
            EmployeeRelatedContactRequest contactRequest = new EmployeeRelatedContactRequest();
            contactRequest.setContactType(contact.getContactType());
            contactRequest.setFullName(contact.getFullName());
            contactRequest.setRelationshipDescription(contact.getRelationshipDescription());
            contactRequest.setPhoneNumber(contact.getPhoneNumber());
            contactRequest.setEmailAddress(contact.getEmailAddress());
            contactRequest.setAddress(contact.getAddress());
            request.getRelatedContacts().add(contactRequest);
        }

        ModelAndView modelAndView = new ModelAndView(Pages.EDIT_EMPLOYEE);
        modelAndView.addObject("employeeId", id);
        modelAndView.addObject("employeeRequest", request);
        populateForm(modelAndView, "Edit Employee");
        return modelAndView;
    }

    @PostMapping("/update-employee/{id}")
    public ModelAndView updateEmployee(@PathVariable Long id,
                                       @Valid @ModelAttribute("employeeRequest") EmployeeRequest employeeRequest,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.EDIT_EMPLOYEE);
            modelAndView.addObject("employeeId", id);
            populateForm(modelAndView, "Edit Employee");
            return modelAndView;
        }
        employeeService.update(id, employeeRequest);
        return new ModelAndView("redirect:/employees/view-employee/" + id);
    }

    @GetMapping("/contracts/{contractId}/download")
    public ResponseEntity<Resource> downloadContract(@PathVariable Long contractId) {
        EmployeeContract contract = findContract(contractId);
        if (contract.getDocumentPath() == null) {
            throw new IllegalArgumentException("No contract attached for contract " + contractId);
        }
        Resource resource = new FileSystemResource(contract.getDocumentPath());
        if (!resource.exists()) {
            throw new IllegalArgumentException("Contract file not found for contract " + contractId);
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + contract.getFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/{id}/dependents")
    public ModelAndView manageDependents(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView(Pages.MANAGE_EMPLOYEE_DEPENDENTS);
        populateEmployeeRecordPage(modelAndView, id, "Manage Dependants");
        modelAndView.addObject("dependentRequest", new EmployeeDependentRequest());
        return modelAndView;
    }

    @PostMapping("/{id}/dependents")
    public ModelAndView saveDependent(@PathVariable Long id,
                                      @Valid @ModelAttribute("dependentRequest") EmployeeDependentRequest dependentRequest,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.MANAGE_EMPLOYEE_DEPENDENTS);
            populateEmployeeRecordPage(modelAndView, id, "Manage Dependants");
            return modelAndView;
        }
        employeeService.addDependent(id, dependentRequest);
        return new ModelAndView("redirect:/employees/" + id + "/dependents");
    }

    @GetMapping("/{id}/disabilities")
    public ModelAndView manageDisabilities(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView(Pages.MANAGE_EMPLOYEE_DISABILITIES);
        populateEmployeeRecordPage(modelAndView, id, "Manage Disabilities");
        modelAndView.addObject("disabilityRequest", new EmployeeDisabilityRequest());
        return modelAndView;
    }

    @PostMapping("/{id}/disabilities")
    public ModelAndView saveDisability(@PathVariable Long id,
                                       @Valid @ModelAttribute("disabilityRequest") EmployeeDisabilityRequest disabilityRequest,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.MANAGE_EMPLOYEE_DISABILITIES);
            populateEmployeeRecordPage(modelAndView, id, "Manage Disabilities");
            return modelAndView;
        }
        employeeService.addDisability(id, disabilityRequest);
        return new ModelAndView("redirect:/employees/" + id + "/disabilities");
    }

    @GetMapping("/{id}/contacts")
    public ModelAndView manageContacts(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView(Pages.MANAGE_EMPLOYEE_CONTACTS);
        populateEmployeeRecordPage(modelAndView, id, "Manage Related Contacts");
        modelAndView.addObject("contactRequest", new EmployeeRelatedContactRequest());
        return modelAndView;
    }

    @PostMapping("/{id}/contacts")
    public ModelAndView saveContact(@PathVariable Long id,
                                    @Valid @ModelAttribute("contactRequest") EmployeeRelatedContactRequest contactRequest,
                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.MANAGE_EMPLOYEE_CONTACTS);
            populateEmployeeRecordPage(modelAndView, id, "Manage Related Contacts");
            return modelAndView;
        }
        employeeService.addRelatedContact(id, contactRequest);
        return new ModelAndView("redirect:/employees/" + id + "/contacts");
    }

    @GetMapping("/{id}/contracts")
    public ModelAndView manageContracts(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView(Pages.MANAGE_EMPLOYEE_CONTRACTS);
        populateEmployeeRecordPage(modelAndView, id, "Manage Contracts");
        modelAndView.addObject("contractRequest", new EmployeeContractRequest());
        return modelAndView;
    }

    @PostMapping("/{id}/contracts")
    public ModelAndView saveContract(@PathVariable Long id,
                                     @Valid @ModelAttribute("contractRequest") EmployeeContractRequest contractRequest,
                                     BindingResult bindingResult,
                                     @RequestParam(name = "contractFile", required = false) MultipartFile contractFile) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(Pages.MANAGE_EMPLOYEE_CONTRACTS);
            populateEmployeeRecordPage(modelAndView, id, "Manage Contracts");
            return modelAndView;
        }
        storeContractFile(contractRequest, contractFile);
        employeeService.addContract(id, contractRequest);
        return new ModelAndView("redirect:/employees/" + id + "/contracts");
    }

    @GetMapping("/employment-confirmations/{requestId}/download")
    public ResponseEntity<Resource> downloadEmploymentConfirmation(@PathVariable Long requestId) {
        EmploymentConfirmationRequest request = findEmploymentConfirmationRequest(requestId);
        if (request.getSignedDocumentPath() == null) {
            throw new IllegalArgumentException("No signed confirmation document found for request " + requestId);
        }
        Resource resource = new FileSystemResource(request.getSignedDocumentPath());
        if (!resource.exists()) {
            throw new IllegalArgumentException("Confirmation document file not found for request " + requestId);
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + request.getSignedDocumentFileName() + "\"")
                .body(resource);
    }

    @PostMapping("/delete-employee/{id}")
    public ModelAndView deleteEmployee(@PathVariable Long id) {
        employeeService.delete(id);
        return new ModelAndView("redirect:/employees");
    }

    @PostMapping("/disciplinary-records")
    public ModelAndView saveDisciplinaryRecord(@Valid @ModelAttribute DisciplinaryRecordRequest disciplinaryRecordRequest,
                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Long employeeId = disciplinaryRecordRequest.getEmployeeId();
            return buildEmployeeView(employeeId,
                    new EmploymentConfirmationRequestInput(),
                    new EmploymentConfirmationApprovalRequest(),
                    disciplinaryRecordRequest);
        }
        employeeOperationsService.createDisciplinaryRecord(disciplinaryRecordRequest);
        return new ModelAndView("redirect:/employees/view-employee/" + disciplinaryRecordRequest.getEmployeeId());
    }

    @PostMapping("/employment-confirmations")
    public ModelAndView requestEmploymentConfirmation(@Valid @ModelAttribute EmploymentConfirmationRequestInput employmentConfirmationRequestInput,
                                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return buildEmployeeView(employmentConfirmationRequestInput.getEmployeeId(),
                    employmentConfirmationRequestInput,
                    new EmploymentConfirmationApprovalRequest(),
                    new DisciplinaryRecordRequest());
        }
        EmploymentConfirmationRequest saved = employeeOperationsService.requestEmploymentConfirmation(employmentConfirmationRequestInput);
        Employee employee = employeeService.findById(employmentConfirmationRequestInput.getEmployeeId());
        employmentConfirmationNotificationService.notifyHrManagersOfRequest(employee, saved, findHrManagers(employeeService.findAll()));
        return new ModelAndView("redirect:/employees/view-employee/" + employmentConfirmationRequestInput.getEmployeeId());
    }

    @PostMapping("/employment-confirmations/sign")
    public ModelAndView signEmploymentConfirmation(@Valid @ModelAttribute EmploymentConfirmationApprovalRequest employmentConfirmationApprovalRequest,
                                                   BindingResult bindingResult) {
        EmploymentConfirmationRequest existingRequest = findEmploymentConfirmationRequest(employmentConfirmationApprovalRequest.getRequestId());
        Long employeeId = existingRequest.getEmployee().getId();
        if (bindingResult.hasErrors()) {
            EmploymentConfirmationRequestInput confirmationRequestInput = new EmploymentConfirmationRequestInput();
            confirmationRequestInput.setEmployeeId(employeeId);
            confirmationRequestInput.setDeliveryEmail(existingRequest.getDeliveryEmail());
            confirmationRequestInput.setPurpose(existingRequest.getPurpose());
            return buildEmployeeView(employeeId,
                    confirmationRequestInput,
                    employmentConfirmationApprovalRequest,
                    new DisciplinaryRecordRequest());
        }
        Employee hrManager = employeeService.findById(employmentConfirmationApprovalRequest.getSignerEmployeeId());
        EmploymentConfirmationDocumentService.GeneratedDocument generatedDocument =
                employmentConfirmationDocumentService.generate(existingRequest.getEmployee(), existingRequest, hrManager);
        employmentConfirmationApprovalRequest.setSignedDocumentPath(generatedDocument.getPath());
        employmentConfirmationApprovalRequest.setSignedDocumentFileName(generatedDocument.getFileName());
        EmploymentConfirmationRequest saved = employeeOperationsService.signEmploymentConfirmation(employmentConfirmationApprovalRequest);
        employmentConfirmationNotificationService.notifyEmployeeOfSignedLetter(saved.getEmployee(), saved);
        return new ModelAndView("redirect:/employees/view-employee/" + employeeId);
    }

    private void populateForm(ModelAndView modelAndView, String pageTitle) {
        EmployeeRequest employeeRequest = (EmployeeRequest) modelAndView.getModel().get("employeeRequest");
        if (employeeRequest != null && (employeeRequest.getQualifications() == null || employeeRequest.getQualifications().isEmpty())) {
            employeeRequest.setQualifications(new ArrayList<EmployeeQualificationRequest>());
            employeeRequest.getQualifications().add(new EmployeeQualificationRequest());
        }
        modelAndView.addObject("pageDomain", "Employee Management");
        modelAndView.addObject("pageName", "Employees");
        modelAndView.addObject("pageTitle", pageTitle);
        modelAndView.addObject("departments", employeeReferenceService.departments());
        modelAndView.addObject("employmentTypes", employeeReferenceService.employmentTypes());
        modelAndView.addObject("jobTitles", employeeReferenceService.jobTitles());
        modelAndView.addObject("educationLevels", employeeReferenceService.educationLevels());
        modelAndView.addObject("institutions", employeeReferenceService.institutions());
        modelAndView.addObject("relatedContactTypes", hrms.employee.model.RelatedContactType.values());
        modelAndView.addObject("employees", employeeService.findAll());
    }

    private ModelAndView buildEmployeeView(Long employeeId,
                                           EmploymentConfirmationRequestInput confirmationRequestInput,
                                           EmploymentConfirmationApprovalRequest confirmationApprovalRequest,
                                           DisciplinaryRecordRequest disciplinaryRecordRequest) {
        ModelAndView modelAndView = new ModelAndView(Pages.VIEW_EMPLOYEE);
        Employee employee = employeeService.findById(employeeId);
        List<Employee> employees = employeeService.findAll();
        modelAndView.addObject("pageDomain", "Employee Management");
        modelAndView.addObject("pageName", "Employees");
        modelAndView.addObject("pageTitle", "View Employee");
        modelAndView.addObject("employee", employee);
        modelAndView.addObject("manager", findManager(employee, employees));
        modelAndView.addObject("directReports", findDirectReports(employee, employees));
        modelAndView.addObject("coworkers", findCoworkers(employee, employees));
        modelAndView.addObject("activeContract", findActiveContract(employee));
        modelAndView.addObject("hrManagers", findHrManagers(employees));
        modelAndView.addObject("confirmationRequests", employeeOperationsService.employmentConfirmations(employeeId));
        modelAndView.addObject("confirmationSignerNames", buildEmployeeNameMap(employees));
        if (confirmationRequestInput.getEmployeeId() == null) {
            confirmationRequestInput.setEmployeeId(employeeId);
        }
        if (confirmationRequestInput.getDeliveryEmail() == null) {
            confirmationRequestInput.setDeliveryEmail(employee.getEmail());
        }
        modelAndView.addObject("employmentConfirmationRequestInput", confirmationRequestInput);
        modelAndView.addObject("employmentConfirmationApprovalRequest", confirmationApprovalRequest);
        disciplinaryRecordRequest.setEmployeeId(employeeId);
        modelAndView.addObject("disciplinaryRecordRequest", disciplinaryRecordRequest);
        modelAndView.addObject("disciplinaryRecords", employeeOperationsService.disciplinaryRecords(employeeId));
        modelAndView.addObject("disciplinaryTypes", hrms.employee.model.DisciplinaryRecordType.values());
        return modelAndView;
    }

    private void populateEmployeeRecordPage(ModelAndView modelAndView, Long employeeId, String pageTitle) {
        Employee employee = employeeService.findById(employeeId);
        modelAndView.addObject("pageDomain", "Employee Management");
        modelAndView.addObject("pageName", "Employees");
        modelAndView.addObject("pageTitle", pageTitle);
        modelAndView.addObject("employee", employee);
        modelAndView.addObject("relatedContactTypes", hrms.employee.model.RelatedContactType.values());
    }

    private void storeContractFile(EmployeeContractRequest contractRequest, MultipartFile contractFile) {
        if (contractFile == null || contractFile.isEmpty()) {
            return;
        }
        try {
            Path directory = Paths.get("uploads", "employee-contracts");
            Files.createDirectories(directory);
            String storedFileName = UUID.randomUUID().toString() + "-" + contractFile.getOriginalFilename();
            Path target = directory.resolve(storedFileName);
            Files.copy(contractFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            contractRequest.setDocumentPath(target.toAbsolutePath().toString());
            contractRequest.setFileName(contractFile.getOriginalFilename());
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to store contract file", exception);
        }
    }

    private Employee findManager(Employee employee, List<Employee> employees) {
        if (employee.getManagerEmployeeId() == null) {
            return null;
        }
        for (Employee candidate : employees) {
            if (employee.getManagerEmployeeId().equals(candidate.getId())) {
                return candidate;
            }
        }
        return null;
    }

    private List<Employee> findDirectReports(Employee employee, List<Employee> employees) {
        List<Employee> directReports = new ArrayList<Employee>();
        for (Employee candidate : employees) {
            if (employee.getId().equals(candidate.getManagerEmployeeId())) {
                directReports.add(candidate);
            }
        }
        return directReports;
    }

    private List<Employee> findCoworkers(Employee employee, List<Employee> employees) {
        List<Employee> coworkers = new ArrayList<Employee>();
        if (employee.getDepartment() == null) {
            return coworkers;
        }
        for (Employee candidate : employees) {
            if (candidate.getId().equals(employee.getId())) {
                continue;
            }
            if (candidate.getDepartment() != null
                    && employee.getDepartment().getId().equals(candidate.getDepartment().getId())) {
                coworkers.add(candidate);
            }
        }
        return coworkers;
    }

    private List<Employee> findHrManagers(List<Employee> employees) {
        List<Employee> hrManagers = new ArrayList<Employee>();
        for (Employee employee : employees) {
            if (employee.getDepartment() != null
                    && employee.getDepartment().getName() != null
                    && "human resources".equalsIgnoreCase(employee.getDepartment().getName())
                    && employee.getJobTitle() != null
                    && employee.getJobTitle().getName() != null
                    && employee.getJobTitle().getName().toLowerCase().contains("manager")) {
                hrManagers.add(employee);
            }
        }
        return hrManagers;
    }

    private EmployeeContract findActiveContract(Employee employee) {
        for (EmployeeContract contract : employee.getContracts()) {
            if (contract.isActive()) {
                return contract;
            }
        }
        return employee.getContracts().isEmpty() ? null : employee.getContracts().get(0);
    }

    private EmployeeContract findContract(Long contractId) {
        for (Employee employee : employeeService.findAll()) {
            for (EmployeeContract contract : employee.getContracts()) {
                if (contract.getId().equals(contractId)) {
                    return contract;
                }
            }
        }
        throw new IllegalArgumentException("Employee contract not found: " + contractId);
    }

    private Map<Long, String> buildEmployeeNameMap(List<Employee> employees) {
        Map<Long, String> employeeNameMap = new LinkedHashMap<Long, String>();
        for (Employee employee : employees) {
            employeeNameMap.put(employee.getId(),
                    employee.getFirstName() + " " + employee.getLastName() + " (" + employee.getEmployeeNumber() + ")");
        }
        return employeeNameMap;
    }

    private EmploymentConfirmationRequest findEmploymentConfirmationRequest(Long requestId) {
        for (Employee employee : employeeService.findAll()) {
            for (EmploymentConfirmationRequest request : employeeOperationsService.employmentConfirmations(employee.getId())) {
                if (request.getId().equals(requestId)) {
                    return request;
                }
            }
        }
        throw new IllegalArgumentException("Employment confirmation request not found: " + requestId);
    }
}
