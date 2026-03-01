package exceptions;

public class EventException extends Exception implements AppException{
    private String errorMessage;

    public EventException(String message, String errorMessage){
        super(message);
        this.errorMessage=errorMessage;
    }

    public String getErrorMessage(){
        return errorMessage;
    }
}
