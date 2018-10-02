package app.rsez.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Ticket extends ModelBase {
    private String ticketId;

    private String eventId;
    private String userId;

    private Date checkInDateTime;

    public Ticket(String ticketId, String eventId, String userId, Date checkInDateTime) {
        this.ticketId = ticketId;
        this.eventId = eventId;
        this.userId = userId;
        this.checkInDateTime = checkInDateTime;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCheckInDateTime() {
        return checkInDateTime;
    }

    public void setCheckInDateTime(Date checkInDateTime) {
        this.checkInDateTime = checkInDateTime;
    }

    public void create() {
        Map<String, Object> ticket = new HashMap<>();
        ticket.put("ticketId", ticketId);
        ticket.put("eventId", eventId);
        ticket.put("userId", userId);
        ticket.put("checkInDateTime", checkInDateTime);

        db.collection("tickets").add(ticket);
    }
}
