package hrms.payroll.dto;

import hrms.payroll.model.GeneralLedgerAccount;
import hrms.payroll.model.JournalEntryType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PayrollJournalEntryResponse {

    private GeneralLedgerAccount account;
    private JournalEntryType entryType;
    private BigDecimal amount;
    private String currencyCode;
    private String narration;
}
