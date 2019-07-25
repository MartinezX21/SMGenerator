package com.uds.urifia.smgenerator.smanet.datatables;

import com.uds.urifia.smgenerator.smanet.model.Subject;

import java.util.ArrayList;
import java.util.Iterator;

public class SubscriptionTable {
    private ArrayList<Subject> subjects;

    public SubscriptionTable() {
        this.subjects = new ArrayList<>();
    }

    public void add(Subject s) {
        this.subjects.add(s);
    }

    public Subject find(String subjectId) {
        Iterator<Subject> iterator = this.subjects.iterator();
        Subject current;
        while (iterator.hasNext()) {
            current = iterator.next();
            if(current.getId().equals(subjectId))
                return current;
        }
        return null;
    }

    public boolean remove(Subject s) {
        return this.subjects.remove(s);
    }

    public boolean isInterestedOn(Subject s) {
        Iterator<Subject> iterator = this.subjects.iterator();
        Subject current;
        while (iterator.hasNext()) {
            current = iterator.next();
            if(current.isFatherOf(s))
                return true;
        }
        return false;
    }
}
