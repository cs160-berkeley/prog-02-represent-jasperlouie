package com.example.jasper.represent;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class WatchToPhoneService extends Service implements GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient mWatchApiClient;
    private List<Node> nodes = new ArrayList<>();
    private ArrayList<String> message = new ArrayList<String>();

    @Override
    public void onCreate() {
        super.onCreate();
        //initialize the googleAPIClient for message passing
        mWatchApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addConnectionCallbacks(this)
                .build();
        //and actually connect it
        mWatchApiClient.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            Log.d("T", "intent not null");
            Bundle extras = intent.getExtras();
            String cmd = extras.getString("cmd");
            message = new ArrayList<String>();
            if(cmd.equals("zip")){
                message.add("/zip");
                message.add(extras.getString("zip"));
            }else if(cmd.equals("details")){
                message.add("/details");
                message.add(extras.getString("json"));
            }
        }

        Log.d("T", "start of onStartCommand");
        if(nodes.size() == 0){
            mWatchApiClient = new GoogleApiClient.Builder( this )
                    .addApi( Wearable.API )
                    .addConnectionCallbacks(this)
                    .build();
        }

        if(!mWatchApiClient.isConnected()){
            mWatchApiClient.connect();
        }
        Log.d("T", "Message is " + message);
        Log.d("T", "Trying to send message " + message);
        sendMessage(message.get(0), message.get(1));
        Log.d("T", "sent");
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mWatchApiClient.disconnect();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override //alternate method to connecting: no longer create this in a new thread, but as a callback
    public void onConnected(Bundle bundle) {
        Log.d("T", "in onconnected");
        Wearable.NodeApi.getConnectedNodes(mWatchApiClient)
                .setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                        if (message.size() > 0) {
                            nodes = getConnectedNodesResult.getNodes();
                            Log.d("T", "found nodes");
                            sendMessage(message.get(0), message.get(1));
                        }
                    }
                });
    }

    @Override //we need this to implement GoogleApiClient.ConnectionsCallback
    public void onConnectionSuspended(int i) {}

    private void sendMessage(final String path, final String text ) {
        for (Node node : nodes) {
            Wearable.MessageApi.sendMessage(
                    mWatchApiClient, node.getId(), path, text.getBytes());
            Log.d("T", "Trying to send message " + path+" "+text+" to "+node);

        }
    }

}
