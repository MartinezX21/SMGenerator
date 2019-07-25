package com.uds.urifia.smgenerator.smanet.core.nearby_api.listeners;

import android.support.annotation.NonNull;

import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.uds.urifia.smgenerator.MainActivity;

/**
 * Created by Martin on 19/06/2018.
 */

public class EndpointDiscoveryListener extends EndpointDiscoveryCallback {
    MainActivity activity;
    /*PublicationsActivity mPublicationsActivity;*/

    public EndpointDiscoveryListener(MainActivity activity) {
        this.activity = activity;
    }

    /*public EndpointDiscoveryListener(PublicationsActivity activity) {
        this.mPublicationsActivity = activity;
    }*/

    @Override
    public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
        activity.displayFlashNotification(endpointId+" found!", true);
        activity.requestConnection(endpointId);
    }

    @Override
    public void onEndpointLost(@NonNull String endpointId) {
        activity.displayFlashNotification(endpointId+" lost", true);
    }

}