package exceptions;

public class FriendshipNotFound extends RuntimeException implements AppException{
    private String errorMessage;
    public FriendshipNotFound(String message, String errorMessage){
        super(message);
        this.errorMessage=errorMessage;
    }

    public String getErrorMessage(){
        return errorMessage;
    }
}
