package hrms.audit.service;

import hrms.audit.dto.AuditTrailResponse;

import java.util.List;

public interface AuditTrailService {

    void log(String moduleName, String entityName, String entityId, String action, String details);

    List<AuditTrailResponse> latest(String moduleName);
}
