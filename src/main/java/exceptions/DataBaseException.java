package exceptions;

public class DataBaseException extends Exception implements AppException{
    private String errorMessage;

    public DataBaseException(String message, String errorMessage){
        super(message);
        this.errorMessage=errorMessage;
    }

    public String getErrorMessage(){
        return errorMessage;
    }
}
