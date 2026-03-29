package hrms.audit.service.impl;

import hrms.audit.dto.AuditTrailResponse;
import hrms.audit.entity.AuditTrail;
import hrms.audit.repository.AuditTrailRepository;
import hrms.audit.service.AuditTrailService;
import hrms.common.util.DateUtils;
import hrms.common.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AuditTrailServiceImpl implements AuditTrailService {

    private final AuditTrailRepository auditTrailRepository;

    public AuditTrailServiceImpl(AuditTrailRepository auditTrailRepository) {
        this.auditTrailRepository = auditTrailRepository;
    }

    public void log(String moduleName, String entityName, String entityId, String action, String details) {
        AuditTrail auditTrail = new AuditTrail();
        auditTrail.setModuleName(moduleName);
        auditTrail.setEntityName(entityName);
        auditTrail.setEntityId(entityId);
        auditTrail.setAction(action);
        auditTrail.setChangedBy("system");
        auditTrail.setChangedAt(DateUtils.now());
        auditTrail.setDetails(details);
        auditTrailRepository.save(auditTrail);
    }

    @Transactional(readOnly = true)
    public List<AuditTrailResponse> latest(String moduleName) {
        List<AuditTrail> records = StringUtils.isBlank(moduleName)
                ? auditTrailRepository.findTop50ByOrderByChangedAtDesc()
                : auditTrailRepository.findByModuleNameOrderByChangedAtDesc(moduleName);
        List<AuditTrailResponse> responses = new ArrayList<AuditTrailResponse>();
        for (AuditTrail record : records) {
            responses.add(AuditTrailResponse.builder()
                    .id(record.getId())
                    .moduleName(record.getModuleName())
                    .entityName(record.getEntityName())
                    .entityId(record.getEntityId())
                    .action(record.getAction())
                    .changedBy(record.getChangedBy())
                    .changedAt(record.getChangedAt())
                    .details(record.getDetails())
                    .build());
        }
        return responses;
    }
}
