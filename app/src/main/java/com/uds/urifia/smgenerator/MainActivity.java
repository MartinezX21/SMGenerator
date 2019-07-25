package com.uds.urifia.smgenerator;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.uds.urifia.smgenerator.adapters.SubjectAdapter;
import com.uds.urifia.smgenerator.smanet.core.EventsGCTask;
import com.uds.urifia.smgenerator.smanet.core.NotificationService;
import com.uds.urifia.smgenerator.smanet.core.nearby_api.listeners.ConnectionLifeCycleListener;
import com.uds.urifia.smgenerator.smanet.core.nearby_api.listeners.EndpointDiscoveryListener;
import com.uds.urifia.smgenerator.smanet.managers.EventManager;
import com.uds.urifia.smgenerator.smanet.managers.datastorage.EventDataSource;
import com.uds.urifia.smgenerator.smanet.model.Event;
import com.uds.urifia.smgenerator.smanet.model.Neighbor;
import com.uds.urifia.smgenerator.smanet.model.Subject;
import com.uds.urifia.smgenerator.smanet.managers.SubscriptionManager;
import com.uds.urifia.smgenerator.smanet.managers.datastorage.SubscriptionDataSource;
import com.uds.urifia.smgenerator.utils.Constants;
import com.uds.urifia.smgenerator.utils.OnSubjectItemChanged;
import com.uds.urifia.smgenerator.utils.Query;
import com.uds.urifia.smgenerator.utils.QueryEntry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "NOTIFY_ME_CHANNEL";
    private static final int NOTIFICATION_ID = 0;
    private static final String TAG = "MAIN_ACTIVITY";
    private static final String[] REQUIRED_PERMISSIONS =
            new String[] {
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;

    private RecyclerView recyclerView;
    private SubjectAdapter subjectAdapter = null;
    private RecyclerView.LayoutManager layoutManager;

    private SubscriptionManager sm;
    private SubscriptionDataSource dataSource;
    private EventDataSource eventDataSource;
    private EventManager em;

    private Runnable mNGCTask = null;
    private boolean disseminationStarted = false;
    private boolean NGCStoped = false;
    private boolean HBStoped = false;

    public static final Strategy STRATEGY = Strategy.P2P_CLUSTER;
    public static final String CLIENT_NAME = Build.MODEL;
    public static final String SERVICE_ID = "com.uds.urifia.smgenerator";

    private ConnectionLifeCycleListener connectionLifeCycleListener = null;
    private EndpointDiscoveryListener endpointDiscoveryListener = null;
    private static HashMap<String, Neighbor> neighbors = new HashMap<>();
    private static HashMap<String, Event> eventBeingReceive = new HashMap<>();
    private static ArrayList<String> eventsAlreadyReceived = new ArrayList<>();
    private static HashMap<String, Payload> payloadBeingReceived = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchSubjectActivity.class);
                startActivity(intent);
            }
        });

        dataSource = new SubscriptionDataSource(getApplicationContext());
        dataSource.open();
        sm = SubscriptionManager.getInstance(getApplicationContext(), dataSource);
        eventDataSource = new EventDataSource(getApplicationContext());
        eventDataSource.open();
        em = EventManager.getInstance(getApplicationContext(), eventDataSource, dataSource);

        recyclerView = findViewById(R.id.subscriptions_recycler_view);
        //recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        subjectAdapter = new SubjectAdapter(true, new ArrayList<Subject>(), new OnSubjectItemChanged() {
            @Override
            public void onSubscribe(Subject s) {
                // Nothing to provide here
            }

            @Override
            public void onUnsubscribe(Subject s) {
                // Nothing to provide here
            }

            @Override
            public void onSubjectPressed(Subject s) {
                Intent intent = new Intent(getApplicationContext(), Events.class);
                intent.putExtra("SUBJECT", s);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(subjectAdapter);

        createNotificationChannel();
        startETGCTask();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_start_scan) {
            startNetwork();
            return true;
        }
        else if (id == R.id.action_stop_scan) {
            stopNetwork();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            if (Build.VERSION.SDK_INT < 23) {
                ActivityCompat.requestPermissions(
                        this, REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
            } else {
                requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
            }
        }
    }

    @Override
    protected void onResume() {
        subjectAdapter.setDataset(sm.getSubscriptions());
        subjectAdapter.notifyDataSetChanged();
        startNGCTask();
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (mNGCTask != null) stopNGCTask();

        stopNetwork();
    }

    @CallSuper
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_REQUIRED_PERMISSIONS) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    displayFlashNotification(getString(R.string.error_missing_permissions), false);
                    finish();
                    return;
                }
            }
            recreate();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void startETGCTask() {
        WorkManager.getInstance().cancelAllWorkByTag(Constants.GCTaskTAG);
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .setRequiresBatteryNotLow(true)
                .build();
        PeriodicWorkRequest saveRequest =
                new PeriodicWorkRequest.Builder(EventsGCTask.class, Constants.ETGCDelay, TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .addTag(Constants.GCTaskTAG)
                        .build();

        WorkManager.getInstance()
                .enqueue(saveRequest);
    }

    public void startNetwork(){
        stopNetwork();
        startAdvertising();
        startDiscovery();
    }

    public void stopNetwork(){
        Nearby.getConnectionsClient(this).stopAdvertising();
        Nearby.getConnectionsClient(this).stopDiscovery();
    }

    private void startAdvertising() {
        connectionLifeCycleListener = new ConnectionLifeCycleListener(this);
        Nearby.getConnectionsClient(this).startAdvertising(CLIENT_NAME, SERVICE_ID,
                connectionLifeCycleListener, new AdvertisingOptions(STRATEGY))
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unusedResult) {
                                // We're advertising!
                                displayFlashNotification("We are advertising!", true);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // We were unable to start advertising.
                                displayFlashNotification("We were unable to start advertising!", true);
                            }
                        });
    }

    public void startDiscovery() {
        endpointDiscoveryListener = new EndpointDiscoveryListener(this);
        Nearby.getConnectionsClient(this).startDiscovery(
                SERVICE_ID,
                endpointDiscoveryListener,
                new DiscoveryOptions(STRATEGY))
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unusedResult) {
                                // We're discovering!
                                displayFlashNotification("We are discovering!", true);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // We were unable to start discovering.
                                displayFlashNotification("We were unable to start discovering!", true);
                            }
                        });
    }

    public void addNeighbor(String s){
        if(containsNeighborId(s)) return;

        neighbors.put(s, new Neighbor(s));
        if (isHBStoped()){
            startHBTask();
        }
        displayFlashNotification(neighbors.size()+" voisins connecté(s)", true);
    }

    public void removeNeighbor(String s){
        if(containsNeighborId(s)) {
            neighbors.remove(s);

            if (neighbors.size() == 0){
                setHBStoped(true);
            }
            displayFlashNotification(neighbors.size()+" voisins connecté(s)", true);
        }
    }

    public boolean containsNeighborId(String s){
        boolean contain = false;
        Iterator<String> i = neighbors.keySet().iterator();
        String current;
        while (!contain && i.hasNext()){
            current = i.next();
            if (current.equals(s)){
                contain = true;
            }
        }
        return contain;
    }

    public void requestConnection(@NonNull final String endpointId){
        if (containsNeighborId(endpointId)) return;

        Nearby.getConnectionsClient(this).requestConnection(
                CLIENT_NAME,
                endpointId,
                connectionLifeCycleListener)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unusedResult) {
                                displayFlashNotification("request connection succeeded", true);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                displayFlashNotification("failed to connect", true);
                            }
                        });

    }

    private void executeNGCTask() {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        ArrayList<String> toRemove = new ArrayList<>();
        Neighbor neighbor;
        for (String neighborId: neighbors.keySet()) {
            neighbor = neighbors.get(neighborId);
            if (neighbor.getStoredTime() + Constants.NGC_DELAY <= currentTime){
                toRemove.add(neighborId);
            }
        }

        for(String found: toRemove) {
            neighbors.remove(found);
        }
    }

    public boolean isNGCStoped() {
        return NGCStoped;
    }

    public void stopNGCTask(){
        NGCStoped = true;
    }

    public void startNGCTask() {
        // tache de recuperation de la memoire occupee par la table NeighborhoodTable
        final Handler handler = new Handler();
        if (mNGCTask == null){
            mNGCTask = new Runnable() {
                @Override
                public void run() {
                    if (isNGCStoped()) return;

                    executeNGCTask();
                    //relancer cette tâche dans (NGCDelay) millisecondes
                    handler.postDelayed(this, Constants.NGC_DELAY);
                }
            };
        }
        // lancer la tâche tout de suite
        handler.postDelayed(mNGCTask, 0);
    }

    public boolean isHBStoped() {
        return HBStoped;
    }

    public void setHBStoped(boolean HBStoped) {
        this.HBStoped = HBStoped;
    }

    public void startHBTask(){
        List<String> ids = new ArrayList<>();
        for (String neighborId: neighbors.keySet()){
            ids.add(neighborId);
        }
        startResearchTask(ids);
    }

    public void startResearchTask(final List<String> endpointIds){
        final Handler handler = new Handler();
        Runnable researchTask = new Runnable() {
            @Override
            public void run() {
                //if (getSubscriptionsManager().getInterests().isEmpty()) return;
                if (isHBStoped()) return;

                // Send this message as a bytes payload.
                String message = "$"+em.computeQuery().getQuery()+"$";
                //String message = "test1";
                Log.e(TAG, "Sending query: " + message);
                try {
                    Nearby.getConnectionsClient(getApplicationContext()).sendPayload(endpointIds, Payload.fromBytes(message.getBytes("UTF-8")));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                //programmer le nouvel envoi
                //...
                handler.postDelayed(this, Constants.RESEARCH_DELAY);
            }
        };
        //lancer cette tache tout de suite
        //handler.postDelayed(researchTask, activity.getCore().getResearchDelay());
        handler.postDelayed(researchTask, 0);
    }

    public void sendFilePayload(ArrayList<String> receivers, Event event){
        File file = new File(event.getPath());
        try {
            Nearby.getConnectionsClient(getApplicationContext()).sendPayload(receivers, Payload.fromFile(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void notifyReceptionProcess(String endpointId, String msg){
        Event event = Event.makeEvent(msg);
        if (alreadyReceived(event.getEventId())) return;

        eventBeingReceive.put(endpointId, event);
        Log.e(TAG, endpointId+" Event -> "+msg);
    }

    private boolean alreadyReceived(String id){
        for (String eventId: eventsAlreadyReceived){
            if (eventId.equals(id)) return true;
        }
        return false;
    }

    public void notifyReception(String endpointId){
        Event event = eventBeingReceive.get(endpointId);
        Payload payload = payloadBeingReceived.get(endpointId);
        if (event != null && payload != null){
            File root = getFilesDir();
            File pub = payload.asFile().asJavaFile();
            if (root.getFreeSpace() >  pub.length()) {
                String dirPath = root.getAbsolutePath() + "/" + event.getSubjectId().replaceAll("\\.", "/");
                File dir = new File(dirPath);
                File publication = null;

                if (dir.isDirectory() || dir.mkdirs()) {
                    FileInputStream input = null;
                    FileOutputStream output = null;
                    boolean status = false;

                    try {
                        publication = new File(dirPath + "/" + event.getFileName());
                        Log.e(TAG,"Setting the received file "+publication.getAbsolutePath());

                        output = new FileOutputStream(publication);
                        input = new FileInputStream(pub);

                        byte[] buf = new byte[64];
                        int n = 0;
                        while ((n = input.read(buf)) >= 0){
                            output.write(buf);
                            buf = new byte[64];
                        }
                        status = true;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        try {
                            if (output != null) output.close();
                            if (input != null) input.close();
                        } catch (IOException e) {
                            //ignore
                        }
                    }

                    if (status){
                        event.setPath(publication.getAbsolutePath());
                        em.saveEvent(event);

                        Log.e(TAG, "Sending notification to user ...");
                        String title = getString(R.string.app_name);
                        String description = "Nouvelle publication reçue";
                        showNotification(title, description, event.getDescription(), event.getSubjectId());
                    }
                }
            }

            eventBeingReceive.remove(endpointId);
            payloadBeingReceived.remove(endpointId);
            eventsAlreadyReceived.add(event.getEventId());
        }
    }

    public void putPayload(String endpointId, Payload payload){
        boolean exist = false;
        for (String str:eventBeingReceive.keySet()){
            if (str.equals(endpointId)) exist = true;
        }
        if (exist) payloadBeingReceived.put(endpointId, payload);
    }

    public void startDissemination(){
        if (disseminationStarted) return;

        disseminationStarted = true;
        ArrayList<Event> eventsToSend = computeEventsToSend();
        ArrayList<String> receivers;
        for (final Event event: eventsToSend){
            receivers = getReceivers(event);
            if (receivers.size() > 0) {
                String StrEvent = event.getEntry();
                try {
                    Nearby.getConnectionsClient(getApplicationContext()).sendPayload(receivers, Payload.fromBytes(StrEvent.getBytes("UTF-8")));
                    sendFilePayload(receivers, event);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        if (eventsToSend.isEmpty()){
            displayFlashNotification("no event to send", true);
        }

        disseminationStarted = false;
    }

    private ArrayList<String> getReceivers(Event event) {
        ArrayList<String> receivers = new ArrayList<>();
        Neighbor neighbor;
        for(String key: neighbors.keySet()) {
            neighbor = neighbors.get(key);
            if(neighbor.interestInTopic(event.getSubjectId()) && !neighbor.hasEvent(event)) {
                receivers.add(key);
            }
        }
        Log.e(TAG, receivers.size() + " receivers found");
        return receivers;
    }

    private ArrayList<Event> computeEventsToSend(){
        ArrayList<Event> eventsToSend = new ArrayList<>();
        for (Event event: em.getEvents()){
            Iterator<Neighbor> i = neighbors.values().iterator();
            Event toSend;
            Neighbor neighbor;
            while (i.hasNext()){
                neighbor = i.next();
                if (!neighbor.hasEvent(new Event(event.getEventId(), event.getSubjectId()))){
                    toSend = new Event(
                            event.getEventId(),
                            event.getSubjectId(),
                            event.getEventDate(),
                            event.getValidity(),
                            event.getDescription());
                    toSend.setPath(event.getPath());
                    boolean exist = false;
                    for(Event e: eventsToSend) {
                        if(e.getEventId().equals(toSend.getEventId())) {
                            e.addReceiver(neighbor);
                            exist = true;
                        }
                    }
                    if (!exist) {
                        toSend.addReceiver(neighbor);
                        eventsToSend.add(toSend);
                    }
                }
            }
        }
        return eventsToSend;
    }

    public void updateNeighborInfo(String neighborId, Query query){ // add an event id for a neighbor entry
        Log.e(TAG, "Received: " + query.getQuery());
        if(containsNeighborId(neighborId)) {
            Neighbor neighbor = neighbors.get(neighborId);
            for (QueryEntry q: query.getEntries()){
                for (String e: q.getEventIds()) {
                    neighbor.addEvent(new Event(e, q.getTopicId()));
                }
            }
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String title, String description, String content, String subjectId) {
        // Create an explicit intent for an Activity in your app
        Subject s = sm.getSubject(subjectId);
        Intent intent = new Intent(MainActivity.this, Events.class);
        intent.putExtra("SUBJECT", s);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_app_notification)
                .setContentTitle(title)
                .setContentText(description)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public void displayFlashNotification(String message, boolean brief){
        Toast.makeText(getApplicationContext(), message, brief? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
        Log.e(TAG, message);
    }

}
