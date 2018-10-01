package app.rsez;

public class User {
    char id;
    String username;
    String password;
    String nickname;
    String email;
    Friend friends;
    Ticket tickets[];
    boolean status;
    Notification notification;

    public User(String username, String password, String nickname, String email){
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.friends = null;
        this.tickets = null;
        this.notification = null;
    }

    public char getId() {
        return id;
    }

    public void setId(char id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Friend getFriends() {
        return friends;
    }

    public void setFriends(Friend friends) {
        this.friends = friends;
    }

    public Ticket[] getTickets() {
        return tickets;
    }

    public void setTickets(Ticket[] tickets) {
        this.tickets = tickets;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public void createPDF(Ticket ticket) {

    }
}
