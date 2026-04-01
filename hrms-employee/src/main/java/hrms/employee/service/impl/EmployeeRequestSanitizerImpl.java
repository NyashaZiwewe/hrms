package hrms.employee.service.impl;

import hrms.common.util.StringUtils;
import hrms.employee.dto.EmployeeRequest;
import hrms.employee.service.EmployeeRequestSanitizer;
import org.springframework.stereotype.Component;

@Component
public class EmployeeRequestSanitizerImpl implements EmployeeRequestSanitizer {

    public void sanitize(EmployeeRequest request) {
        request.setEmployeeNumber(trimToNull(request.getEmployeeNumber()));
        request.setFirstName(trimToNull(request.getFirstName()));
        request.setMiddleName(trimToNull(request.getMiddleName()));
        request.setLastName(trimToNull(request.getLastName()));
        request.setEmail(trimToNull(request.getEmail()));
        request.setPhoneNumber(trimToNull(request.getPhoneNumber()));
        request.setNationalId(trimToNull(request.getNationalId()));
        request.setJobTitle(trimToNull(request.getJobTitle()));
        request.setDepartment(trimToNull(request.getDepartment()));
        request.setPreferredCurrency(trimToNull(request.getPreferredCurrency()));
        request.setContractDocumentPath(trimToNull(request.getContractDocumentPath()));
        request.setContractFileName(trimToNull(request.getContractFileName()));
        request.setEmploymentHistory(trimToNull(request.getEmploymentHistory()));
        request.setBenefitsSummary(trimToNull(request.getBenefitsSummary()));
        request.setPerformanceSummary(trimToNull(request.getPerformanceSummary()));
        if (!StringUtils.hasText(request.getPreferredCurrency())) {
            request.setPreferredCurrency("USD");
        }
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
