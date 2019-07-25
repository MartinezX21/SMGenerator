package com.uds.urifia.smgenerator.smanet.managers;

import android.content.Context;

import com.uds.urifia.smgenerator.R;
import com.uds.urifia.smgenerator.smanet.model.Subject;
import com.uds.urifia.smgenerator.smanet.managers.datastorage.SubscriptionDataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class SubscriptionManager {
    public static boolean SUBJECTS_LOADED = false;

    private SubscriptionDataSource dataSource;
    private ArrayList<Subject> subjects;
    private static SubscriptionManager manager = null;
    private static Context appContext;

    private SubscriptionManager(Context ctx, SubscriptionDataSource dataSource) {
        this.dataSource = dataSource;
        this.subjects = new ArrayList<>();
        appContext = ctx;
        this.loadSubjects();
    }

    public static SubscriptionManager getInstance(Context ctx,
                                                  SubscriptionDataSource dataSource) {
        if (manager == null) {
            manager = new SubscriptionManager(ctx, dataSource);
        }
        return manager;
    }

    // load subjects from file and set the flag SUBJECTS_LOADED to true
    private boolean loadSubjects() {
        if (SUBJECTS_LOADED) return true;

        InputStream inputStream = appContext.getResources().openRawResource(R.raw.subjects);
        DocumentBuilder builder = null;

        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(inputStream, null);
            final String rootId = appContext.getResources().getString(R.string.subject_root_element_id);
            Element root = doc.getElementById(rootId);

            NodeList subjectElements = root.getChildNodes();
            Element subjectElement;
            Subject currentSubject;
            // Loading subject appearing in level 1
            for(int i = 0; i < subjectElements.getLength(); i++) {
                if(subjectElements.item(i) instanceof Element) {
                    subjectElement = (Element) subjectElements.item(i);
                    String code = subjectElement.getAttribute("code");
                    String name = subjectElement.getAttribute("name");
                    String description = subjectElement.getAttribute("description");
                    // the id of a subject of level 1 is its code
                    currentSubject = new Subject(code, code, name, description);
                    currentSubject.setSubtopics(this.loadChildren(subjectElement, currentSubject.getId()));
                    this.subjects.add(currentSubject);
                }
            }
            SUBJECTS_LOADED = true;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return SUBJECTS_LOADED;
    }

    /**
     * load local subscriptions
     * Subscriptions are subjects whose the user has subscribe to and it has an empty set of subtopics.
     */
    public ArrayList<Subject> getSubscriptions() {
        return dataSource.findAll();
    }

    public Subject getSubject(String subjectId) {
        return dataSource.find(subjectId);
    }

    public boolean subscribe(final Subject s) {
        Subject found = dataSource.find(s.getId());
        long id = -1;
        if (found == null) {
            id = dataSource.insert(s);

        }
        return id > 0;
    }

    public boolean unSubscribe(final Subject s) {
        Subject found = dataSource.find(s.getId());
        if (found != null) {
            dataSource.delete(found);
            return true;
        }
        return false;
    }

    public boolean hasSubscribeTo(Subject s) {
        Subject found = dataSource.find(s.getId());
        if (found != null){
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<Subject> getSubjects() {
        return this.getAllSubjectsIndividually();
    }

    private ArrayList<Subject> getAllSubjectsIndividually() {
        ArrayList<Subject> subjects_ = new ArrayList<>();
        for(Subject s: this.subjects) {
            subjects_.addAll(this.collect(s));
        }
        return subjects_;
    }

    /**
     * Get individually the subject -subject- and its children
     * Set the field -interested- of Subjects here
     * @param subject
     * @return children
     */
    private ArrayList<Subject> collect(Subject subject) {
        Subject s = subject.clone();
        if (this.hasSubscribeTo(s)) {
            s.setInterested(true);
        }
        ArrayList<Subject> children = new ArrayList<>();
        if (s.getSubtopics().isEmpty()) {
            children.add(s);
        } else {
            for (Subject child: s.getSubtopics()) {
                children.addAll(this.collect(child));
            }
            s.getSubtopics().clear();
            children.add(s);
        }
        return children;
    }

    /**
     * Load the children of a subject represented by -element-, whose id is
     * -fatherId- as an ArrayList of Subject
     * @param element
     * @param fatherId
     * @return children
     */
    private ArrayList<Subject> loadChildren(Element element, String fatherId) {
        ArrayList<Subject> children = new ArrayList<>();
        NodeList childrenElements = element.getChildNodes();
        Element childElement;
        Subject childSubject;
        for(int i = 0; i < childrenElements.getLength(); i++) {
            if(childrenElements.item(i) instanceof Element) {
                childElement = (Element) childrenElements.item(i);
                String code = childElement.getAttribute("code");
                String name = childElement.getAttribute("name");
                String description = childElement.getAttribute("description");
                /**
                 * the id of a child subject is the one of its father, follow
                 * by its code, both separated by a dot (.)
                 */
                String id = fatherId + "." + code;
                childSubject = new Subject(id, code, name, description);
                childSubject.setSubtopics(this.loadChildren(childElement, childSubject.getId()));
                children.add(childSubject);
            }
        }
        return children;
    }
}
