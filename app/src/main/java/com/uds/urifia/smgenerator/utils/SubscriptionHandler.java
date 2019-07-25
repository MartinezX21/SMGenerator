package com.uds.urifia.smgenerator.utils;

import com.uds.urifia.smgenerator.smanet.model.Subject;

import java.util.ArrayList;

public interface SubscriptionHandler {
    void afterSubscribe(Subject s);
    void afterUnsubscribe(Subject s);
    void afterSubscriptionLoaded(ArrayList<Subject> subjects);
}
