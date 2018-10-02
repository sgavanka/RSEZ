package app.rsez;

import android.graphics.Bitmap;

public class Event {
    QRCode qrcodes[];
    User hosts[];
    User ticketHolder[];
    Ticket tickets[];
    Bitmap image;
    String color;
    String title;
    String time;
    String date;
    String description;
    String notification;
    boolean checkedIn;
    String extraInfo;

    public Event(String title, String time, String date, String description, Bitmap image, User hosts[]){
        this.title = title;
        this.time = time;
        this.date = date;
        this.description = description;
        this.image = image;
        this.hosts = hosts;
    }

    public QRCode[] getQrcodes() {
        return qrcodes;
    }

    public void setQrcodes(QRCode[] qrcodes) {
        this.qrcodes = qrcodes;
    }

    public User[] getHosts() {
        return hosts;
    }

    public void setHosts(User[] hosts) {
        this.hosts = hosts;
    }

    public User[] getTicketHolder() {
        return ticketHolder;
    }

    public void setTicketHolder(User[] ticketHolder) {
        this.ticketHolder = ticketHolder;
    }

    public Ticket[] getTickets() {
        return tickets;
    }

    public void setTickets(Ticket[] tickets) {
        this.tickets = tickets;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public boolean isCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }
}
