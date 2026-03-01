package exceptions;

public class DuckGroupException extends Exception implements AppException{
    private String errorMessage;

    public DuckGroupException(String message, String errorMessage){
        super(message);
        this.errorMessage=errorMessage;
    }

    public String getErrorMessage(){
        return errorMessage;
    }
}
