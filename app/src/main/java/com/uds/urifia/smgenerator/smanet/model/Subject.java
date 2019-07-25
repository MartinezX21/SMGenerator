package com.uds.urifia.smgenerator.smanet.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class Subject implements Serializable {
    private String id;
    private String code;
    private String name;
    private String description;

    private boolean interested;
    private ArrayList<Subject> subtopics;

    public Subject(String id) {
        this.id = id;
    }

    public Subject(String id, String code, String name, String description) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.subtopics = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Subject> getSubtopics() {
        return subtopics;
    }

    public void setSubtopics(ArrayList<Subject> subtopics) {
        this.subtopics = subtopics;
    }

    public boolean isInterested() {
        return interested;
    }

    public void setInterested(boolean interested) {
        this.interested = interested;
    }

    public void addSubTopic(Subject son) {
        if(this.findSubTopic(son) == null) {
            this.subtopics.add(son);
        }
    }

    public boolean isFatherOf(Subject s) {
        return s.isSonOf(this);
    }

    public boolean isSonOf(Subject s) {
        return this.getId().startsWith(s.getId());
    }

    public Subject findSubTopic(Subject s) {
        Iterator<Subject> iterator = this.subtopics.iterator();
        Subject current;
        while (iterator.hasNext()) {
            current = iterator.next();
            if (current.getId().equals(s.getId())){
                return current;
            }
        }
        return null;
    }

    public Subject clone() {
        Subject s = new Subject(this.id, this.code, this.name, this.description);
        ArrayList<Subject> subtopics_ = new ArrayList<>();
        for(Subject subtopic: this.getSubtopics()) {
            subtopics_.add(subtopic.clone());
        }
        s.setSubtopics(subtopics_);
        return s;
    }

    public boolean equals(Subject s) {
        if(!this.getId().equals(s.getId())) return false;
        else if(!this.getCode().equals(s.getCode())) return false;
        else if(!this.getName().equals(s.getName())) return false;
        else if (!this.getDescription().equals(s.getDescription())) return false;
        else if (this.getSubtopics().size() != s.getSubtopics().size()) return false;
        else {
            for (int i=0; i<getSubtopics().size(); i++) {
                if (!getSubtopics().get(i).equals(s.getSubtopics().get(i))) return false;
            }
        }
        return true;
    }
}
