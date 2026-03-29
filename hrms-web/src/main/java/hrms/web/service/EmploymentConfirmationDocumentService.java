package hrms.web.service;

import hrms.employee.entity.Employee;
import hrms.employee.entity.EmploymentConfirmationRequest;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class EmploymentConfirmationDocumentService {

    public GeneratedDocument generate(Employee employee,
                                      EmploymentConfirmationRequest request,
                                      Employee hrManager) {
        try {
            Path directory = Paths.get("uploads", "employment-confirmations");
            Files.createDirectories(directory);
            String fileName = "employment-confirmation-" + employee.getEmployeeNumber() + "-" + UUID.randomUUID() + ".pdf";
            Path target = directory.resolve(fileName);
            writePdf(target, employee, request, hrManager);
            return new GeneratedDocument(target.toAbsolutePath().toString(), fileName);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to generate employment confirmation letter", exception);
        }
    }

    private void writePdf(Path target,
                          Employee employee,
                          EmploymentConfirmationRequest request,
                          Employee hrManager) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        try {
            beginTextBlock(contentStream, 72, 730);
            writeLine(contentStream, PDType1Font.HELVETICA_BOLD, 16, "CONFIRMATION OF EMPLOYMENT");
            writeLine(contentStream, PDType1Font.HELVETICA, 12, "");
            writeLine(contentStream, PDType1Font.HELVETICA, 12, "Date: " + LocalDate.now());
            writeLine(contentStream, PDType1Font.HELVETICA, 12, "");
            writeLine(contentStream, PDType1Font.HELVETICA, 12, "To whom it may concern,");
            writeLine(contentStream, PDType1Font.HELVETICA, 12, "");
            writeLine(contentStream, PDType1Font.HELVETICA, 12,
                    employee.getFirstName() + " " + employee.getLastName() + " (" + employee.getEmployeeNumber() + ")");
            writeLine(contentStream, PDType1Font.HELVETICA, 12,
                    "is employed by this organisation as " + safeJobTitle(employee) + " in " + safeDepartment(employee) + ".");
            writeLine(contentStream, PDType1Font.HELVETICA, 12,
                    "The employee joined on " + String.valueOf(employee.getHireDate()) + " and is currently in " + String.valueOf(employee.getStatus()) + " status.");
            writeLine(contentStream, PDType1Font.HELVETICA, 12,
                    "The current monthly salary is " + employee.getPreferredCurrency() + " " + employee.getMonthlySalary() + ".");
            if (request.getPurpose() != null && !request.getPurpose().trim().isEmpty()) {
                writeLine(contentStream, PDType1Font.HELVETICA, 12, "");
                writeLine(contentStream, PDType1Font.HELVETICA, 12, "Purpose: " + request.getPurpose().trim());
            }
            writeLine(contentStream, PDType1Font.HELVETICA, 12, "");
            writeLine(contentStream, PDType1Font.HELVETICA, 12, "Signed by: " + hrManager.getFirstName() + " " + hrManager.getLastName());
            writeLine(contentStream, PDType1Font.HELVETICA, 12, "Title: " + safeJobTitle(hrManager));
            writeLine(contentStream, PDType1Font.HELVETICA, 12, "Date Signed: " + LocalDate.now());
        } finally {
            contentStream.endText();
            contentStream.close();
            document.save(target.toFile());
            document.close();
        }
    }

    private void beginTextBlock(PDPageContentStream contentStream, float x, float y) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
    }

    private void writeLine(PDPageContentStream contentStream,
                           PDType1Font font,
                           int fontSize,
                           String text) throws IOException {
        contentStream.setFont(font, fontSize);
        contentStream.showText(text == null ? "" : text);
        contentStream.newLineAtOffset(0, -20);
    }

    private String safeJobTitle(Employee employee) {
        return employee.getJobTitle() == null ? "N/A" : employee.getJobTitle().getName();
    }

    private String safeDepartment(Employee employee) {
        return employee.getDepartment() == null ? "N/A" : employee.getDepartment().getName();
    }

    public static class GeneratedDocument {
        private final String path;
        private final String fileName;

        public GeneratedDocument(String path, String fileName) {
            this.path = path;
            this.fileName = fileName;
        }

        public String getPath() {
            return path;
        }

        public String getFileName() {
            return fileName;
        }
    }
}
