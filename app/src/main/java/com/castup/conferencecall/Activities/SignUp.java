package com.castup.conferencecall.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.state.State;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.castup.conferencecall.Constants.Constants;
import com.castup.conferencecall.Constants.PerfrancesManager;
import com.castup.conferencecall.R;
import com.castup.conferencecall.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    private ActivitySignUpBinding signUpBinding ;
    private FirebaseFirestore firestore ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(signUpBinding.getRoot());

        backToSignIn();
        firestore = FirebaseFirestore.getInstance();
       signUp();
    }
    private void backToSignIn(){

        setSupportActionBar(signUpBinding.ToolbarSignUp);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        signUpBinding.SingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getBaseContext(),SignIn.class));
            }
        });

    }
    private void signUp(){

        signUpBinding.SignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (signUpBinding.inputFirstName.getText().toString().trim().isEmpty()) {

                    signUpBinding.inputFirstName.setError("Enter frist name");
                    signUpBinding.inputFirstName.requestFocus();

                } else if (signUpBinding.inputLastName.getText().toString().trim().isEmpty()) {

                    signUpBinding.inputLastName.setError("Enter last name");
                    signUpBinding.inputLastName.requestFocus();

                } else if (TextUtils.isEmpty(signUpBinding.inputEmail.getText().toString().trim())) {

                    signUpBinding.inputEmail.setError("Enter email");
                    signUpBinding.inputEmail.requestFocus();

                } else if (!Patterns.EMAIL_ADDRESS.matcher(signUpBinding.inputEmail.getText().toString().trim()).matches()) {

                    signUpBinding.inputEmail.setError("Enter vaild email");
                    signUpBinding.inputEmail.requestFocus();

                } else if (TextUtils.isEmpty(signUpBinding.inputPassword.getText().toString().trim())) {

                    signUpBinding.inputPassword.setError("Enter password");
                    signUpBinding.inputPassword.requestFocus();

                } else if (signUpBinding.inputPassword.getText().toString().trim().length() < 8) {

                    signUpBinding.inputPassword.setError("It must be at least 8 characters long or more");
                    signUpBinding.inputPassword.requestFocus();

                } else if (TextUtils.isEmpty(signUpBinding.inputConfirmPassword.getText().toString().trim())) {

                    signUpBinding.inputConfirmPassword.setError("Enter confirm password");
                    signUpBinding.inputConfirmPassword.requestFocus();

                } else if (!TextUtils.equals(signUpBinding.inputPassword.getText().toString().trim(), signUpBinding.inputConfirmPassword.getText().toString().trim())) {

                    signUpBinding.inputConfirmPassword.setError("password & confirm password must be same");
                    signUpBinding.inputConfirmPassword.requestFocus();

                } else {

                    signUpBinding.progressBarSingUp.setVisibility(View.VISIBLE);
                    signUpBinding.SignUpBtn.setVisibility(View.GONE);
                    chickEmailUser();
                }

            }
        });
    }
    private void chickEmailUser(){

        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        String UserEmail = signUpBinding.inputEmail.getText().toString().toLowerCase().trim();

                        if(task.isSuccessful() && task.getResult() != null){

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()){

                                if(UserEmail.equals(documentSnapshot.getString(Constants.KEY_EMAIL))){

                                    signUpBinding.inputEmail.setError("Mail is already used");
                                    signUpBinding.inputEmail.requestFocus();
                                    signUpBinding.progressBarSingUp.setVisibility(View.GONE);
                                    signUpBinding.SignUpBtn.setVisibility(View.VISIBLE);
                                    return;
                                }
                            }

                            signUpToFirestore();
                            return;
                        }

                    }
                });
    }
    private void signUpToFirestore(){

        HashMap<String , String> map = new HashMap<>();

        map.put(Constants.KEY_FISRT_NAME,signUpBinding.inputFirstName.getText().toString().toLowerCase().trim());
        map.put(Constants.KEY_LAST_NAME,signUpBinding.inputLastName.getText().toString().toLowerCase().trim());
        map.put(Constants.KEY_EMAIL,signUpBinding.inputEmail.getText().toString().toLowerCase().trim());
        map.put(Constants.KEY_PROFILE_PHOTO,"");
        map.put(Constants.KEY_PASSWORD,signUpBinding.inputPassword.getText().toString().trim());

        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

                PerfrancesManager.getInstance(getBaseContext()).setRegistrationStatus(Constants.KEY_USER_STATUS,true);
                PerfrancesManager.getInstance(getBaseContext()).setUserInformation(Constants.KEY_USER_ID,documentReference.getId());
                PerfrancesManager.getInstance(getBaseContext()).setUserInformation(Constants.KEY_FISRT_NAME,signUpBinding.inputFirstName.getText().toString().toLowerCase().trim());
                PerfrancesManager.getInstance(getBaseContext()).setUserInformation(Constants.KEY_LAST_NAME,signUpBinding.inputLastName.getText().toString().toLowerCase().trim());
                PerfrancesManager.getInstance(getBaseContext()).setUserInformation(Constants.KEY_EMAIL,signUpBinding.inputEmail.getText().toString().toLowerCase().trim());
                PerfrancesManager.getInstance(getBaseContext()).setUserInformation(Constants.KEY_PROFILE_PHOTO,"");

                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                       startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                signUpBinding.SignUpBtn.setVisibility(View.VISIBLE);
                signUpBinding.progressBarSingUp.setVisibility(View.GONE);

                Toast.makeText(getBaseContext(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}