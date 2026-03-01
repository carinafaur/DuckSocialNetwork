package exceptions;

public class FriendshipAlreadyExists extends RuntimeException implements AppException{
    private String errorMessage;
    public FriendshipAlreadyExists(String message, String errorMessage){
        super(message);
        this.errorMessage=errorMessage;
    }

    public String getErrorMessage(){
        return errorMessage;
    }
}