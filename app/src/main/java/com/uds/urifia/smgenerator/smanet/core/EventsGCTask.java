package com.uds.urifia.smgenerator.smanet.core;

import android.content.Context;
import android.support.annotation.NonNull;

import com.uds.urifia.smgenerator.smanet.managers.EventManager;
import com.uds.urifia.smgenerator.smanet.managers.datastorage.EventDataSource;
import com.uds.urifia.smgenerator.smanet.managers.datastorage.SubscriptionDataSource;
import com.uds.urifia.smgenerator.smanet.model.Event;

import java.io.File;
import java.util.Date;

import androidx.work.Worker;
import androidx.work.WorkerParameters;



public class EventsGCTask extends Worker {
    SubscriptionDataSource subscriptionDataSource;
    EventDataSource eventDataSource;
    EventManager em;

    public EventsGCTask(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        subscriptionDataSource = new SubscriptionDataSource(context);
        subscriptionDataSource.open();
        eventDataSource = new EventDataSource(context);
        eventDataSource.open();
        em = EventManager.getInstance(context, eventDataSource, subscriptionDataSource);
    }

    @NonNull
    @Override
    public Result doWork() {
        Date validityDate, today = new Date();
        File eventFile;
        for(Event e: em.getEvents()) {
            validityDate = new Date(e.getValidity());

            if(validityDate.before(today)) {
                // The event has expired.
                // Delete the event and the corresponding file.
                boolean deleted = true;
                eventFile = new File(e.getPath());
                if (eventFile.isFile()) {
                    deleted = deleted && eventFile.delete();
                }
                if (deleted) {
                    em.deleteEvent(e);
                }
            }
        }

        return Result.success();
    }
}
