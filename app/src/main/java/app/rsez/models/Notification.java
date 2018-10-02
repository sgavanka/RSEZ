package app.rsez.models;

public class Notification {
    Event event;
    String message;
    User sendTo;
    User sentBy;

    public Notification(Event event, String message, User sentBy){
        this.event = event;
        this.message = message;
        this.sentBy = sentBy;
    }

    public Notification(Event event, String message){
        this.event = event;
        this.message = message;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getSendTo() {
        return sendTo;
    }

    public void setSendTo(User sendTo) {
        this.sendTo = sendTo;
    }

    public User getSentBy() {
        return sentBy;
    }

    public void setSentBy(User sentBy) {
        this.sentBy = sentBy;
    }

    public void send(Event event, String message, User sendTo){

    }
}
