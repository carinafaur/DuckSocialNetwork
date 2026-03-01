package exceptions;

public class UserAlreadyExistsException extends Exception implements AppException{
    private String errorMessage;
    public UserAlreadyExistsException(String message,String errorMessage){
        super(message);
        this.errorMessage=errorMessage;
    }

    public String getErrorMessage(){
        return errorMessage;
    }
}
