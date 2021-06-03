package com.castup.conferencecall.Firebase_Apis;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface ApiServer {

    @POST("send")
    Call<String> sentRemoteMessage(@HeaderMap HashMap<String , String> headers , @Body String bady);

}
