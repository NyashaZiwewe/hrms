package hrms.employee.service.impl;

import hrms.common.util.StringUtils;
import hrms.employee.entity.Employee;
import hrms.employee.repository.EmployeeRepository;
import hrms.employee.service.EmployeeNumberGenerator;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EmployeeNumberGeneratorImpl implements EmployeeNumberGenerator {

    private static final Pattern EMPLOYEE_NUMBER_SUFFIX_PATTERN = Pattern.compile("(\\d+)$");

    private final EmployeeRepository employeeRepository;

    public EmployeeNumberGeneratorImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public String nextEmployeeNumber() {
        int nextSequence = 1;
        for (Employee employee : employeeRepository.findAll()) {
            int currentSequence = extractSequence(employee.getEmployeeNumber());
            if (currentSequence >= nextSequence) {
                nextSequence = currentSequence + 1;
            }
        }

        String employeeNumber = formatEmployeeNumber(nextSequence);
        while (employeeRepository.findByEmployeeNumber(employeeNumber).isPresent()) {
            nextSequence++;
            employeeNumber = formatEmployeeNumber(nextSequence);
        }
        return employeeNumber;
    }

    private int extractSequence(String employeeNumber) {
        if (!StringUtils.hasText(employeeNumber)) {
            return 0;
        }
        Matcher matcher = EMPLOYEE_NUMBER_SUFFIX_PATTERN.matcher(employeeNumber.trim());
        if (!matcher.find()) {
            return 0;
        }
        try {
            return Integer.parseInt(matcher.group(1));
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private String formatEmployeeNumber(int sequence) {
        return String.format("EMP-%03d", sequence);
    }
}
