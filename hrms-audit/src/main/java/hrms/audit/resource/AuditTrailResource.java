package hrms.audit.resource;

import hrms.audit.dto.AuditTrailResponse;
import hrms.audit.service.AuditTrailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit-trails")
public class AuditTrailResource {

    private final AuditTrailService auditTrailService;

    public AuditTrailResource(AuditTrailService auditTrailService) {
        this.auditTrailService = auditTrailService;
    }

    @GetMapping
    public List<AuditTrailResponse> latest(@RequestParam(required = false) String moduleName) {
        return auditTrailService.latest(moduleName);
    }
}
