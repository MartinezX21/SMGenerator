package com.uds.urifia.smgenerator.smanet.core.nearby_api.listeners;

import android.support.annotation.NonNull;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.uds.urifia.smgenerator.MainActivity;

/**
 * Created by Martin on 19/06/2018.
 */

public class ConnectionLifeCycleListener extends ConnectionLifecycleCallback {
    MainActivity activity;
    /*PublicationsActivity mPublicationsActivity;*/
    DataTransferListener dataTransferListener;

    public ConnectionLifeCycleListener(MainActivity activity) {
        this.activity = activity;
        dataTransferListener = new DataTransferListener(activity);
    }

    /*public ConnectionLifeCycleListener(PublicationsActivity activity) {
        this.mPublicationsActivity = activity;
        dataTransferListener = new DataTransferListener(mPublicationsActivity);
    }*/

    @Override
    public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
        // Automatically accept the connection on both sides.
        if (!activity.containsNeighborId(endpointId)){
            Nearby.getConnectionsClient(activity).acceptConnection(endpointId, dataTransferListener);
        }
        else {
            Nearby.getConnectionsClient(activity).rejectConnection(endpointId);
        }

    }

    @Override
    public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution connectionResolution) {
        if (connectionResolution.getStatus().getStatusCode() == ConnectionsStatusCodes.STATUS_OK){
            // We are connected! Can now start sending and receiving data.
            activity.displayFlashNotification("Connected with "+endpointId, true);
            activity.addNeighbor(endpointId);
            activity.startHBTask();
        }
        else {
            activity.displayFlashNotification("Unable to connect with "+endpointId, true);
        }
    }

    @Override
    public void onDisconnected(@NonNull String endpointId) {
        activity.displayFlashNotification(endpointId+" disconnected", true);
        activity.removeNeighbor(endpointId);
    }


}