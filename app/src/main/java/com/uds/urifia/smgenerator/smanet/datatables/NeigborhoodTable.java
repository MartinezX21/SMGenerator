package com.uds.urifia.smgenerator.smanet.datatables;

import java.util.ArrayList;
import java.util.Iterator;

public class NeigborhoodTable {
    private ArrayList<Entry> entries;

    public NeigborhoodTable() {
        this.entries = new ArrayList<>();
    }

    public void add(Entry e) {
        if(this.find(e.getNeighborId(), e.getSubjectId()) == null) {
            this.entries.add(e);
        }
    }

    public boolean remove(Entry e) {
        return this.entries.remove(e);
    }

    public ArrayList<Entry> getEntries() {
        return entries;
    }

    public void setEntries(ArrayList<Entry> entries) {
        this.entries = entries;
    }

    public Entry find(String neighborId, String sujectId) {
        Iterator<Entry> iterator = this.entries.iterator();
        Entry current;
        while (iterator.hasNext()) {
            current = iterator.next();
            if (current.getNeighborId().equals(neighborId) && current.getSubjectId().equals(sujectId)){
                return current;
            }
        }
        return null;
    }

    public class Entry {
        private String neighborId;
        private String subjectId;
        private ArrayList<String> eventsIds;

        public Entry(String neighborId, String subjectId) {
            this.neighborId = neighborId;
            this.subjectId = subjectId;
            this.eventsIds = new ArrayList<>();
        }

        public void addEvent(String eventId) {
            if (this.findEvent(eventId) == null){
                this.eventsIds.add(eventId);
            }
        }

        public String findEvent(String eventId) {
            Iterator<String> iterator = this.eventsIds.iterator();
            String current;
            while (iterator.hasNext()) {
                current = iterator.next();
                if (current.equals(eventId)) return current;
            }
            return null;
        }

        public String getNeighborId() {
            return neighborId;
        }

        public void setNeighborId(String neighborId) {
            this.neighborId = neighborId;
        }

        public String getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(String subjectId) {
            this.subjectId = subjectId;
        }

        public ArrayList<String> getEventsIds() {
            return eventsIds;
        }

        public void setEventsIds(ArrayList<String> eventsIds) {
            this.eventsIds = eventsIds;
        }
    }

}
