package domain;

import java.util.Objects;

public class Friendship {
    private FriendshipType friendshipType;
    private Long id1;
    private Long id2;
    private FriendshipStatus friendshipStatus =FriendshipStatus.NOT_FRIENDS;

    public Friendship(Long id1, Long id2, FriendshipType friendshipType) {
        this.id1=id1;
        this.id2=id2;
        this.friendshipType=friendshipType;
    }

    public Long getUser1(){
        return id1;
    }

    public Long getUser2(){
        return id2;
    }

    public FriendshipType getFriendshipType(){
        return friendshipType;
    }

    public FriendshipStatus getFriendshipStatus(){return friendshipStatus;}

    public void setFriendshipStatus(FriendshipStatus friendshipStatus){
        this.friendshipStatus=friendshipStatus;
    }

    public Long getOther(Long id){
        if(Objects.equals(id, id1)){
            return id2;
        }
        else if(Objects.equals(id, id2)){
            return id1;
        }
        return  null;
    }

    public String toString(){
        return "User1: "+id1+" User2: "+id2+" FriendshipType: "+friendshipType;
    }
}
