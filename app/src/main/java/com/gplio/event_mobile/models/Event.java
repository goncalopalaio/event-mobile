package com.gplio.event_mobile.models;

/**
 * Created by goncalopalaio on 29/04/18.
 */

public class Event {
    public String description;

    public Event(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Event{" +
                "description='" + description + '\'' +
                '}';
    }
}
