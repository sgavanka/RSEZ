package app.rsez.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Event extends ModelBase {
    String eventId;

    String title;
    String description;

    Date datetime;

    String hostUserId;

    public Event(String eventId, String title, String description, Date datetime, String hostUserId) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.datetime = datetime;
        this.hostUserId = hostUserId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public String getHostUserId() {
        return hostUserId;
    }

    public void setHostUserId(String hostUserId) {
        this.hostUserId = hostUserId;
    }

    public void create() {
        Map<String, Object> event = new HashMap<>();
        event.put("eventId", eventId);
        event.put("title", title);
        event.put("description", description);
        event.put("datetime", datetime);
        event.put("hostUserId", hostUserId);

        db.collection("events").add(event);
    }
}
