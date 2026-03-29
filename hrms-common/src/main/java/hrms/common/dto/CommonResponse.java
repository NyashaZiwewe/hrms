package hrms.common.dto;

public class CommonResponse<T> {

    private boolean success;
    private int statusCode;
    private String message;
    private T data;

    public CommonResponse() {
    }

    public CommonResponse(boolean success, int statusCode, String message, T data) {
        this.success = success;
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public static <T> CommonResponse<T> success(int statusCode, String message, T data) {
        return new CommonResponse<T>(true, statusCode, message, data);
    }

    public static <T> CommonResponse<T> failure(int statusCode, String message) {
        return new CommonResponse<T>(false, statusCode, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
