package com.uds.urifia.smgenerator;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.uds.urifia.smgenerator.adapters.EventAdapter;
import com.uds.urifia.smgenerator.smanet.managers.EventManager;
import com.uds.urifia.smgenerator.smanet.managers.datastorage.EventDataSource;
import com.uds.urifia.smgenerator.smanet.managers.datastorage.SubscriptionDataSource;
import com.uds.urifia.smgenerator.smanet.model.Event;
import com.uds.urifia.smgenerator.smanet.model.Subject;
import com.uds.urifia.smgenerator.utils.OnEventSelectionHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Events extends AppCompatActivity implements OnEventSelectionHandler {
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter = null;
    private RecyclerView.LayoutManager layoutManager;

    SubscriptionDataSource subscriptionDataSource;
    EventDataSource eventDataSource;
    EventManager em;

    Subject subject;
    ArrayList<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        Toolbar toolbar = findViewById(R.id.events_toolbar);
        setSupportActionBar(toolbar);

        subject = (Subject) getIntent().getSerializableExtra("SUBJECT");
        FloatingActionButton fab = findViewById(R.id.events_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Publish.class);
                intent.putExtra("SUBJECT", subject);
                startActivity(intent);
            }
        });

        subscriptionDataSource = new SubscriptionDataSource(getApplicationContext());
        subscriptionDataSource.open();
        eventDataSource = new EventDataSource(getApplicationContext());
        eventDataSource.open();
        em = EventManager.getInstance(getApplicationContext(), eventDataSource, subscriptionDataSource);

        recyclerView = findViewById(R.id.events_recycler_view);
        //recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        eventAdapter = new EventAdapter(new ArrayList<Event>(), this, getApplicationContext());
        recyclerView.setAdapter(eventAdapter);
    }

    @Override
    protected void onResume() {
        eventList = em.getEvents(subject);
        eventAdapter.setDataset(eventList);
        eventAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onEventSelected(Event e) {
        String fileName = e.getPath();
        File requestFile = new File(fileName);
        try {
            final Uri data = FileProvider.getUriForFile(
                    Events.this,
                    "com.uds.urifia.smgenerator.fileprovider",
                    requestFile);
            grantUriPermission(getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            final Intent intent = new Intent(Intent.ACTION_VIEW);

            // Verify it resolves
            PackageManager packageManager = getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
            boolean isIntentSafe = activities.size() > 0;

            // Start an activity if it's safe
            if (isIntentSafe) {
                String mimeType = getContentResolver().getType(data);
                intent.setDataAndType(data, mimeType)
                      .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            }
        } catch (IllegalArgumentException ex) {
            Log.e("File Selector",
                    "The selected file can't be shared: " + requestFile.toString());
        }

    }
}
