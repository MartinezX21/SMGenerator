package com.uds.urifia.smgenerator;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.uds.urifia.smgenerator.adapters.SubjectAdapter;
import com.uds.urifia.smgenerator.smanet.model.Subject;
import com.uds.urifia.smgenerator.smanet.managers.SubscriptionManager;
import com.uds.urifia.smgenerator.smanet.managers.datastorage.SubscriptionDataSource;
import com.uds.urifia.smgenerator.utils.OnSubjectItemChanged;

import java.util.ArrayList;

public class SearchSubjectActivity extends AppCompatActivity {
    //private static final String TAG = "SEARCH-SUBJECT-ACTIVITY";
    private RecyclerView recyclerView;
    private SubjectAdapter subjectAdapter = null;
    private RecyclerView.LayoutManager layoutManager;

    private SubscriptionManager sm;
    private ArrayList<Subject> subjects = new ArrayList<>();
    private SubscriptionDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_subject);

        dataSource = new SubscriptionDataSource(getApplicationContext());
        dataSource.open();
        sm = SubscriptionManager.getInstance(getApplicationContext(), dataSource);

        recyclerView = findViewById(R.id.subjects_recycler_view);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        if(sm.SUBJECTS_LOADED) {
            this.subjects = sm.getSubjects();
            subjectAdapter = new SubjectAdapter(false, this.subjects, new OnSubjectItemChanged() {
                @Override
                public void onSubscribe(Subject s) {
                    if(sm.subscribe(s)) {
                        s.setInterested(true);
                        updateSubject(s);
                    }
                }

                @Override
                public void onUnsubscribe(Subject s) {
                    if(sm.unSubscribe(s)) {
                        s.setInterested(false);
                        updateSubject(s);
                    }
                }

                @Override
                public void onSubjectPressed(Subject s) {
                    // nothing do provide here.
                }
            });
        }
        recyclerView.setAdapter(subjectAdapter);
        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return onSearchRequested();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSearchRequested() {
        //...
        return super.onSearchRequested();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onDestroy() {
        //dataSource.close();
        super.onDestroy();
    }


    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            handleSearch(query);
        }
    }

    private void handleSearch(String query) {
        ArrayList<Subject> results = new ArrayList<>();
        for (Subject s: this.subjects) {
            if(s.getName().toLowerCase().contains(query.toLowerCase())) {
                results.add(s);
            }
        }
        subjectAdapter.setDataset(results);
        subjectAdapter.notifyDataSetChanged();
    }

    private void updateSubject(Subject s) {
        for (Subject current: this.subjects) {
            if (current.getId().equals(s.getId())) {
                current.setInterested(s.isInterested());
            }
        }
    }
}
