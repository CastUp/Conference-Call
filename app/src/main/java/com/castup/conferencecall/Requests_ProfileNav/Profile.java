package com.castup.conferencecall.Requests_ProfileNav;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.renderscript.Type;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;
import com.castup.conferencecall.Constants.Constants;
import com.castup.conferencecall.Constants.PerfrancesManager;
import com.castup.conferencecall.R;
import com.castup.conferencecall.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Profile extends AppCompatActivity {

    private ActivityProfileBinding profileBinding ;

    private DatabaseReference databaseRef ;
    private FirebaseFirestore firestore ;
    private AlertDialog profileDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(profileBinding.getRoot());

        databaseRef = FirebaseDatabase.getInstance().getReference();
        firestore   = FirebaseFirestore.getInstance();

        Toolbar();
        showUserInfo();
        changeFirstname();
        changeLastname();
        updateEmail();
        updatePassword();

    }
    private void Toolbar() {

        setSupportActionBar(profileBinding.ToolbarProfile);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }
    private void ads(View view){

        //  String TS = "fee50c333ff3825fd6ad6d38cff78154de3025546d47a84f" ;
        String ID = "95b9dd1c47e6407db176bc2398bda2c8323030f814183567" ;

        Appodeal.initialize((Activity)view.getContext(),ID,Appodeal.INTERSTITIAL);
        Appodeal.show((Activity)view.getContext(), Appodeal.INTERSTITIAL);
        Appodeal.isLoaded(Appodeal.INTERSTITIAL);

    }

    private void showUserInfo(){

        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .document(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful() && task.getResult() != null){

                            String firstName = Character.toUpperCase(task.getResult().getString(Constants.KEY_FISRT_NAME).charAt(0))
                                               +task.getResult().getString(Constants.KEY_FISRT_NAME).substring(1).toLowerCase();

                            String lastName = Character.toUpperCase(task.getResult().getString(Constants.KEY_LAST_NAME).charAt(0))
                                    +task.getResult().getString(Constants.KEY_LAST_NAME).substring(1).toLowerCase();

                            String Email = Character.toUpperCase(task.getResult().getString(Constants.KEY_EMAIL).charAt(0))
                                    +task.getResult().getString(Constants.KEY_EMAIL).substring(1).toLowerCase();

                            profileBinding.fristNameProfile.setText(firstName);
                            profileBinding.LastNameProfile.setText(lastName);
                            profileBinding.EmailNameProfile.setText(Email);

                        }
                    }
                });
    }

    private void changeFirstname(){

        profileBinding.changefristNameProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);

                View viewGroupDialog = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_change_profile_info, findViewById(R.id.layoutDialogProfile));

                builder.setView(viewGroupDialog);

                profileDialog = builder.create();

                if(profileDialog.getWindow() != null){

                    profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

                }

                ImageView IconMain  = viewGroupDialog.findViewById(R.id.imageMain);
                TextView  Title     = viewGroupDialog.findViewById(R.id.title_dialog);

                EditText  editfrist = viewGroupDialog.findViewById(R.id.dialogOldPassword);
                EditText  editscand = viewGroupDialog.findViewById(R.id.dialogNewPassword);

                TextView    update    = viewGroupDialog.findViewById(R.id.Update_dialog_profile);
                TextView    cancel    = viewGroupDialog.findViewById(R.id.Cancel_dialog_profile);


                IconMain.setImageResource(R.drawable.ic_change_info);
                Title.setText("Change your first name");

                editfrist.setHint("New first name");
                editfrist.setInputType(InputType.TYPE_CLASS_TEXT);
                editscand.setVisibility(View.GONE);

                update.setText("Update");
                cancel.setText("Cancel");

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(TextUtils.isEmpty(editfrist.getText().toString().trim())){

                            editfrist.setError("It cannot be left blank");
                            editfrist.requestFocus();

                        }else if(editfrist.getText().toString().trim().length() < 4){

                            editfrist.setError("It must be at least 4 characters long Or more");
                            editfrist.requestFocus();

                        }else{

                            HashMap<String , Object> hashMapUpdateInfo = new HashMap<>();
                            hashMapUpdateInfo.put(Constants.KEY_FISRT_NAME,editfrist.getText().toString().trim());

                            firestore.collection(Constants.KEY_COLLECTION_USERS)
                                    .document(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                                    .update(hashMapUpdateInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    profileDialog.dismiss();
                                    String firstname = Character.toUpperCase(editfrist.getText().toString().trim().charAt(0))+editfrist.getText().toString().trim().toLowerCase().substring(1).toLowerCase();
                                    profileBinding.fristNameProfile.setText(firstname);
                                    Snackbar.make(profileBinding.getRoot(),"First name has been successfully updated",Snackbar.LENGTH_SHORT).show();
                                    databaseRef.child("Users").child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                                            .child(Constants.KEY_FISRT_NAME).setValue(editfrist.getText().toString().trim().toLowerCase());
                                }
                            });
                        }

                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        profileDialog.dismiss();
                    }
                });

                profileDialog.show();
                profileDialog.setCancelable(false);
            }

        });
    }

    private void changeLastname(){

        profileBinding.changeLastNameProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);

                View viewGroupDialog = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_change_profile_info, findViewById(R.id.layoutDialogProfile));

                builder.setView(viewGroupDialog);

                profileDialog = builder.create();

                if(profileDialog.getWindow() != null){

                    profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

                }

                ImageView IconMain  = viewGroupDialog.findViewById(R.id.imageMain);
                TextView  Title     = viewGroupDialog.findViewById(R.id.title_dialog);

                EditText  editfrist = viewGroupDialog.findViewById(R.id.dialogOldPassword);
                EditText  editscand = viewGroupDialog.findViewById(R.id.dialogNewPassword);

                TextView    update    = viewGroupDialog.findViewById(R.id.Update_dialog_profile);
                TextView    cancel    = viewGroupDialog.findViewById(R.id.Cancel_dialog_profile);


                IconMain.setImageResource(R.drawable.ic_change_info);
                Title.setText("Change your last name");

                editfrist.setHint("New last name");
                editfrist.setInputType(InputType.TYPE_CLASS_TEXT);
                editscand.setVisibility(View.GONE);

                update.setText("Update");
                cancel.setText("Cancel");

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(TextUtils.isEmpty(editfrist.getText().toString().trim())){

                            editfrist.setError("It cannot be left blank");
                            editfrist.requestFocus();

                        }else if(editfrist.getText().toString().trim().length() < 4){

                            editfrist.setError("It must be at least 4 characters long Or more");
                            editfrist.requestFocus();

                        }else{

                            HashMap<String , Object> hashMapUpdateInfo = new HashMap<>();
                            hashMapUpdateInfo.put(Constants.KEY_LAST_NAME,editfrist.getText().toString().trim());

                            firestore.collection(Constants.KEY_COLLECTION_USERS)
                                    .document(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                                    .update(hashMapUpdateInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    profileDialog.dismiss();
                                    String lastname = Character.toUpperCase(editfrist.getText().toString().trim().charAt(0))+editfrist.getText().toString().trim().toLowerCase().substring(1).toLowerCase();
                                    profileBinding.LastNameProfile.setText(lastname);
                                    Snackbar.make(profileBinding.getRoot(),"First name has been successfully updated",Snackbar.LENGTH_SHORT).show();
                                    databaseRef.child("Users").child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                                            .child(Constants.KEY_LAST_NAME).setValue(editfrist.getText().toString().trim().toLowerCase());
                                }
                            });
                        }


                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        profileDialog.dismiss();
                    }
                });

                profileDialog.show();
                profileDialog.setCancelable(false);
            }

        });

    }

    private void updateEmail(){

        profileBinding.changeEmailNameProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);

                View viewGroupDialog = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_change_profile_info, findViewById(R.id.layoutDialogProfile));

                builder.setView(viewGroupDialog);

                profileDialog = builder.create();

                if(profileDialog.getWindow() != null){

                    profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

                }

                ImageView IconMain  = viewGroupDialog.findViewById(R.id.imageMain);
                TextView  Title     = viewGroupDialog.findViewById(R.id.title_dialog);

                EditText  editfrist = viewGroupDialog.findViewById(R.id.dialogOldPassword);
                EditText  editscand = viewGroupDialog.findViewById(R.id.dialogNewPassword);

                TextView    update    = viewGroupDialog.findViewById(R.id.Update_dialog_profile);
                TextView    cancel    = viewGroupDialog.findViewById(R.id.Cancel_dialog_profile);


                IconMain.setImageResource(R.drawable.ic_change_email);
                Title.setText("Change your email");

                editfrist.setHint("New email");
                editfrist.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                editscand.setHint("Your password");

                update.setText("Update");
                cancel.setText("Cancel");

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(TextUtils.isEmpty(editfrist.getText().toString().trim())){

                            editfrist.setError("It cannot be left blank");
                            editfrist.requestFocus();

                        }else if(!Patterns.EMAIL_ADDRESS.matcher(editfrist.getText().toString().trim()).matches()){

                            editfrist.setError("Enter valid email");
                            editfrist.requestFocus();

                        }else if(TextUtils.isEmpty(editscand.getText().toString().trim())){

                            editscand.setError("It cannot be left blank");
                            editscand.requestFocus();
                        }else {

                            changeEmailDatabase(editscand.getText().toString().trim(),editfrist.getText().toString().trim(),editscand);
                        }

                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        profileDialog.dismiss();
                    }
                });

                profileDialog.show();
                profileDialog.setCancelable(false);
            }
        });
    }

    private void updatePassword(){

        profileBinding.changePssword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ads(v);
                AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);

                View viewGroupDialog = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_change_profile_info, findViewById(R.id.layoutDialogProfile));

                builder.setView(viewGroupDialog);

                profileDialog = builder.create();

                if(profileDialog.getWindow() != null){

                    profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

                }

                ImageView IconMain  = viewGroupDialog.findViewById(R.id.imageMain);
                TextView  Title     = viewGroupDialog.findViewById(R.id.title_dialog);

                EditText  editfrist = viewGroupDialog.findViewById(R.id.dialogOldPassword);
                EditText  editscand = viewGroupDialog.findViewById(R.id.dialogNewPassword);

                TextView    update    = viewGroupDialog.findViewById(R.id.Update_dialog_profile);
                TextView    cancel    = viewGroupDialog.findViewById(R.id.Cancel_dialog_profile);


                IconMain.setImageResource(R.drawable.ic_changepassword);
                Title.setText("Change your password");

                editfrist.setHint("New password");
                editscand.setHint("Old password");

                update.setText("Update");
                cancel.setText("Cancel");

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(TextUtils.isEmpty(editfrist.getText().toString().trim())){

                            editfrist.setError("It cannot be left blank");
                            editfrist.requestFocus();

                        }else if(editfrist.getText().toString().trim().length() < 8){

                            editfrist.setError("It must be at least 8 characters long Or more");
                            editfrist.requestFocus();

                        }else if(TextUtils.isEmpty(editscand.getText().toString().trim())){

                            editscand.setError("It cannot be left blank");
                            editscand.requestFocus();
                        }else {

                            changePassWordDatabase(editscand.getText().toString().trim(),editfrist.getText().toString().trim() , editscand);
                        }
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        profileDialog.dismiss();
                    }
                });

                profileDialog.show();
                profileDialog.setCancelable(false);
            }

        });
    }

    private void changeEmailDatabase(String yourpassword , String newEmail , EditText editscand){

        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .document(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful() && task.getResult() != null){

                            if(task.getResult().getString(Constants.KEY_PASSWORD).equals(yourpassword)){

                                HashMap<String , Object> hashMapPassword = new HashMap<>();
                                hashMapPassword.put(Constants.KEY_EMAIL , newEmail);

                                firestore.collection(Constants.KEY_COLLECTION_USERS)
                                        .document(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                                        .update(hashMapPassword)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                profileDialog.dismiss();
                                                String Email = Character.toUpperCase(newEmail.charAt(0))+newEmail.toLowerCase().substring(1).toLowerCase();
                                                profileBinding.EmailNameProfile.setText(Email);
                                                Snackbar.make(profileBinding.getRoot(),"Email has been successfully updated",Snackbar.LENGTH_SHORT).show();
                                                databaseRef.child("Users").child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                                                        .child(Constants.KEY_EMAIL).setValue(newEmail.toLowerCase());
                                            }
                                        });

                            }else {

                                editscand.setError("Your password error");
                                editscand.requestFocus();
                            }

                        }
                    }
                });
    }

    private void changePassWordDatabase(String oldPassword , String newPassword , EditText editscand){

        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .document(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful() && task.getResult() != null){

                            if(task.getResult().getString(Constants.KEY_PASSWORD).equals(oldPassword)){

                                HashMap<String , Object> hashMapPassword = new HashMap<>();
                                hashMapPassword.put(Constants.KEY_PASSWORD , newPassword);

                                firestore.collection(Constants.KEY_COLLECTION_USERS)
                                        .document(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                                        .update(hashMapPassword)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                profileDialog.dismiss();
                                                Snackbar.make(profileBinding.getRoot(),"password has been successfully updated",Snackbar.LENGTH_SHORT).show();
                                            }
                                        });

                            }else {

                                editscand.setError("Old password error");
                                editscand.requestFocus();
                            }

                        }
                    }
                });
    }







}