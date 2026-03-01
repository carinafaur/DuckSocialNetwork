package exceptions;

public class FriendshipException extends Exception implements AppException{
    private String errorMessage;
    public FriendshipException(String message,String errorMessage) {
        super(message);
        this.errorMessage=errorMessage;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
