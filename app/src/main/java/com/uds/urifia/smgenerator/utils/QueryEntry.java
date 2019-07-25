package com.uds.urifia.smgenerator.utils;

import java.util.ArrayList;

/**
 * Created by Martin on 12/06/2018.
 */

public class QueryEntry {
    private String topicId;
    private String eventIds;

    public QueryEntry(String topicId) {
        this.topicId = topicId;
        this.eventIds = "";
    }
    public QueryEntry(String topicId, String eventId) {
        this.topicId = topicId;
        this.eventIds = "";
        if (eventIds.equals("")) {
            eventIds = eventId;
        } else {
            eventIds = eventIds + ":" + eventId;
        }
    }

    public void addEventId(String e){
        if (!eventIds.contains(e)){
            if (eventIds.equals("")) eventIds = e;
            else eventIds = eventIds + ":" + e;
        }
    }

    public ArrayList<String> getEventIds(){
        ArrayList<String> list = new ArrayList<>();
        for (String s: eventIds.split(":")){
            if (!s.equals("")) list.add(s);
        }
        return list;
    }

    public String getStrEventIds(){
        return eventIds;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }
}