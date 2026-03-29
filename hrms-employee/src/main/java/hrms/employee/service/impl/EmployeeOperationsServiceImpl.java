package hrms.employee.service.impl;

import hrms.audit.service.AuditTrailService;
import hrms.common.exception.OperationNotAllowedException;
import hrms.employee.dto.DisciplinaryRecordRequest;
import hrms.employee.dto.EmploymentConfirmationApprovalRequest;
import hrms.employee.dto.EmploymentConfirmationRequestInput;
import hrms.employee.entity.DisciplinaryRecord;
import hrms.employee.entity.EmploymentConfirmationRequest;
import hrms.employee.dto.EmployeeCaseRequest;
import hrms.employee.dto.EmployeeStatusChangeRequest;
import hrms.employee.dto.OffboardingRecordRequest;
import hrms.employee.dto.OnboardingRecordRequest;
import hrms.employee.dto.RecruitmentRequestInput;
import hrms.employee.dto.ServiceLevelAgreementRequest;
import hrms.employee.dto.SuccessionPlanRequest;
import hrms.employee.dto.TrainingRecordRequest;
import hrms.employee.dto.WorkforceAnalyticsResponse;
import hrms.employee.entity.Employee;
import hrms.employee.entity.EmployeeCase;
import hrms.employee.entity.EmployeeStatusChange;
import hrms.employee.entity.OffboardingRecord;
import hrms.employee.entity.OnboardingRecord;
import hrms.employee.entity.RecruitmentRequest;
import hrms.employee.entity.ServiceLevelAgreement;
import hrms.employee.entity.SuccessionPlan;
import hrms.employee.entity.TrainingRecord;
import hrms.employee.model.EmploymentStatus;
import hrms.employee.model.EmploymentConfirmationStatus;
import hrms.employee.model.RecruitmentRequestStatus;
import hrms.employee.repository.EmployeeCaseRepository;
import hrms.employee.repository.DisciplinaryRecordRepository;
import hrms.employee.repository.EmploymentConfirmationRequestRepository;
import hrms.employee.repository.EmployeeStatusChangeRepository;
import hrms.employee.repository.OffboardingRecordRepository;
import hrms.employee.repository.OnboardingRecordRepository;
import hrms.employee.repository.RecruitmentRequestRepository;
import hrms.employee.repository.ServiceLevelAgreementRepository;
import hrms.employee.repository.SuccessionPlanRepository;
import hrms.employee.repository.TrainingRecordRepository;
import hrms.employee.service.EmployeeOperationsService;
import hrms.employee.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EmployeeOperationsServiceImpl implements EmployeeOperationsService {

    private final EmployeeService employeeService;
    private final EmployeeStatusChangeRepository employeeStatusChangeRepository;
    private final DisciplinaryRecordRepository disciplinaryRecordRepository;
    private final EmploymentConfirmationRequestRepository employmentConfirmationRequestRepository;
    private final OnboardingRecordRepository onboardingRecordRepository;
    private final TrainingRecordRepository trainingRecordRepository;
    private final RecruitmentRequestRepository recruitmentRequestRepository;
    private final EmployeeCaseRepository employeeCaseRepository;
    private final SuccessionPlanRepository successionPlanRepository;
    private final OffboardingRecordRepository offboardingRecordRepository;
    private final ServiceLevelAgreementRepository serviceLevelAgreementRepository;
    private final AuditTrailService auditTrailService;

    public EmployeeOperationsServiceImpl(EmployeeService employeeService,
                                     EmployeeStatusChangeRepository employeeStatusChangeRepository,
                                     DisciplinaryRecordRepository disciplinaryRecordRepository,
                                     EmploymentConfirmationRequestRepository employmentConfirmationRequestRepository,
                                     OnboardingRecordRepository onboardingRecordRepository,
                                     TrainingRecordRepository trainingRecordRepository,
                                     RecruitmentRequestRepository recruitmentRequestRepository,
                                     EmployeeCaseRepository employeeCaseRepository,
                                     SuccessionPlanRepository successionPlanRepository,
                                     OffboardingRecordRepository offboardingRecordRepository,
                                     ServiceLevelAgreementRepository serviceLevelAgreementRepository,
                                     AuditTrailService auditTrailService) {
        this.employeeService = employeeService;
        this.employeeStatusChangeRepository = employeeStatusChangeRepository;
        this.disciplinaryRecordRepository = disciplinaryRecordRepository;
        this.employmentConfirmationRequestRepository = employmentConfirmationRequestRepository;
        this.onboardingRecordRepository = onboardingRecordRepository;
        this.trainingRecordRepository = trainingRecordRepository;
        this.recruitmentRequestRepository = recruitmentRequestRepository;
        this.employeeCaseRepository = employeeCaseRepository;
        this.successionPlanRepository = successionPlanRepository;
        this.offboardingRecordRepository = offboardingRecordRepository;
        this.serviceLevelAgreementRepository = serviceLevelAgreementRepository;
        this.auditTrailService = auditTrailService;
    }

    public EmployeeStatusChange recordStatusChange(EmployeeStatusChangeRequest request) {
        Employee employee = employeeService.findById(request.getEmployeeId());
        EmployeeStatusChange change = new EmployeeStatusChange();
        change.setEmployee(employee);
        change.setPreviousStatus(employee.getStatus());
        change.setNewStatus(request.getNewStatus());
        change.setEffectiveDate(request.getEffectiveDate());
        change.setEventType(request.getEventType());
        change.setNotes(request.getNotes());
        employee.setStatus(request.getNewStatus());
        if (request.getNewStatus() == EmploymentStatus.TERMINATED) {
            employee.setTerminationDate(request.getEffectiveDate());
        }
        EmployeeStatusChange saved = employeeStatusChangeRepository.save(change);
        auditTrailService.log("EMPLOYEE", "EmployeeStatusChange", String.valueOf(saved.getId()), "STATUS_CHANGE",
                "Changed employee " + employee.getEmployeeNumber() + " status to " + request.getNewStatus());
        return saved;
    }

    public DisciplinaryRecord createDisciplinaryRecord(DisciplinaryRecordRequest request) {
        Employee employee = employeeService.findById(request.getEmployeeId());
        if (request.getEffectiveFrom() != null && request.getEffectiveTo() != null
                && request.getEffectiveTo().isBefore(request.getEffectiveFrom())) {
            throw new IllegalArgumentException("Effective to date cannot be before effective from date");
        }
        DisciplinaryRecord record = new DisciplinaryRecord();
        record.setEmployee(employee);
        record.setRecordType(request.getRecordType());
        record.setRecordDate(request.getRecordDate());
        record.setEffectiveFrom(request.getEffectiveFrom());
        record.setEffectiveTo(request.getEffectiveTo());
        record.setSubject(request.getSubject());
        record.setDetails(request.getDetails());
        record.setOutcome(request.getOutcome());
        DisciplinaryRecord saved = disciplinaryRecordRepository.save(record);
        auditTrailService.log("EMPLOYEE", "DisciplinaryRecord", String.valueOf(saved.getId()), "CREATE",
                "Recorded " + saved.getRecordType() + " for employee " + employee.getEmployeeNumber());
        return saved;
    }

    public EmploymentConfirmationRequest requestEmploymentConfirmation(EmploymentConfirmationRequestInput request) {
        Employee employee = employeeService.findById(request.getEmployeeId());
        EmploymentConfirmationRequest confirmationRequest = new EmploymentConfirmationRequest();
        confirmationRequest.setEmployee(employee);
        confirmationRequest.setRequestedDate(LocalDate.now());
        confirmationRequest.setPurpose(request.getPurpose());
        confirmationRequest.setDeliveryEmail(request.getDeliveryEmail() == null || request.getDeliveryEmail().trim().isEmpty()
                ? employee.getEmail()
                : request.getDeliveryEmail().trim());
        confirmationRequest.setStatus(EmploymentConfirmationStatus.REQUESTED);
        EmploymentConfirmationRequest saved = employmentConfirmationRequestRepository.save(confirmationRequest);
        auditTrailService.log("EMPLOYEE", "EmploymentConfirmationRequest", String.valueOf(saved.getId()), "REQUEST",
                "Requested confirmation of employment for employee " + employee.getEmployeeNumber());
        return saved;
    }

    public EmploymentConfirmationRequest signEmploymentConfirmation(EmploymentConfirmationApprovalRequest request) {
        EmploymentConfirmationRequest confirmationRequest = employmentConfirmationRequestRepository.findById(request.getRequestId())
                .orElseThrow(() -> new IllegalArgumentException("Employment confirmation request not found: " + request.getRequestId()));
        if (confirmationRequest.getStatus() == EmploymentConfirmationStatus.SIGNED) {
            throw new OperationNotAllowedException("Employment confirmation request has already been signed");
        }
        Employee signer = employeeService.findById(request.getSignerEmployeeId());
        if (!isHrManager(signer)) {
            throw new OperationNotAllowedException("Only an HR manager can sign employment confirmation letters");
        }
        confirmationRequest.setSignedByEmployeeId(signer.getId());
        confirmationRequest.setSignedDate(LocalDate.now());
        confirmationRequest.setStatus(EmploymentConfirmationStatus.SIGNED);
        confirmationRequest.setSignedDocumentPath(request.getSignedDocumentPath());
        confirmationRequest.setSignedDocumentFileName(request.getSignedDocumentFileName());
        EmploymentConfirmationRequest saved = employmentConfirmationRequestRepository.save(confirmationRequest);
        auditTrailService.log("EMPLOYEE", "EmploymentConfirmationRequest", String.valueOf(saved.getId()), "SIGN",
                "Signed confirmation of employment for employee " + saved.getEmployee().getEmployeeNumber());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<EmployeeStatusChange> history(Long employeeId) {
        employeeService.findById(employeeId);
        return employeeStatusChangeRepository.findByEmployeeIdOrderByEffectiveDateDesc(employeeId);
    }

    @Transactional(readOnly = true)
    public List<DisciplinaryRecord> disciplinaryRecords(Long employeeId) {
        employeeService.findById(employeeId);
        return disciplinaryRecordRepository.findByEmployeeIdOrderByRecordDateDesc(employeeId);
    }

    @Transactional(readOnly = true)
    public List<EmploymentConfirmationRequest> employmentConfirmations(Long employeeId) {
        employeeService.findById(employeeId);
        return employmentConfirmationRequestRepository.findByEmployeeIdOrderByRequestedDateDesc(employeeId);
    }

    public OnboardingRecord createOnboarding(OnboardingRecordRequest request) {
        Employee employee = employeeService.findById(request.getEmployeeId());
        OnboardingRecord record = new OnboardingRecord();
        record.setEmployee(employee);
        record.setStage(request.getStage());
        record.setDueDate(request.getDueDate());
        record.setCompleted(request.isCompleted());
        record.setNotes(request.getNotes());
        OnboardingRecord saved = onboardingRecordRepository.save(record);
        auditTrailService.log("EMPLOYEE", "OnboardingRecord", String.valueOf(saved.getId()), "CREATE",
                "Created onboarding stage " + saved.getStage() + " for employee " + employee.getEmployeeNumber());
        return saved;
    }

    public TrainingRecord createTraining(TrainingRecordRequest request) {
        Employee employee = employeeService.findById(request.getEmployeeId());
        TrainingRecord record = new TrainingRecord();
        record.setEmployee(employee);
        record.setCourseName(request.getCourseName());
        record.setProvider(request.getProvider());
        record.setCompletionDate(request.getCompletionDate());
        record.setCertified(request.isCertified());
        record.setDevelopmentPlan(request.getDevelopmentPlan());
        TrainingRecord saved = trainingRecordRepository.save(record);
        auditTrailService.log("EMPLOYEE", "TrainingRecord", String.valueOf(saved.getId()), "CREATE",
                "Recorded training " + saved.getCourseName() + " for employee " + employee.getEmployeeNumber());
        return saved;
    }

    public RecruitmentRequest createRecruitmentRequest(RecruitmentRequestInput request) {
        RecruitmentRequest recruitmentRequest = new RecruitmentRequest();
        recruitmentRequest.setPositionTitle(request.getPositionTitle());
        recruitmentRequest.setDepartment(request.getDepartment());
        recruitmentRequest.setRequestedHeadcount(request.getRequestedHeadcount());
        recruitmentRequest.setRequestDate(request.getRequestDate());
        recruitmentRequest.setStatus(request.getStatus());
        recruitmentRequest.setJustification(request.getJustification());
        RecruitmentRequest saved = recruitmentRequestRepository.save(recruitmentRequest);
        auditTrailService.log("EMPLOYEE", "RecruitmentRequest", String.valueOf(saved.getId()), "CREATE",
                "Created recruitment request for " + saved.getPositionTitle());
        return saved;
    }

    public EmployeeCase createCase(EmployeeCaseRequest request) {
        Employee employee = employeeService.findById(request.getEmployeeId());
        EmployeeCase employeeCase = new EmployeeCase();
        employeeCase.setEmployee(employee);
        employeeCase.setCaseType(request.getCaseType());
        employeeCase.setStatus(request.getStatus());
        employeeCase.setOpenedDate(request.getOpenedDate());
        employeeCase.setDescription(request.getDescription());
        employeeCase.setResolution(request.getResolution());
        EmployeeCase saved = employeeCaseRepository.save(employeeCase);
        auditTrailService.log("EMPLOYEE", "EmployeeCase", String.valueOf(saved.getId()), "CREATE",
                "Opened " + saved.getCaseType() + " case for employee " + employee.getEmployeeNumber());
        return saved;
    }

    public SuccessionPlan createSuccessionPlan(SuccessionPlanRequest request) {
        SuccessionPlan successionPlan = new SuccessionPlan();
        successionPlan.setPositionOwner(employeeService.findById(request.getPositionOwnerEmployeeId()));
        successionPlan.setSuccessor(employeeService.findById(request.getSuccessorEmployeeId()));
        successionPlan.setReadinessLevel(request.getReadinessLevel());
        successionPlan.setDevelopmentActions(request.getDevelopmentActions());
        SuccessionPlan saved = successionPlanRepository.save(successionPlan);
        auditTrailService.log("EMPLOYEE", "SuccessionPlan", String.valueOf(saved.getId()), "CREATE",
                "Created succession plan for owner " + saved.getPositionOwner().getEmployeeNumber());
        return saved;
    }

    public OffboardingRecord createOffboarding(OffboardingRecordRequest request) {
        Employee employee = employeeService.findById(request.getEmployeeId());
        if (request.getLastWorkingDate().isBefore(employee.getHireDate())) {
            throw new IllegalArgumentException("Last working date cannot be before date joined");
        }
        OffboardingRecord record = new OffboardingRecord();
        record.setEmployee(employee);
        record.setLastWorkingDate(request.getLastWorkingDate());
        record.setExitInterviewCompleted(request.isExitInterviewCompleted());
        record.setAssetsReturned(request.isAssetsReturned());
        record.setFinanceClearanceCompleted(request.isFinanceClearanceCompleted());
        record.setRecordsClearanceCompleted(request.isRecordsClearanceCompleted());
        record.setAccountDeactivationCompleted(request.isAccountDeactivationCompleted());
        record.setNotes(request.getNotes());
        OffboardingRecord saved = offboardingRecordRepository.save(record);
        applyOffboardingOutcome(employee, saved);
        auditTrailService.log("EMPLOYEE", "OffboardingRecord", String.valueOf(saved.getId()), "CREATE",
                "Started offboarding for employee " + employee.getEmployeeNumber());
        return saved;
    }

    public ServiceLevelAgreement createServiceLevelAgreement(ServiceLevelAgreementRequest request) {
        ServiceLevelAgreement agreement = new ServiceLevelAgreement();
        agreement.setAgreementName(request.getAgreementName());
        agreement.setCounterparty(request.getCounterparty());
        agreement.setSignedDate(request.getSignedDate());
        agreement.setSigned(request.isSigned());
        agreement.setDocumentPath(request.getDocumentPath());
        agreement.setNotes(request.getNotes());
        ServiceLevelAgreement saved = serviceLevelAgreementRepository.save(agreement);
        auditTrailService.log("EMPLOYEE", "ServiceLevelAgreement", String.valueOf(saved.getId()), "CREATE",
                "Stored SLA " + saved.getAgreementName());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<OnboardingRecord> onboarding(Long employeeId) {
        employeeService.findById(employeeId);
        return onboardingRecordRepository.findByEmployeeId(employeeId);
    }

    @Transactional(readOnly = true)
    public List<TrainingRecord> training(Long employeeId) {
        employeeService.findById(employeeId);
        return trainingRecordRepository.findByEmployeeId(employeeId);
    }

    @Transactional(readOnly = true)
    public List<EmployeeCase> cases(Long employeeId) {
        employeeService.findById(employeeId);
        return employeeCaseRepository.findByEmployeeId(employeeId);
    }

    @Transactional(readOnly = true)
    public List<OffboardingRecord> offboarding(Long employeeId) {
        employeeService.findById(employeeId);
        return offboardingRecordRepository.findByEmployeeId(employeeId);
    }

    @Transactional(readOnly = true)
    public List<RecruitmentRequest> recruitmentRequests() {
        return recruitmentRequestRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<SuccessionPlan> successionPlans() {
        return successionPlanRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ServiceLevelAgreement> agreements() {
        return serviceLevelAgreementRepository.findAll();
    }

    @Transactional(readOnly = true)
    public WorkforceAnalyticsResponse analytics() {
        List<Employee> employees = employeeService.findAll();
        Map<String, Long> byDepartment = new LinkedHashMap<String, Long>();
        long active = 0;
        long inactive = 0;
        long terminated = 0;
        for (Employee employee : employees) {
            if (employee.getDepartment() != null) {
                String departmentName = employee.getDepartment().getName();
                byDepartment.put(departmentName, byDepartment.getOrDefault(departmentName, 0L) + 1L);
            }
            if (employee.getStatus() == EmploymentStatus.ACTIVE) {
                active++;
            } else if (employee.getStatus() == EmploymentStatus.TERMINATED) {
                terminated++;
            } else {
                inactive++;
            }
        }
        long onboardingInProgress = onboardingRecordRepository.findAll().stream().filter(record -> !record.isCompleted()).count();
        long offboardingInProgress = offboardingRecordRepository.findAll().stream()
                .filter(record -> !record.isAssetsReturned() || !record.isFinanceClearanceCompleted()).count();
        long openRecruitment = recruitmentRequestRepository.findAll().stream()
                .filter(request -> request.getStatus() == RecruitmentRequestStatus.OPEN
                        || request.getStatus() == RecruitmentRequestStatus.INTERVIEWING
                        || request.getStatus() == RecruitmentRequestStatus.OFFERED)
                .count();
        return WorkforceAnalyticsResponse.builder()
                .totalHeadcount(employees.size())
                .activeHeadcount(active)
                .inactiveHeadcount(inactive)
                .terminatedHeadcount(terminated)
                .onboardingInProgress(onboardingInProgress)
                .offboardingInProgress(offboardingInProgress)
                .openRecruitmentRequests(openRecruitment)
                .departmentHeadcount(byDepartment)
                .build();
    }

    private void applyOffboardingOutcome(Employee employee, OffboardingRecord record) {
        boolean fullyCleared = record.isExitInterviewCompleted()
                && record.isAssetsReturned()
                && record.isFinanceClearanceCompleted()
                && record.isRecordsClearanceCompleted()
                && record.isAccountDeactivationCompleted();
        employee.setTerminationDate(record.getLastWorkingDate());
        employee.setRecordsCleared(record.isRecordsClearanceCompleted());
        employee.setAccountActive(!record.isAccountDeactivationCompleted());
        if (fullyCleared) {
            employee.setStatus(EmploymentStatus.TERMINATED);
            auditTrailService.log("EMPLOYEE", "Employee", String.valueOf(employee.getId()), "OFFBOARD_COMPLETE",
                    "Completed offboarding and deactivated account for employee " + employee.getEmployeeNumber());
        }
    }

    private boolean isHrManager(Employee employee) {
        return employee.getDepartment() != null
                && employee.getDepartment().getName() != null
                && "human resources".equalsIgnoreCase(employee.getDepartment().getName())
                && employee.getJobTitle() != null
                && employee.getJobTitle().getName() != null
                && employee.getJobTitle().getName().toLowerCase().contains("manager");
    }
}
