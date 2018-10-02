package app.rsez.models;

import java.util.Date;

public class Attendee {
    String attendeeId;

    String eventId;
    String userId;

    Date checkInDateTime;

    public Attendee(String attendeeId, String eventId, String userId, Date checkInDateTime) {
        this.attendeeId = attendeeId;
        this.eventId = eventId;
        this.userId = userId;
        this.checkInDateTime = checkInDateTime;
    }

    public String getAttendeeId() {
        return attendeeId;
    }

    public void setAttendeeId(String attendeeId) {
        this.attendeeId = attendeeId;
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
}
