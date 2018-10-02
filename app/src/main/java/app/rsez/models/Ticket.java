package app.rsez.models;

import java.util.HashMap;
import java.util.Map;

public class Ticket extends ModelBase {
    String ticketId;

    String eventId;
    String userId;

    public Ticket(String ticketId, String eventId, String userId) {
        this.ticketId = ticketId;
        this.eventId = eventId;
        this.userId = userId;
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

    public void create() {
        Map<String, Object> user = new HashMap<>();
        user.put("ticketId", ticketId);
        user.put("eventId", eventId);
        user.put("userId", userId);

        db.collection("tickets").add(user);
    }
}
