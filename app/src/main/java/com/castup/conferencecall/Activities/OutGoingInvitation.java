package com.castup.conferencecall.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.castup.conferencecall.Constants.Constants;
import com.castup.conferencecall.Constants.PerfrancesManager;
import com.castup.conferencecall.Firebase_Apis.ApiClient;
import com.castup.conferencecall.Firebase_Apis.ApiServer;
import com.castup.conferencecall.R;
import com.castup.conferencecall.databinding.ActivityOutGoingInvitationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutGoingInvitation extends AppCompatActivity {

    private ActivityOutGoingInvitationBinding  outGoingInvitationBinding ;
    private String meetingRoom = null ;
    private BroadcastReceiver broadcastReceiver ;
    private MediaPlayer player  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        outGoingInvitationBinding = ActivityOutGoingInvitationBinding.inflate(getLayoutInflater());
        setContentView(outGoingInvitationBinding.getRoot());
        player = MediaPlayer.create(getApplicationContext(),R.raw.ring);

        showInfoReceiver();

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {

                if(task.isSuccessful()){

                   sendInvitation(getIntent().getStringExtra("type"),getIntent().getStringExtra(Constants.KEY_FCM_TOKEN),task.getResult().getToken());
                }
            }
        });

        cancelInvitation(getIntent().getStringExtra(Constants.KEY_FCM_TOKEN));
        stopRing(getIntent().getStringExtra(Constants.KEY_FCM_TOKEN));
        setBroadcastReceiver();


    }

    private void showInfoReceiver(){

        if(getIntent().getStringExtra("type").equals("vedio")){

            outGoingInvitationBinding.MeetingType.setImageResource(R.drawable.ic_video);

        }else {

            outGoingInvitationBinding.MeetingType.setImageResource(R.drawable.ic_audio);
        }

        if(getIntent().getStringExtra(Constants.KEY_PROFILE_PHOTO).equals("")){

            char CharName = Character.toUpperCase(getIntent().getStringExtra(Constants.KEY_FISRT_NAME).charAt(0));

            outGoingInvitationBinding.imageFirstChar.setText(String.valueOf(CharName));
            outGoingInvitationBinding.imageFirstChar.setVisibility(View.VISIBLE);
            outGoingInvitationBinding.imageUser.setVisibility(View.GONE);

        }else{

            Picasso.get().load(getIntent().getStringExtra(Constants.KEY_PROFILE_PHOTO)).into(outGoingInvitationBinding.imageUser);
            outGoingInvitationBinding.imageUser.setVisibility(View.VISIBLE);
            outGoingInvitationBinding.imageFirstChar.setVisibility(View.GONE);
        }

        String friendName = Character.toUpperCase(getIntent().getStringExtra(Constants.KEY_FISRT_NAME).charAt(0))
                            + getIntent().getStringExtra(Constants.KEY_FISRT_NAME).substring(1).toLowerCase()+ " "+
                              getIntent().getStringExtra(Constants.KEY_LAST_NAME).toLowerCase();

        String Email  = Character.toUpperCase(getIntent().getStringExtra(Constants.KEY_EMAIL).charAt(0))
                        + getIntent().getStringExtra(Constants.KEY_EMAIL).substring(1).toLowerCase();

        outGoingInvitationBinding.friendName.setText(friendName);
        outGoingInvitationBinding.textEmail.setText(Email);

    }

    private void cancelInvitation(String recieverToken){

        outGoingInvitationBinding.StopInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    JSONArray token = new JSONArray();

                    if(recieverToken != null){

                        token.put(recieverToken);
                    }

                    JSONObject  data = new JSONObject();

                    data.put(Constants.REMOTE_MSG_TYPE,Constants.REMOTE_MSG_INVITATION_RESPONSE);
                    data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE,Constants.REMOTE_MSG_INVITATION_CANCELLED);

                    JSONObject bady = new JSONObject();

                    bady.put(Constants.REMOTE_MSG_DATA, data);
                    bady.put(Constants.REMOTE_MSG_REGISTRATION_IDS, token);

                    sendMessageToServer(bady.toString(),Constants.REMOTE_MSG_INVITATION_RESPONSE);

                }catch (Exception e){

                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });

    }

    private void sendInvitation(String meetingType , String recieverToken , String inviterToken ){

        try {

            JSONArray token = new JSONArray();
            if(recieverToken != null){
                token.put(recieverToken);
            }

            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION);
            data.put(Constants.REMOTE_MSG_MEETING_TYPE,meetingType);
            data.put(Constants.KEY_FISRT_NAME, PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_FISRT_NAME));
            data.put(Constants.KEY_LAST_NAME,PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_LAST_NAME));
            data.put(Constants.KEY_EMAIL,PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_EMAIL));
            data.put(Constants.KEY_PROFILE_PHOTO,PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_PROFILE_PHOTO));
            data.put(Constants.REMOTE_MSG_INVITER_TOKEN,inviterToken);
            meetingRoom = PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID)+" "+ UUID.randomUUID().toString().substring(0,5);
            data.put(Constants.REMOTE_MSG_MEETING_ROOM,meetingRoom);

            JSONObject body = new JSONObject();
            body.put(Constants.REMOTE_MSG_DATA,data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS,token);

            sendMessageToServer(body.toString(),Constants.REMOTE_MSG_INVITATION);


        }catch (Exception e){

            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    private void sendMessageToServer(String messageBady , String typeInvitation){

        ApiClient.getClient().create(ApiServer.class).sentRemoteMessage(Constants.getMessagesHeader(),messageBady)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if(response.isSuccessful()){

                            if(typeInvitation.equals(Constants.REMOTE_MSG_INVITATION)){

                                Toast.makeText(getApplicationContext(),"invitation sent successfully",Toast.LENGTH_SHORT).show();
                                if(!player.isPlaying()){player.start();}
                            }else if(typeInvitation.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)){

                                Toast.makeText(getApplicationContext(),"invitation cancelled",Toast.LENGTH_SHORT).show();
                                if(player.isPlaying()){player.stop();}
                                finish();
                            }

                        }else {

                            Toast.makeText(getApplicationContext(),response.message(),Toast.LENGTH_SHORT).show();
                            if(player.isPlaying()){player.stop();}
                            finish();
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
                        if(player.isPlaying()){player.stop();}
                    }
                });
    }

    private void setBroadcastReceiver (){

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);

                if(action != null){

                    if(action.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)){

                        try {

                            URL serverURL = new URL("https://meet.jit.si");

                            JitsiMeetConferenceOptions.Builder conference = new JitsiMeetConferenceOptions.Builder();

                            conference.setServerURL(serverURL);
                            conference.setWelcomePageEnabled(false);
                            conference.setRoom(meetingRoom);

                            if(getIntent().getStringExtra("type").equals("audio")){

                                conference.setVideoMuted(true);
                            }
                            JitsiMeetActivity.launch(OutGoingInvitation.this,conference.build());
                            if(player.isPlaying()){player.stop();}
                            finish();

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                            if(player.isPlaying()){player.stop();}
                        }

                    }else {

                        Toast.makeText(getApplicationContext(),"Invitation Rejected",Toast.LENGTH_SHORT).show();
                        if(player.isPlaying()){player.stop();}
                        finish();
                    }
                }

            }
        };
    }

    private void stopRing(String recieverToken){

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                try {

                    JSONArray token = new JSONArray();

                    if(recieverToken != null){

                        token.put(recieverToken);
                    }

                    JSONObject  data = new JSONObject();

                    data.put(Constants.REMOTE_MSG_TYPE,Constants.REMOTE_MSG_INVITATION_RESPONSE);
                    data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE,Constants.REMOTE_MSG_INVITATION_CANCELLED);

                    JSONObject bady = new JSONObject();

                    bady.put(Constants.REMOTE_MSG_DATA, data);
                    bady.put(Constants.REMOTE_MSG_REGISTRATION_IDS, token);

                    sendMessageToServer(bady.toString(),Constants.REMOTE_MSG_INVITATION_RESPONSE);

                }catch (Exception e){

                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver,new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE));
    }

    @Override
    protected void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
    }
}