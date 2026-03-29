package hrms.leave.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class OvertimeClaimInput {

    @NotNull
    private Long employeeId;

    @NotNull
    @PastOrPresent
    private LocalDate workDate;

    @NotNull
    @DecimalMin("0.5")
    private BigDecimal hoursClaimed = BigDecimal.ZERO;

    private String reason;
}
