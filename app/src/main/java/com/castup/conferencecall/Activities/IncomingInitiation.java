package com.castup.conferencecall.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.castup.conferencecall.Constants.Constants;
import com.castup.conferencecall.Firebase_Apis.ApiClient;
import com.castup.conferencecall.Firebase_Apis.ApiServer;
import com.castup.conferencecall.R;
import com.castup.conferencecall.databinding.ActivityIncomingInitiationBinding;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncomingInitiation extends AppCompatActivity {

    private ActivityIncomingInitiationBinding incomingInitiationBinding;
    private BroadcastReceiver broadcasInvitation;
    public static final String CHANNEL_ID = "cancel_call";
    private MediaPlayer player  ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        incomingInitiationBinding = ActivityIncomingInitiationBinding.inflate(getLayoutInflater());
        setContentView(incomingInitiationBinding.getRoot());
        player = MediaPlayer.create(getApplicationContext(),R.raw.ring);
        if(!player.isPlaying())player.start();

        showInfoSender();
        ActionsIncomeInvitation();
        broadcastChackInvitation();


    }

    private void showInfoSender() {

        if (getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE).equals("vedio")) {

            incomingInitiationBinding.MeetingType.setImageResource(R.drawable.ic_video);

        } else {

            incomingInitiationBinding.MeetingType.setImageResource(R.drawable.ic_audio);

        }

        if (getIntent().getStringExtra(Constants.KEY_PROFILE_PHOTO).equals("")) {

            char nameChar = Character.toUpperCase(getIntent().getStringExtra(Constants.KEY_FISRT_NAME).charAt(0));

            incomingInitiationBinding.imageFirstChar.setText(String.valueOf(nameChar));
            incomingInitiationBinding.imageFirstChar.setVisibility(View.VISIBLE);
            incomingInitiationBinding.imageUser.setVisibility(View.GONE);

        } else {

            Picasso.get().load(getIntent().getStringExtra(Constants.KEY_PROFILE_PHOTO)).into(incomingInitiationBinding.imageUser);

            incomingInitiationBinding.imageUser.setVisibility(View.VISIBLE);
            incomingInitiationBinding.imageFirstChar.setVisibility(View.GONE);

        }

        String name = Character.toUpperCase(getIntent().getStringExtra(Constants.KEY_FISRT_NAME).charAt(0))
                + getIntent().getStringExtra(Constants.KEY_FISRT_NAME).substring(1).toLowerCase() + " "
                + getIntent().getStringExtra(Constants.KEY_LAST_NAME).toLowerCase();

        String email = Character.toUpperCase(getIntent().getStringExtra(Constants.KEY_EMAIL).charAt(0))
                + getIntent().getStringExtra(Constants.KEY_EMAIL).substring(1).toLowerCase();

        incomingInitiationBinding.UserName.setText(name);
        incomingInitiationBinding.Email.setText(email);

    }

    private void ActionsIncomeInvitation(){

        incomingInitiationBinding.imageAcceptInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                  invitationResponse(Constants.REMOTE_MSG_INVITATION_ACCEPTED,getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN));


            }
        });

        incomingInitiationBinding.imageRejectInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                invitationResponse(Constants.REMOTE_MSG_INVITATION_REJECTED,getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN));

            }
        });

    }

    private void invitationResponse(String actionType , String tokenReceiver){


        try {

            JSONArray token = new JSONArray();
            token.put(tokenReceiver);

            JSONObject data = new JSONObject();
            data.put(Constants.REMOTE_MSG_TYPE , Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, actionType);

            JSONObject bady = new JSONObject();

            bady.put(Constants.REMOTE_MSG_DATA, data);
            bady.put(Constants.REMOTE_MSG_REGISTRATION_IDS,token);

            sendMessageToServer(bady.toString(),actionType);


        }catch (Exception e){

            Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void sendMessageToServer(String messageBady , String actionType){

        ApiClient.getClient().create(ApiServer.class).sentRemoteMessage(Constants.getMessagesHeader(),messageBady)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if(response.isSuccessful()){

                            if(actionType.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)){

                                try {


                                    URL serverURL = new URL("https://meet.jit.si");

                                    JitsiMeetConferenceOptions.Builder conference = new JitsiMeetConferenceOptions.Builder();

                                    conference.setServerURL(serverURL);
                                    conference.setWelcomePageEnabled(false);
                                    conference.setRoom(getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM));

                                    if(getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE).equals("audio")){

                                        conference.setVideoMuted(true);
                                    }
                                    JitsiMeetActivity.launch(IncomingInitiation.this,conference.build());
                                    if(player.isPlaying())player.stop();
                                    finish();

                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                    if(player.isPlaying())player.stop();
                                }

                            }else {

                                Toast.makeText(getApplicationContext(),"Invitation Rejected",Toast.LENGTH_SHORT).show();
                                if(player.isPlaying())player.stop();
                                finish();
                            }

                        }else {

                            Toast.makeText(getApplicationContext(),response.message(),Toast.LENGTH_SHORT).show();
                            if(player.isPlaying())player.stop();
                            finish();
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
                        if(player.isPlaying())player.stop();
                        finish();
                    }
                });

    }

    private void setNotifications(){

        String name = getIntent().getStringExtra(Constants.KEY_FISRT_NAME)+" "+ getIntent().getStringExtra(Constants.KEY_LAST_NAME);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Missed connection attempt from your friend "+name);

            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(channel);

        }


        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(),CHANNEL_ID);

        builder.setSmallIcon(R.drawable.ic_main_icon);
        builder.setContentTitle(getResources().getString(R.string.app_name));
        builder.setContentIntent(pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText("Missed connection attempt from your friend "+name));

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getBaseContext());
        managerCompat.notify(1,builder.build());
    }

    private void broadcastChackInvitation() {

        broadcasInvitation = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String typeResponse = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);

                if (typeResponse != null) {

                    if (typeResponse.equals(Constants.REMOTE_MSG_INVITATION_CANCELLED)) {

                        Toast.makeText(getBaseContext(), "Invitation Cancelled", Toast.LENGTH_SHORT).show();
                        if(player.isPlaying())player.stop();
                        setNotifications();
                        finish();
                    }
                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcasInvitation , new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE));
    }

    @Override
    protected void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcasInvitation);
    }
}