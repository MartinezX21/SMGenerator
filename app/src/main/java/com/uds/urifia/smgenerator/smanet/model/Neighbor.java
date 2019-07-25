package com.uds.urifia.smgenerator.smanet.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class Neighbor implements Serializable {
    private String neighborId;
    private long storedTime;
    private ArrayList<Event> events;

    public Neighbor(String neighborId) {
        this.neighborId = neighborId;
        storedTime = Calendar.getInstance().getTimeInMillis();
        this.events = new ArrayList<>();
    }

    public String getNeighborId() {
        return neighborId;
    }

    public void setNeighborId(String neighborId) {
        this.neighborId = neighborId;
    }

    public void addEvent(Event e) {
        if (!hasEvent(e)) {
            events.add(e);
        }
    }

    public boolean hasEvent(Event event) {
        Iterator<Event> i = events.iterator();
        Event e;
        while (i.hasNext()) {
            e = i.next();
            if (e.getEventId().equals(event.getEventId())) {
                return true;
            }
        }
        return false;
    }

    public long getStoredTime() {
        return storedTime;
    }

    public boolean interestInTopic(String topicId) {
        for (Event e: events) {
            if(e.getSubjectId().equals(topicId)) return true;
        }
        return false;
    }
}
