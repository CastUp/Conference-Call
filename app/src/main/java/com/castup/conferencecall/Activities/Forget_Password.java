package com.castup.conferencecall.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;
import com.castup.conferencecall.Constants.Constants;
import com.castup.conferencecall.Emails.GMailSender;
import com.castup.conferencecall.databinding.ActivityForgetPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class Forget_Password extends AppCompatActivity {

    private ActivityForgetPasswordBinding forgetPasswordBinding;
    private FirebaseFirestore firestore;
    private GMailSender sender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        forgetPasswordBinding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        setContentView(forgetPasswordBinding.getRoot());

        firestore = FirebaseFirestore.getInstance();

        backSignIn();
        chickEmail();


    }

    private void backSignIn() {

        forgetPasswordBinding.BackSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void chickEmail() {

        forgetPasswordBinding.FindAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (TextUtils.isEmpty(forgetPasswordBinding.SearchEmail.getText().toString().trim())) {

                    forgetPasswordBinding.SearchEmail.setError("Enter email");
                    forgetPasswordBinding.SearchEmail.requestFocus();

                } else if (!Patterns.EMAIL_ADDRESS.matcher(forgetPasswordBinding.SearchEmail.getText().toString().trim()).matches()) {

                    forgetPasswordBinding.SearchEmail.setError("Enter valid email");
                    forgetPasswordBinding.SearchEmail.requestFocus();

                } else {

                    forgetPasswordBinding.FindAccount.setVisibility(View.GONE);
                    forgetPasswordBinding.progressFindAccount.setVisibility(View.VISIBLE);
                    searchAcount();
                }
            }
        });
    }

    private void searchAcount() {

        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {

                            String Email = forgetPasswordBinding.SearchEmail.getText().toString().toLowerCase().trim();

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                if (Email.equals(documentSnapshot.getString(Constants.KEY_EMAIL))) {

                                    String password = documentSnapshot.getString(Constants.KEY_PASSWORD);
                                    String email = documentSnapshot.getString(Constants.KEY_EMAIL);
                                    String name = Character.toUpperCase(documentSnapshot.getString(Constants.KEY_FISRT_NAME).charAt(0)) + documentSnapshot.getString(Constants.KEY_FISRT_NAME).substring(1).toLowerCase()+
                                                  " "+Character.toUpperCase(documentSnapshot.getString(Constants.KEY_LAST_NAME).charAt(0)) + documentSnapshot.getString(Constants.KEY_LAST_NAME).substring(1).toLowerCase();

                                    Toast.makeText(getBaseContext(), "Password has been sent to your email", Toast.LENGTH_LONG).show();
                                    sendPassWordTOEmail(password, email, name);
                                    startActivity(new Intent(getApplicationContext(),SignIn.class));
                                    return;

                                }
                            }
                            forgetPasswordBinding.FindAccount.setVisibility(View.VISIBLE);
                            forgetPasswordBinding.progressFindAccount.setVisibility(View.GONE);
                            Toast.makeText(getBaseContext(), "There is no data for this mail", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
    }

    private void sendPassWordTOEmail(String password, String email, String name) {

        sender = new GMailSender("castupapp@gmail.com","Egy@#1986");

        String subject = "noreply@conference-call-1b7e6.CastUp.com";
        String body = "Hello "+name+"\n\n\n"+"This is a message with important information that only those who own this mail will see it"
                      +"\n\n"+"Email: "+email+"\n"+"Password: "+password+"\n\n\n"+"If you didn’t ask to verify this address, you can ignore this email."
                       +"\n\n\n"+"Thanks...";

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    // Add subject, Body, your mail Id, and receiver mail Id.
                    //https://myaccount.google.com/u/2/lesssecureapps?pli=1&rapt=AEjHL4MvUJJXbsewRWPtYsjs1Evm9M1R8T_ffnAAsm_Z7eFlIBv6tZxRsrICNjChyFz8A8dPEavyccdbYPOH7g81Bsmf5JjcxA
                    sender.sendMail(subject,body,"castupapp@gmail.com",email);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

}