package hrms.payroll.dto;

import hrms.payroll.model.PayrollStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Builder
public class PayrollRunResponse {

    private Long payrollRunId;
    private String payrollCode;
    private String currencyCode;
    private String baseCurrencyCode;
    private Date payDate;
    private Date periodStart;
    private Date periodEnd;
    private BigDecimal grossPay;
    private BigDecimal totalDeductions;
    private BigDecimal netPay;
    private boolean journalPosted;
    private PayrollStatus status;
    private List<PayrollEntryResponse> entries;
    private List<PayrollJournalEntryResponse> journalEntries;
}
