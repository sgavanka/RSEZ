package app.rsez;

public class Ticket {
    User owner;
    Event event;
    QRCode qrcode;

    public Ticket(Event event, QRCode qrcode, User owner){
        this.event = event;
        this.qrcode = qrcode;
        this.owner = owner;
    }

    public Ticket(Event event, QRCode qrcode){
        this.event = event;
        this.qrcode = qrcode;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Event getEvent() {
        return event;
    }

    public QRCode getQrcode() {
        return qrcode;
    }
}
