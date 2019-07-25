package com.uds.urifia.smgenerator.smanet.datatables;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * The validity attribute represents the validity period of the event an is set in seconds
 */
public class EventsTable {
    private ArrayList<Entry> entries;

    public EventsTable() {
        this.entries = new ArrayList<>();
    }

    public void add(Entry e) {
        if(this.find(e.getSubjectId(), e.getEventId()) == null) {
            this.entries.add(e);
        }
    }

    public Entry find(String subjectId, String eventId) {
        Iterator<Entry> iterator = this.entries.iterator();
        Entry current;
        while (iterator.hasNext()) {
            current = iterator.next();
            if (current.getSubjectId().equals(subjectId) && current.getEventId().equals(eventId)){
                return current;
            }
        }
        return null;
    }

    public boolean remove(Entry e) {
        return this.entries.remove(e);
    }

    public class Entry {
        private String subjectId;
        private String eventId;
        private long publishingDate;
        private long validity;

        public Entry(String subjectId, String eventId, long publishingDate, long validity) {
            this.subjectId = subjectId;
            this.eventId = eventId;
            this.publishingDate = publishingDate;
            this.validity = validity;
        }

        public String getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(String subjectId) {
            this.subjectId = subjectId;
        }

        public String getEventId() {
            return eventId;
        }

        public void setEventId(String eventId) {
            this.eventId = eventId;
        }

        public long getPublishingDate() {
            return publishingDate;
        }

        public void setPublishingDate(long publishingDate) {
            this.publishingDate = publishingDate;
        }

        public long getValidity() {
            return validity;
        }

        public void setValidity(long validity) {
            this.validity = validity;
        }
    }
}
