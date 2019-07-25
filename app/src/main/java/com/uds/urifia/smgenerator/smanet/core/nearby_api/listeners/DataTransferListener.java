package com.uds.urifia.smgenerator.smanet.core.nearby_api.listeners;

import android.support.annotation.NonNull;

import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.uds.urifia.smgenerator.MainActivity;
import com.uds.urifia.smgenerator.utils.Query;

import java.io.UnsupportedEncodingException;

/**
 * Created by Martin on 19/06/2018.
 */

public class DataTransferListener extends PayloadCallback {
    MainActivity activity;

    public DataTransferListener(MainActivity activity) {
        this.activity = activity;
    }
    @Override
    public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
        String queryStr;
        if (payload.getType() == Payload.Type.BYTES) {
            try {
                String received = new String(payload.asBytes(), "UTF-8");
                if (received.startsWith("$") && received.endsWith("$")){
                    //c'est une requette
                    queryStr = received.substring(1, received.length()-1);
                    Query query = Query.makeQuery(queryStr);
                    activity.updateNeighborInfo(endpointId, query);

                    // lancer la phase de dissemination.
                    //...
                    activity.startDissemination();
                }else {
                    //c'est la description d'une publication (Event_Entry)
                    activity.notifyReceptionProcess(endpointId, received);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else
        if (payload.getType() == Payload.Type.FILE){
            // Une publication (fichier) est en cours de reception
            activity.putPayload(endpointId, payload);
        }
    }

    @Override
    public void onPayloadTransferUpdate(@NonNull String endpointId, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
        if (payloadTransferUpdate.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {
            activity.notifyReception(endpointId);
        }
    }
}