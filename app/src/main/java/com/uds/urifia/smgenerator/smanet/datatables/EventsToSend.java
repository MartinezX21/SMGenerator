package com.uds.urifia.smgenerator.smanet.datatables;

import java.util.ArrayList;
import java.util.Iterator;

public class EventsToSend {
    private ArrayList<Entry> entries;

    public EventsToSend() {
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
        private ArrayList<String> receivers;

        public Entry(String subjectId, String eventId, long publishingDate, long validity) {
            this.subjectId = subjectId;
            this.eventId = eventId;
            this.publishingDate = publishingDate;
            this.validity = validity;
            this.receivers = new ArrayList<>();
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

        public ArrayList<String> getReceivers() {
            return receivers;
        }

        public void setReceivers(ArrayList<String> receivers) {
            this.receivers = receivers;
        }

        public void addReceiver(String neighborId) {
            if (this.findReceiver(neighborId) == null) {
                this.receivers.add(neighborId);
            }
        }

        public boolean removeReveiver(String neighborId) {
            return this.receivers.remove(neighborId);
        }

        public String findReceiver(String neighborId) {
            Iterator<String> iterator = this.receivers.iterator();
            String current;
            while (iterator.hasNext()) {
                current = iterator.next();
                if (current.equals(neighborId)) return current;
            }
            return null;
        }
    }
}
