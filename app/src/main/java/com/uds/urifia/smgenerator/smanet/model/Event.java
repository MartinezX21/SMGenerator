package com.uds.urifia.smgenerator.smanet.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * This class act both like EventsTable and EventsToSend entry
 */
public class Event implements Serializable {
    private String eventId;
    private String subjectId;
    private Date eventDate;
    private long validity;
    private String description;
    private String path;

    //not persisted fields
    private String fileName;
    private Subject subject;
    private ArrayList<Neighbor> receivers;

    /**
     *
     * @param eventId
     * @param subjectId
     * @param eventDate
     * @param validity the date when this event expires
     * @param description
     */
    public Event(String eventId,
                 String subjectId,
                 Date eventDate,
                 long validity,
                 String description,
                 String path) {
        this.eventId = eventId;
        this.subjectId = subjectId;
        this.eventDate = eventDate;
        this.validity = validity;
        this.description = description;
        this.path = path;

        receivers = new ArrayList<>();
    }

    public Event(String eventId, String subjectId, Date eventDate, long validity, String description) {
        this.eventId = eventId;
        this.subjectId = subjectId;
        this.eventDate = eventDate;
        this.validity = validity;
        this.description = description;

        receivers = new ArrayList<>();
    }

    public Event(String eventId, String topicId) {
        this.eventId = eventId;
        this.subjectId = topicId;

        receivers = new ArrayList<>();
    }

    public Event(String eventId) {
        this.eventId = eventId;

        receivers = new ArrayList<>();
    }

    public Event() {
        //no yet implemented
        receivers = new ArrayList<>();
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public long getValidity() {
        return validity;
    }

    public void setValidity(long validity) {
        this.validity = validity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public void addReceiver(Neighbor n) {
        if (!isAReceiver(n)) {
            receivers.add(n);
        }
    }

    public void removeReceiver(Neighbor n) {
        Neighbor neighbor = findReceiver(n.getNeighborId());
        if (neighbor != null) {
            receivers.remove(neighbor);
        }
    }

    public boolean isAReceiver(Neighbor neighbor) {
        return findReceiver(neighbor.getNeighborId()) != null;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Neighbor findReceiver(String neighborId) {
        Iterator<Neighbor> i = receivers.iterator();
        Neighbor n;
        while (i.hasNext()) {
            n = i.next();
            if (n.getNeighborId().equals(neighborId)) {
                return n;
            }
        }
        return null;
    }

    public ArrayList<Neighbor> getReceivers() {
        return receivers;
    }

    public String getEntry(){
        File f = new File(getPath());
        String name = getEventId();
        if(f.isFile()) {
            name = f.getName();
        }
        return subjectId+"@"+eventId+"@"+eventDate.getTime()+"@"+validity+"@"+description+"@"+name;
    }

    public static Event makeEvent(String e){
        String[] champs = e.split("@");
        Event res = new Event();
        res.setSubjectId(champs[0]);
        res.setEventId(champs[1]);
        res.setEventDate(new Date(Long.parseLong(champs[2])));
        res.setValidity(Long.parseLong(champs[3]));
        res.setDescription(champs[4]);
        res.setFileName(champs[5]);

        return res;
    }
}
