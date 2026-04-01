package hrms.web.config;

import hrms.common.dto.CommonResponse;
import hrms.common.exception.DuplicateResourceException;
import hrms.common.exception.OperationNotAllowedException;
import hrms.common.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CommonResponse<Void>> handleNotFound(ResourceNotFoundException exception) {
        return buildResponse(HttpStatus.NOT_FOUND, friendlyMessage(exception.getMessage()));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<CommonResponse<Void>> handleDuplicate(DuplicateResourceException exception) {
        return buildResponse(HttpStatus.CONFLICT, friendlyMessage(exception.getMessage()));
    }

    @ExceptionHandler(OperationNotAllowedException.class)
    public ResponseEntity<CommonResponse<Void>> handleOperationNotAllowed(OperationNotAllowedException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, friendlyMessage(exception.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse<Void>> handleIllegalArgument(IllegalArgumentException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, friendlyMessage(exception.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResponse<Void>> handleConstraintViolation(ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().stream()
                .findFirst()
                .map(violation -> violation.getMessage())
                .orElse("The request contains invalid values.");
        return buildResponse(HttpStatus.BAD_REQUEST, friendlyMessage(message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleUnexpected(Exception exception) {
        LOGGER.error("Unhandled API exception", exception);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong while processing your request. Please try again.");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(this::formatFieldError)
                .orElse("Please check the submitted details and try again.");
        return response(HttpStatus.BAD_REQUEST, message);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        return response(HttpStatus.BAD_REQUEST, "The request body could not be understood. Please check the submitted data.");
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers,
                                                                          HttpStatus status,
                                                                          WebRequest request) {
        return response(HttpStatus.BAD_REQUEST, readableName(ex.getParameterName()) + " is required.");
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex,
                                                                     HttpHeaders headers,
                                                                     HttpStatus status,
                                                                     WebRequest request) {
        return response(HttpStatus.BAD_REQUEST, readableName(ex.getRequestPartName()) + " is required.");
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
                                                             Object body,
                                                             HttpHeaders headers,
                                                             HttpStatus status,
                                                             WebRequest request) {
        return response(status, friendlyMessage(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CommonResponse<Void>> handleRequestBinding(MethodArgumentTypeMismatchException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Some request values are missing or invalid. Please review your input and try again.");
    }

    private ResponseEntity<CommonResponse<Void>> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(CommonResponse.failure(status.value(), safeMessage(message)));
    }

    private ResponseEntity<Object> response(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(CommonResponse.failure(status.value(), safeMessage(message)));
    }

    private String formatFieldError(FieldError error) {
        return readableName(error.getField()) + " " + error.getDefaultMessage();
    }

    private String readableName(String value) {
        return value == null ? "Field" : value.replaceAll("([a-z])([A-Z])", "$1 $2")
                .replaceAll("[_.\\-]", " ")
                .trim();
    }

    private String safeMessage(String message) {
        return (message == null || message.trim().isEmpty())
                ? "Something went wrong while processing your request."
                : message;
    }

    private String friendlyMessage(String message) {
        String safeMessage = safeMessage(message).trim();
        if (safeMessage.contains(":")) {
            String[] parts = safeMessage.split(":", 2);
            String prefix = parts[0].trim().toLowerCase();
            String suffix = parts[1].trim();
            if (prefix.endsWith("not found")) {
                return suffix.isEmpty()
                        ? "The requested record was not found."
                        : suffix + " was not found.";
            }
            if (prefix.endsWith("already exists")) {
                return suffix.isEmpty()
                        ? "A matching record already exists."
                        : suffix + " already exists.";
            }
        }
        return safeMessage.chars().anyMatch(Character::isLowerCase)
                ? safeMessage
                : safeMessage.substring(0, 1).toUpperCase() + safeMessage.substring(1).toLowerCase();
    }
}
