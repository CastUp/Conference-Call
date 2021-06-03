package com.castup.conferencecall.Constants;

import android.content.Context;
import android.content.SharedPreferences;

public class PerfrancesManager {

    private static SharedPreferences sharedPreferences ;

    private static PerfrancesManager Instance ;

    public static PerfrancesManager getInstance(Context context){

        if(Instance == null){

            sharedPreferences = context.getSharedPreferences("dataUser",Context.MODE_PRIVATE);

            Instance = new PerfrancesManager();
        }

        return Instance ;
    }

    public void setRegistrationStatus(String key ,Boolean status){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key , status);
        editor.apply();
    }

    public void setUserInformation(String key , String info){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key , info);
        editor.apply();
    }

    public Boolean getRegistrationStatus(String key){

        return sharedPreferences.getBoolean(key ,false);
    }

    public String getUserInformation(String key){

        return sharedPreferences.getString(key , null );
    }

    public void ClearFileShared(){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
