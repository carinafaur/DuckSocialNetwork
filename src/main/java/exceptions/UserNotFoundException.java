package exceptions;

public class UserNotFoundException extends Exception implements AppException{
    private String errorMessage;
    public UserNotFoundException(String message,String errorMessage) {
        super(message);
        this.errorMessage = errorMessage;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
