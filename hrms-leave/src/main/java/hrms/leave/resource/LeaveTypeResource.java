package hrms.leave.resource;

import hrms.leave.dto.LeaveTypeRequest;
import hrms.leave.entity.LeaveType;
import hrms.leave.service.LeaveService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/leave-types")
public class LeaveTypeResource {

    private final LeaveService leaveService;

    public LeaveTypeResource(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @GetMapping
    public List<LeaveType> findAll() {
        return leaveService.leaveTypes();
    }

    @GetMapping("/{leaveTypeId}")
    public LeaveType findById(@PathVariable Long leaveTypeId) {
        return leaveService.leaveType(leaveTypeId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeaveType create(@Valid @RequestBody LeaveTypeRequest input) {
        return leaveService.createLeaveType(input);
    }

    @PutMapping("/{leaveTypeId}")
    public LeaveType update(@PathVariable Long leaveTypeId, @Valid @RequestBody LeaveTypeRequest input) {
        return leaveService.updateLeaveType(leaveTypeId, input);
    }

    @DeleteMapping("/{leaveTypeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long leaveTypeId) {
        leaveService.deleteLeaveType(leaveTypeId);
    }
}
