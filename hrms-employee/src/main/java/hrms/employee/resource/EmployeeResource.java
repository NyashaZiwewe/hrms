package hrms.employee.resource;

import hrms.employee.dto.EmployeeRequest;
import hrms.employee.entity.Employee;
import hrms.employee.model.EmploymentStatus;
import hrms.employee.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeResource {

    private final EmployeeService employeeService;

    public EmployeeResource(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Employee create(@Valid @RequestBody EmployeeRequest request) {
        return employeeService.create(request);
    }

    @GetMapping
    public List<Employee> findAll(@RequestParam(required = false) String query,
                                  @RequestParam(required = false) String department,
                                  @RequestParam(required = false) String jobTitle,
                                  @RequestParam(required = false) EmploymentStatus status,
                                  @RequestParam(required = false) String hiredFrom,
                                  @RequestParam(required = false) String hiredTo) {
        if (query != null || department != null || jobTitle != null || status != null || hiredFrom != null || hiredTo != null) {
            return employeeService.search(
                    query,
                    department,
                    jobTitle,
                    status,
                    hiredFrom == null ? null : LocalDate.parse(hiredFrom),
                    hiredTo == null ? null : LocalDate.parse(hiredTo)
            );
        }
        return employeeService.findAll();
    }

    @GetMapping("/{id}")
    public Employee findById(@PathVariable Long id) {
        return employeeService.findById(id);
    }

    @PutMapping("/{id}")
    public Employee update(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return employeeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        employeeService.delete(id);
    }
}
