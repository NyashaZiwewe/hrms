package hrms.web.service;

import hrms.employee.entity.Employee;
import hrms.employee.entity.EmploymentConfirmationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmploymentConfirmationNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmploymentConfirmationNotificationService.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmploymentConfirmationNotificationService(ObjectProvider<JavaMailSender> mailSenderProvider,
                                                     @Value("${hrms.notifications.from:no-reply@hrms.local}") String fromAddress) {
        this.mailSender = mailSenderProvider.getIfAvailable();
        this.fromAddress = fromAddress;
    }

    public void notifyHrManagersOfRequest(Employee employee,
                                          EmploymentConfirmationRequest request,
                                          List<Employee> hrManagers) {
        List<String> recipients = new ArrayList<String>();
        for (Employee hrManager : hrManagers) {
            if (hrManager.getEmail() != null && !hrManager.getEmail().trim().isEmpty()) {
                recipients.add(hrManager.getEmail().trim());
            }
        }
        if (recipients.isEmpty()) {
            LOGGER.warn("No HR manager email recipients found for confirmation request {}", request.getId());
            return;
        }
        sendEmail(recipients.toArray(new String[0]),
                "Confirmation of employment request",
                employee.getFirstName() + " " + employee.getLastName()
                        + " has requested a confirmation of employment letter."
                        + (request.getPurpose() == null || request.getPurpose().trim().isEmpty() ? "" : " Purpose: " + request.getPurpose().trim()));
    }

    public void notifyEmployeeOfSignedLetter(Employee employee, EmploymentConfirmationRequest request) {
        if (request.getDeliveryEmail() == null || request.getDeliveryEmail().trim().isEmpty()) {
            LOGGER.warn("No delivery email found for signed confirmation request {}", request.getId());
            return;
        }
        sendEmail(new String[]{request.getDeliveryEmail().trim()},
                "Your confirmation of employment is ready",
                "Your confirmation of employment letter has been signed and is ready for download in HRMS.");
    }

    private void sendEmail(String[] recipients, String subject, String text) {
        if (mailSender == null) {
            LOGGER.warn("JavaMailSender is not available. Email notification skipped for subject '{}'", subject);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(recipients);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception exception) {
            LOGGER.warn("Failed to send email notification for subject '{}': {}", subject, exception.getMessage());
        }
    }
}
