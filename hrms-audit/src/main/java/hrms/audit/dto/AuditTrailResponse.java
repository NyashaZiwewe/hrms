package hrms.audit.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AuditTrailResponse {

    private Long id;
    private String moduleName;
    private String entityName;
    private String entityId;
    private String action;
    private String changedBy;
    private LocalDateTime changedAt;
    private String details;
}
