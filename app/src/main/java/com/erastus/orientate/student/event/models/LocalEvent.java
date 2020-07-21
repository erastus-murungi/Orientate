package com.erastus.orientate.student.event.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;

import org.parceler.Parcel;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Parcel
public class LocalEvent {
    private static final String TAG = "LocalEvent";

    private LatLng eventLocation;
    private LocalDateTime startingOn;
    private LocalDateTime endingOn;
    private String url;
    private Integer upVoteCount;
    private boolean isRecurring;
    private boolean isMustAttend;
    private String title;
    private String body;
    private String eventLocationString;

    public LocalEvent() {}

    public LatLng getEventLocation() {
        return eventLocation;
    }

    public LocalDateTime getStartingOn() {
        return startingOn;
    }

    public LocalDateTime getEndingOn() {
        return endingOn;
    }

    public String getUrl() {
        return url;
    }

    public Integer getUpVoteCount() {
        return upVoteCount;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public boolean isMustAttend() {
        return isMustAttend;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public LocalEvent(Event event) {
        this.eventLocation = event.getWhere();
        this.startingOn = Objects.requireNonNull(event.getStartDateTime(), "No happening date");
        this.endingOn = event.getEndingDateTime();
        this.url = event.getUrl();
        this.upVoteCount = Objects.requireNonNull(event.getUpVoteCount());
        this.isRecurring = event.getIsRecurring();
        this.isMustAttend = event.getIsMustAttend();
        this.title = event.getTitle();
        this.body = event.getBody();
        this.eventLocationString = event.getLocationString();
    }

    public static @NonNull Set<LocalEvent> getLocalEventsSet(Collection<Event> events) {
        return events.stream().map(LocalEvent::new).collect(Collectors.toSet());
    }

    public String getStringLocation() {
        return eventLocationString;
    }


}
