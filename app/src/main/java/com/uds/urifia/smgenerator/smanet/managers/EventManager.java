package com.uds.urifia.smgenerator.smanet.managers;

import android.content.Context;

import com.uds.urifia.smgenerator.smanet.managers.datastorage.EventDataSource;
import com.uds.urifia.smgenerator.smanet.managers.datastorage.SubscriptionDataSource;
import com.uds.urifia.smgenerator.smanet.model.Event;
import com.uds.urifia.smgenerator.smanet.model.Subject;
import com.uds.urifia.smgenerator.utils.Query;
import com.uds.urifia.smgenerator.utils.QueryEntry;

import java.util.ArrayList;

public class EventManager {
    private static EventDataSource dataSource;
    private static SubscriptionDataSource subscriptionDataSource;
    private static EventManager manager;
    private static Context appContext;

    private EventManager(Context ctx,
                         EventDataSource dataSource,
                         SubscriptionDataSource subscriptionDataSource) {
        this.appContext = ctx;
        this.dataSource = dataSource;
        this.subscriptionDataSource = subscriptionDataSource;
    }

    public static EventManager getInstance(Context ctx,
                                           EventDataSource dataSource,
                                           SubscriptionDataSource subscriptionDataSource) {
        if (manager == null) {
            manager = new EventManager(ctx, dataSource, subscriptionDataSource);
        }
        return manager;
    }

    public ArrayList<Event> getEvents() {
        ArrayList<Event> results = new ArrayList<>();
        for(Event e: dataSource.findAll()) {
            e.setSubject(subjectOf(e));
            results.add(e);
        }
        return results;
    }

    public ArrayList<Event> getEvents(Subject s) {
        ArrayList<Event> results = new ArrayList<>();
        if (s != null) {
            for(Event e: dataSource.findAllBySubject(s)) {
                e.setSubject(s);
                results.add(e);
            }
        }
        return results;
    }

    public Event saveEvent(Event e) {
        Event found = dataSource.find(e.getEventId());
        long id = -1;
        if (found == null) {
            id = dataSource.insert(e);
            if (id > 0) {
                found = e;
                found.setSubject(subjectOf(found));
            }
        }
        return found;
    }

    public boolean deleteEvent(final Event e) {
        Event found = dataSource.find(e.getEventId());
        if (found != null) {
            dataSource.delete(found);
            return true;
        }
        return false;
    }

    public boolean hasEvent(Event e) {
        Event found = dataSource.find(e.getEventId());
        if (found != null){
            return true;
        } else {
            return false;
        }
    }

    public Subject subjectOf(Event e) {
        return subscriptionDataSource.find(e.getSubjectId());
    }

    public Query computeQuery() {
        Query query = new Query();

        QueryEntry queryEntry;
        for(Event e: getEvents()) {
            queryEntry = new QueryEntry(e.getSubjectId(), e.getEventId());
            if(!query.addEntry(queryEntry)) {
                query.update(e.getSubjectId(), e.getEventId());
            }
        }

        return query;
    }
}
