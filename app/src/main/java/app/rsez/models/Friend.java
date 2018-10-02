package app.rsez.models;

public class Friend {
    User user;

    public Friend(User user){
        this.user = user;
    }

    public User getUser(){
        return user;
    }

    public User[] getMutualFriends() {
        return null;
    }
}
