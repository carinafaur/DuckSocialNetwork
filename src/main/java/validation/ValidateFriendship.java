package validation;

import domain.Friendship;
import domain.FriendshipType;

public class ValidateFriendship {
    private Friendship friendship;

    public ValidateFriendship(Friendship friendship) {
        this.friendship = friendship;
    }

    public boolean validate(){
        if(friendship.getFriendshipType()!= FriendshipType.DUCK_PERSON && friendship.getFriendshipType()!=FriendshipType.DUCK_DUCK && friendship.getFriendshipType()!=FriendshipType.PERSON_PERSON)
            return false;
        return true;
    }
}
