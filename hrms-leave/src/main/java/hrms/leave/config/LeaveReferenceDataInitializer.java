package hrms.leave.config;

import hrms.leave.entity.LeaveType;
import hrms.leave.repository.LeaveTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class LeaveReferenceDataInitializer implements CommandLineRunner {

    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveReferenceDataInitializer(LeaveTypeRepository leaveTypeRepository) {
        this.leaveTypeRepository = leaveTypeRepository;
    }

    public void run(String... args) {
        createIfMissing("ANNUAL", "Annual Leave", 2, true, true);
        createIfMissing("VACATION", "Vacation Leave", 2, true, true);
        createIfMissing("SICK", "Sick Leave", 1, true, false);
        createIfMissing("MATERNITY", "Maternity Leave", 3, false, false);
        createIfMissing("PATERNITY", "Paternity Leave", 1, true, false);
        createIfMissing("SPECIAL", "Special Leave", 0, false, false);
        createIfMissing("STUDY", "Study Leave", 0, false, false);
        createIfMissing("BEREAVEMENT", "Bereavement Leave", 0, false, false);
        createIfMissing("UNPAID", "Unpaid Leave", 0, false, false);
    }

    private void createIfMissing(String code,
                                 String name,
                                 int monthlyEntitlement,
                                 boolean balanceTracked,
                                 boolean leaveSaleAllowed) {
        if (leaveTypeRepository.findByCodeIgnoreCase(code).isPresent()) {
            return;
        }
        LeaveType leaveType = new LeaveType();
        leaveType.setCode(code);
        leaveType.setName(name);
        leaveType.setMonthlyEntitlement(monthlyEntitlement);
        leaveType.setBalanceTracked(balanceTracked);
        leaveType.setLeaveSaleAllowed(leaveSaleAllowed);
        leaveType.setActive(true);
        leaveTypeRepository.save(leaveType);
    }
}
