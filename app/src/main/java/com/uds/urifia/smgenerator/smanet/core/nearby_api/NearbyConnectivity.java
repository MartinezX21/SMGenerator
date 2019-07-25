/*
package com.uds.urifia.smgenerator.smanet.core.nearby_api;

import android.os.Build;
import android.support.annotation.NonNull;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.uds.urifia.smgenerator.MainActivity;
import com.uds.urifia.smgenerator.smanet.core.nearby_api.listeners.ConnectionLifeCycleListener;
import com.uds.urifia.smgenerator.smanet.core.nearby_api.listeners.EndpointDiscoveryListener;

import java.util.List;

public class NearbyConnectivity {
    private static final String SERVICE_ID = "com.uds.urifia.smgenerator";
    private static final Strategy STRATEGY = Strategy.P2P_CLUSTER;

    private boolean isAdvertising = false;
    private boolean isDiscovering = false;

    MainActivity activity;
    ConnectionLifeCycleListener connectionLifeCycleListener;
    EndpointDiscoveryListener endpointDiscoveryListener;

    public NearbyConnectivity(MainActivity activity,
                              ConnectionLifeCycleListener connectionLifeCycleListener,
                              EndpointDiscoveryListener endpointDiscoveryListener) {
        this.activity = activity;
        this.connectionLifeCycleListener = connectionLifeCycleListener;
        this.endpointDiscoveryListener = endpointDiscoveryListener;
    }

    public void send(Payload payload, List<String> receivers) {
        Nearby.getConnectionsClient(activity).sendPayload(receivers, payload);
    }

    public void requestConnection(@NonNull final String endpointId){
        Nearby.getConnectionsClient(activity).requestConnection(
                getName(),
                endpointId,
                connectionLifeCycleListener)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unusedResult) {
                                activity.displayFlashNotification("connection initiated", true);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                activity.displayFlashNotification("failed to connect", true);
                            }
                        });

    }

    public void startDiscovery() {
        endpointDiscoveryListener = new EndpointDiscoveryListener(activity);
        Nearby.getConnectionsClient(activity).startDiscovery(
                SERVICE_ID,
                endpointDiscoveryListener,
                new DiscoveryOptions(STRATEGY))
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unusedResult) {
                                // We're discovering!
                                setDiscovering(true);
                                activity.displayFlashNotification("We are discovering!", true);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // We were unable to start discovering.
                                activity.displayFlashNotification("We were unable to start discovering.", true);
                            }
                        });
    }

    public void startAdvertising() {
        connectionLifeCycleListener = new ConnectionLifeCycleListener(activity);
        Nearby.getConnectionsClient(activity).startAdvertising(getName(), SERVICE_ID,
                connectionLifeCycleListener, new AdvertisingOptions(STRATEGY))
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unusedResult) {
                                // We're advertising!
                                setAdvertising(true);
                                activity.displayFlashNotification("We are advertising!", true);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // We were unable to start advertising.
                                activity.displayFlashNotification("We were unable to start advertising.", true);
                            }
                        });
    }

    private String getName() {
        return Build.MODEL;
    }

    public boolean isAdvertising() {
        return isAdvertising;
    }

    public void setAdvertising(boolean advertising) {
        isAdvertising = advertising;
    }

    public boolean isDiscovering() {
        return isDiscovering;
    }

    public void setDiscovering(boolean discovering) {
        isDiscovering = discovering;
    }

    public void stopAdvertising() {
        Nearby.getConnectionsClient(activity).stopAdvertising();
        setAdvertising(false);
    }

    public void stopDiscovering() {
        Nearby.getConnectionsClient(activity).stopDiscovery();
        setDiscovering(false);
    }
}
*/
