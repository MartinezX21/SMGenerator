package com.uds.urifia.smgenerator.utils;

import com.uds.urifia.smgenerator.smanet.model.Subject;

public interface OnSubjectItemChanged {
    void onSubscribe(Subject s);
    void onUnsubscribe(Subject s);
    void onSubjectPressed(Subject s);
}
