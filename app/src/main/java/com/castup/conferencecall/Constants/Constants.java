package com.castup.conferencecall.Constants;

import java.util.HashMap;

public class Constants {

    public static final String KEY_COLLECTION_USERS = "Users" ;
    public static final String KEY_USER_STATUS      = "Registration";
    public static final String KEY_USER_ID          = "ID";
    public static final String KEY_FCM_TOKEN        = "Token" ;
    public static final String KEY_PROFILE_PHOTO    = "Photo";
    public static final String KEY_FISRT_NAME       = "FirstName";
    public static final String KEY_LAST_NAME        = "LastName";
    public static final String KEY_EMAIL            = "Email";
    public static final String KEY_PASSWORD         = "PassWord";



    public static final String REMOTE_MSG_TYPE = "type";
    public static final String REMOTE_MSG_MEETING_TYPE = "meetingType";
    public static final String REMOTE_MSG_INVITATION = "invitation";
    public static final String REMOTE_MSG_INVITATION_RESPONSE = "invitationResponse";
    public static final String REMOTE_MSG_INVITATION_CANCELLED = "cancelled";
    public static final String REMOTE_MSG_INVITATION_ACCEPTED = "accepted";
    public static final String REMOTE_MSG_INVITATION_REJECTED = "rejected";



    public static final String REMOTE_MSG_DATA ="data";
    public static final String REMOTE_MSG_REGISTRATION_IDS ="registration_ids";
    public static final String REMOTE_MSG_INVITER_TOKEN = "inviterToken";
    public static final String REMOTE_MSG_MEETING_ROOM = "meetingRoom";

    //============================================================================

    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";


    public static HashMap<String , String> getMessagesHeader(){

        HashMap<String , String> headerMap = new HashMap<>();

        headerMap.put(REMOTE_MSG_AUTHORIZATION,"key=AAAA6r4aZnM:APA91bHSJ30JJ17d4qTWF_rGebmZVZxZCvEcRo9KIkojuCTw38gTwoUdcujpnjStCPz9ntl324Gfr2VGbo9Gc0HyoP1BOV4oQ63tnsDlurVy0uOS_z6pj2pEt-dxzDYvLaf6w7PlofrY");
        headerMap.put(REMOTE_MSG_CONTENT_TYPE,"application/json");

        return headerMap ;
    }



}
