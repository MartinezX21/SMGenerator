package com.uds.urifia.smgenerator.utils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Martin on 12/06/2018.
 */

public class Query {
    private ArrayList<QueryEntry> queries;

    public String getQuery() {
        String query = "";
        for (QueryEntry q: queries) {
            String newQuery = q.getTopicId() + ">" + q.getStrEventIds();
            if (query.equals("")) query = newQuery;
            else query = query + "~" + newQuery;
        }
        return query;
    }

    public Query(){
        queries = new ArrayList<>();
    }

    public boolean addEntry(QueryEntry e){
        boolean exist = false;
        Iterator<QueryEntry> iter = queries.iterator();
        while (!exist && iter.hasNext()){
            QueryEntry entry = iter.next();
            if (entry.getTopicId() == e.getTopicId()) exist = true;
        }
        if (!exist){
            queries.add(e);
            return true;
        }
        return false;
    }

    public void update(String topicId, String eventId){
        for (QueryEntry queryEntry : getEntries()) {
            if(queryEntry.getTopicId() == topicId){
                queryEntry.addEventId(eventId);
            }
        }
    }

    public static Query makeQuery(String text){
        Query q = new Query();
        for(String entry : text.split("~")){
            if (!entry.equals("")){
                String[] parts = entry.split(">");
                String topic = parts[0];
                String events = "";
                if (parts.length > 1) events = parts[1];
                q.addEntry(new QueryEntry(topic, events));
            }
        }
        return q;
    }

    public ArrayList<QueryEntry> getEntries(){
        return queries;
    }
}
