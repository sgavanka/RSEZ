package app.rsez.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Event extends ModelBase {
    private String title;
    private String description;

    private Date startDateTime;

    private String hostUserId;

    public Event(String documentId, String title, String description, Date startDateTime, String hostUserId) {
        super(documentId);

        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.hostUserId = hostUserId;
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

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getHostUserId() {
        return hostUserId;
    }

    public void setHostUserId(String hostUserId) {
        this.hostUserId = hostUserId;
    }

    public void create() {
        Map<String, Object> event = new HashMap<>();
        event.put("title", title);
        event.put("description", description);
        event.put("startDateTime", startDateTime);
        event.put("hostUserId", hostUserId);

        db.collection("events").document(getDocumentId()).set(event);
    }
}
