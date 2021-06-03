package com.castup.conferencecall.Firebase_Apis;

import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.castup.conferencecall.Activities.IncomingInitiation;
import com.castup.conferencecall.Constants.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(remoteMessage != null){

            String typeIvitation = remoteMessage.getData().get(Constants.REMOTE_MSG_TYPE);

            if(typeIvitation.equals(Constants.REMOTE_MSG_INVITATION)){

                Intent intent = new Intent(getApplicationContext(), IncomingInitiation.class);

                intent.putExtra(Constants.REMOTE_MSG_MEETING_TYPE, remoteMessage.getData().get(Constants.REMOTE_MSG_MEETING_TYPE));
                intent.putExtra(Constants.KEY_FISRT_NAME,remoteMessage.getData().get(Constants.KEY_FISRT_NAME));
                intent.putExtra(Constants.KEY_LAST_NAME,remoteMessage.getData().get(Constants.KEY_LAST_NAME));
                intent.putExtra(Constants.KEY_EMAIL,remoteMessage.getData().get(Constants.KEY_EMAIL));
                intent.putExtra(Constants.KEY_PROFILE_PHOTO,remoteMessage.getData().get(Constants.KEY_PROFILE_PHOTO));
                intent.putExtra(Constants.REMOTE_MSG_INVITER_TOKEN,remoteMessage.getData().get(Constants.REMOTE_MSG_INVITER_TOKEN));
                intent.putExtra(Constants.REMOTE_MSG_MEETING_ROOM,remoteMessage.getData().get(Constants.REMOTE_MSG_MEETING_ROOM));

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }else if(typeIvitation.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)){

                Intent intent  = new Intent(Constants.REMOTE_MSG_INVITATION_RESPONSE);

                intent.putExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE,remoteMessage.getData().get(Constants.REMOTE_MSG_INVITATION_RESPONSE));

               LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

            }
        }
    }

}
