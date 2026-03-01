package exceptions;

public class ValidationException extends Exception implements AppException {
    private String errorMessage;

    public ValidationException(String message,String errorMessage) {
        super(message);
        this.errorMessage=errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
