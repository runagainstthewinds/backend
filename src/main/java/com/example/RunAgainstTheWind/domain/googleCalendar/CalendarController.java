package com.example.RunAgainstTheWind.domain.googleCalendar;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/calendar")
@CrossOrigin(origins = {"*"}, 
    allowedHeaders = "*", 
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class CalendarController {

    @PostMapping("/events")
    public ResponseEntity<Map<String, Object>> receiveCalendarEvents(@RequestBody CalendarEventsRequest request) {

        System.out.println("Received calendar events from user: " + request.getUserId());
        System.out.println("Timestamp: " + request.getTimestamp());
        System.out.println("Number of events: " + request.getEvents().size());
        
        for (CalendarEvent event : request.getEvents()) {
            System.out.println("\nEvent ID: " + event.getId());
            System.out.println("Summary: " + event.getSummary());
            System.out.println("Description: " + (event.getDescription() != null ? event.getDescription() : "N/A"));
            System.out.println("Start: " + event.getStart().getDateTime() + " (" + event.getStart().getTimeZone() + ")");
            System.out.println("End: " + event.getEnd().getDateTime() + " (" + event.getEnd().getTimeZone() + ")");
            System.out.println("Location: " + (event.getLocation() != null ? event.getLocation() : "N/A"));
            
            if (event.getAttendees() != null && !event.getAttendees().isEmpty()) {
                System.out.println("Attendees:");
                for (Attendee attendee : event.getAttendees()) {
                    System.out.println("  - " + attendee.getEmail() + " (" + attendee.getResponseStatus() + ")");
                }
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Received " + request.getEvents().size() + " calendar events");
        response.put("eventsProcessed", request.getEvents().size());
        
        return ResponseEntity.ok(response);
    }
}

class CalendarEventsRequest {
    private List<CalendarEvent> events;
    private String userId;
    private String timestamp;

    public List<CalendarEvent> getEvents() {
        return events;
    }
    
    public void setEvents(List<CalendarEvent> events) {
        this.events = events;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

class CalendarEvent {
    private String id;
    private String summary;
    private String description;
    private EventDateTime start;
    private EventDateTime end;
    private String location;
    private List<Attendee> attendees;

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public EventDateTime getStart() {
        return start;
    }
    
    public void setStart(EventDateTime start) {
        this.start = start;
    }
    
    public EventDateTime getEnd() {
        return end;
    }
    
    public void setEnd(EventDateTime end) {
        this.end = end;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public List<Attendee> getAttendees() {
        return attendees;
    }
    
    public void setAttendees(List<Attendee> attendees) {
        this.attendees = attendees;
    }
}

class EventDateTime {
    private String dateTime;
    private String timeZone;

    public String getDateTime() {
        return dateTime;
    }
    
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
    
    public String getTimeZone() {
        return timeZone;
    }
    
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}

class Attendee {
    private String email;
    private String responseStatus;
 
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getResponseStatus() {
        return responseStatus;
    }
    
    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }
}
