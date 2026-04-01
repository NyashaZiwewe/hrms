package hrms.leave.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class OvertimeClaimInput {

    @NotNull
    private Long employeeId;

    @NotNull
    @PastOrPresent
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date workDate;

    @NotNull
    @DecimalMin("0.5")
    private BigDecimal hoursClaimed = BigDecimal.ZERO;

    private String reason;
}
