package com.castup.conferencecall.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.castup.conferencecall.Constants.Constants;
import com.castup.conferencecall.Constants.PerfrancesManager;
import com.castup.conferencecall.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class SignIn extends AppCompatActivity {

    private ActivitySignInBinding signInBinding;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signInBinding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(signInBinding.getRoot());
        chickRegistration();

        opensignUp();
        firestore = FirebaseFirestore.getInstance();
        signIn();
        forgetPassWord();
    }

    private void opensignUp() {

        signInBinding.SingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), SignUp.class));
            }
        });

    }

    private void chickRegistration(){
        if(PerfrancesManager.getInstance(getBaseContext()).getRegistrationStatus(Constants.KEY_USER_STATUS)){
           startActivity(new Intent(getBaseContext(),MainActivity.class));
           finish();
        }
    }

    private void signIn(){

        signInBinding.SignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(signInBinding.inputEmail.getText().toString().trim())) {

                    signInBinding.inputEmail.setError("Enter email");
                    signInBinding.inputEmail.requestFocus();

                } else if (!Patterns.EMAIL_ADDRESS.matcher(signInBinding.inputEmail.getText().toString().trim()).matches()) {

                    signInBinding.inputEmail.setError("Enter valid email");
                    signInBinding.inputEmail.requestFocus();

                } else if (TextUtils.isEmpty(signInBinding.inputPassword.getText().toString().trim())) {

                    signInBinding.inputPassword.setError("Enter password");
                    signInBinding.inputPassword.requestFocus();

                } else {

                    signInBinding.progressBarSingIn.setVisibility(View.VISIBLE);
                    signInBinding.SignInBtn.setVisibility(View.GONE);
                    singInWithFirestore();
                }

            }
        });
    }

    private void singInWithFirestore() {

        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, signInBinding.inputEmail.getText().toString().toLowerCase().trim())
                .whereEqualTo(Constants.KEY_PASSWORD, signInBinding.inputPassword.getText().toString().trim())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {

                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                            PerfrancesManager.getInstance(getBaseContext()).setRegistrationStatus(Constants.KEY_USER_STATUS, true);
                            PerfrancesManager.getInstance(getBaseContext()).setUserInformation(Constants.KEY_USER_ID, documentSnapshot.getId());
                            PerfrancesManager.getInstance(getBaseContext()).setUserInformation(Constants.KEY_FISRT_NAME, documentSnapshot.getString(Constants.KEY_FISRT_NAME));
                            PerfrancesManager.getInstance(getBaseContext()).setUserInformation(Constants.KEY_LAST_NAME, documentSnapshot.getString(Constants.KEY_LAST_NAME));
                            PerfrancesManager.getInstance(getBaseContext()).setUserInformation(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
                            PerfrancesManager.getInstance(getBaseContext()).setUserInformation(Constants.KEY_PROFILE_PHOTO,documentSnapshot.getString(Constants.KEY_PROFILE_PHOTO));

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {

                            signInBinding.progressBarSingIn.setVisibility(View.GONE);
                            signInBinding.SignInBtn.setVisibility(View.VISIBLE);
                            Toast.makeText(getBaseContext(), "Unable to sign in", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void forgetPassWord(){

        signInBinding.forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),Forget_Password.class));
            }
        });
    }
}