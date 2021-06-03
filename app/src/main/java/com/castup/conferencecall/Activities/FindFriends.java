package com.castup.conferencecall.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;
import com.castup.conferencecall.Adapters.AllUsersAdapter;
import com.castup.conferencecall.Constants.Constants;
import com.castup.conferencecall.Constants.PerfrancesManager;
import com.castup.conferencecall.Listeners.OnClickLictenerAcceptRequest;
import com.castup.conferencecall.Listeners.OnClickLictenerCancelRequest;
import com.castup.conferencecall.Listeners.OnClickLictenerRemoveFriend;
import com.castup.conferencecall.Listeners.OnClickListenersSentRequest;
import com.castup.conferencecall.Model.ModelAdapterFireStore;
import com.castup.conferencecall.R;
import com.castup.conferencecall.databinding.ActivityFindFriendsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class FindFriends extends AppCompatActivity {

    private ActivityFindFriendsBinding findFriendsBinding;
    private FirebaseFirestore firestore;
    private DatabaseReference databaseRef;
    private AlertDialog dialogRemoved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findFriendsBinding = ActivityFindFriendsBinding.inflate(getLayoutInflater());
        setContentView(findFriendsBinding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        Toolbar();
        SearchFriends();
        showRecycleUsers();
    }

    private void Toolbar() {

        setSupportActionBar(findFriendsBinding.ToolbarSearchFriend);
        getSupportActionBar().setTitle(R.string.search_friends);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }
    private void ads(){

       // String TS = "fee50c333ff3825fd6ad6d38cff78154de3025546d47a84f" ;
        String ID = "95b9dd1c47e6407db176bc2398bda2c8323030f814183567" ;

        Appodeal.initialize(this,ID,Appodeal.INTERSTITIAL);
        Appodeal.show(this, Appodeal.INTERSTITIAL);
        Appodeal.isLoaded(Appodeal.INTERSTITIAL);

    }

    private void SearchFriends() {

        findFriendsBinding.SearchFindFriends.setSubmitButtonEnabled(true);
        findFriendsBinding.SearchFindFriends.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                searchFriend(query.toLowerCase().trim());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchFriend(newText.toLowerCase().trim());
                return false;
            }
        });

        findFriendsBinding.SearchFindFriends.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                showRecycleUsers();
                return false;
            }
        });
    }

    private void showRecycleUsers() {

        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {

                            List<ModelAdapterFireStore> modelAdapterFireStoreList = new ArrayList<>();

                            for (QueryDocumentSnapshot snapshot : task.getResult()) {

                                if (snapshot.getString(Constants.KEY_EMAIL).equals(PerfrancesManager.getInstance(getBaseContext()).getUserInformation(Constants.KEY_EMAIL))) {

                                    continue;
                                } else {
                                    modelAdapterFireStoreList.add(new ModelAdapterFireStore(snapshot.getString(Constants.KEY_USER_ID),
                                            snapshot.getString(Constants.KEY_FISRT_NAME),
                                            snapshot.getString(Constants.KEY_LAST_NAME),
                                            snapshot.getString(Constants.KEY_EMAIL),
                                            snapshot.getString(Constants.KEY_PROFILE_PHOTO),
                                            snapshot.getString(Constants.KEY_FCM_TOKEN)));
                                }
                            }
                            findFriendsBinding.RecyclFindFriends.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                            findFriendsBinding.RecyclFindFriends.setHasFixedSize(true);
                            findFriendsBinding.RecyclFindFriends.setAdapter(new AllUsersAdapter(getApplicationContext(), databaseRef, modelAdapterFireStoreList, new OnClickListenersSentRequest() {
                                @Override
                                public void onClickSentRequest(ModelAdapterFireStore store) {

                                    ads();
                                    sentRequest(store);

                                }
                            }, new OnClickLictenerCancelRequest() {
                                @Override
                                public void onClickCancelRequest(ModelAdapterFireStore store) {

                                    ads();
                                    cancelRequest(store);

                                }
                            }, new OnClickLictenerAcceptRequest() {
                                @Override
                                public void onClickAcceptRequest(ModelAdapterFireStore store) {

                                    ads();
                                    acceptRequest(store);
                                }
                            }, new OnClickLictenerRemoveFriend() {
                                @Override
                                public void OnClickLictenerRemoveFriend(ModelAdapterFireStore store) {

                                    ads();
                                    removeFriend(store);
                                }
                            }));

                            findFriendsBinding.SwiperFindFriends.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                @Override
                                public void onRefresh() {

                                    findFriendsBinding.SwiperFindFriends.setRefreshing(false);
                                    findFriendsBinding.RecyclFindFriends.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                                    findFriendsBinding.RecyclFindFriends.setHasFixedSize(true);
                                    findFriendsBinding.RecyclFindFriends.setAdapter(new AllUsersAdapter(getApplicationContext(), databaseRef, modelAdapterFireStoreList, new OnClickListenersSentRequest() {
                                        @Override
                                        public void onClickSentRequest(ModelAdapterFireStore store) {

                                            sentRequest(store);
                                        }
                                    }, new OnClickLictenerCancelRequest() {
                                        @Override
                                        public void onClickCancelRequest(ModelAdapterFireStore store) {

                                            cancelRequest(store);
                                        }
                                    }, new OnClickLictenerAcceptRequest() {
                                        @Override
                                        public void onClickAcceptRequest(ModelAdapterFireStore store) {

                                            acceptRequest(store);
                                        }
                                    }, new OnClickLictenerRemoveFriend() {
                                        @Override
                                        public void OnClickLictenerRemoveFriend(ModelAdapterFireStore store) {

                                            removeFriend(store);
                                        }
                                    }));
                                }
                            });
                        }

                    }
                });
    }

    private void searchFriend(String Email) {

        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, Email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        List<ModelAdapterFireStore> modelAdapterFireStoreList = new ArrayList<>();

                        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {

                            DocumentSnapshot snapshot = task.getResult().getDocuments().get(0);

                            if (snapshot.getString(Constants.KEY_EMAIL).equals(PerfrancesManager.getInstance(getBaseContext()).getUserInformation(Constants.KEY_EMAIL))) {


                            } else {
                                modelAdapterFireStoreList.add(new ModelAdapterFireStore(snapshot.getString(Constants.KEY_USER_ID),
                                        snapshot.getString(Constants.KEY_FISRT_NAME),
                                        snapshot.getString(Constants.KEY_LAST_NAME),
                                        snapshot.getString(Constants.KEY_EMAIL),
                                        snapshot.getString(Constants.KEY_PROFILE_PHOTO),
                                        snapshot.getString(Constants.KEY_FCM_TOKEN)));
                            }
                        }
                        findFriendsBinding.RecyclFindFriends.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                        findFriendsBinding.RecyclFindFriends.setHasFixedSize(true);
                        findFriendsBinding.RecyclFindFriends.setAdapter(new AllUsersAdapter(getApplicationContext(), databaseRef, modelAdapterFireStoreList, new OnClickListenersSentRequest() {
                            @Override
                            public void onClickSentRequest(ModelAdapterFireStore store) {
                                ads();
                                sentRequest(store);
                            }
                        }, new OnClickLictenerCancelRequest() {
                            @Override
                            public void onClickCancelRequest(ModelAdapterFireStore store) {

                                ads();
                               cancelRequest(store);
                            }
                        }, new OnClickLictenerAcceptRequest() {
                            @Override
                            public void onClickAcceptRequest(ModelAdapterFireStore store) {

                                ads();
                                 acceptRequest(store);
                            }
                        }, new OnClickLictenerRemoveFriend() {
                            @Override
                            public void OnClickLictenerRemoveFriend(ModelAdapterFireStore store) {

                                ads();
                               removeFriend(store);
                            }
                        }));
                    }
                });

    }

    private void sentRequest(ModelAdapterFireStore modelAdapterFireStore) {

        String date = new SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(new Date());
        ;

        HashMap<String, String> sentMap = new HashMap<>();
        sentMap.put("Request", "sent");
        sentMap.put("Date", date);

        HashMap<String, String> reciveMap = new HashMap<>();
        reciveMap.put("Request", "receive");
        reciveMap.put("Date", date);

        databaseRef.child("friendsRequests").child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                .child(modelAdapterFireStore.getID()).setValue(sentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    databaseRef.child("friendsRequests").child(modelAdapterFireStore.getID()).child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                            .setValue(reciveMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                Snackbar.make(findFriendsBinding.getRoot(), "Friend request has been sent", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

    }

    private void cancelRequest(ModelAdapterFireStore modelAdapterFireStore) {


        if (dialogRemoved != null || dialogRemoved == null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(FindFriends.this);

            View dialogView = LayoutInflater.from(getBaseContext()).inflate(R.layout.dialog_removerquest, findViewById(R.id.layoutRemoveRequestContainer));

            builder.setView(dialogView);
            dialogRemoved = builder.create();

            if (dialogRemoved.getWindow() != null) {

                dialogRemoved.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            TextView Title = dialogView.findViewById(R.id.title_dialog);
            TextView Remove = dialogView.findViewById(R.id.RemoveRquest_dialog);
            TextView Cancel = dialogView.findViewById(R.id.Cancel_dialog);

            Title.setText("Cancel Request");

            Remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    databaseRef.child("friendsRequests").child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                            .child(modelAdapterFireStore.getID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                databaseRef.child("friendsRequests").child(modelAdapterFireStore.getID()).child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            dialogRemoved.dismiss();
                                            Snackbar.make(findFriendsBinding.getRoot(), "Request has been canceled", Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });

                }
            });

            Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialogRemoved.dismiss();
                }
            });
        }
        dialogRemoved.show();
    }

    private void acceptRequest(ModelAdapterFireStore modelAdapterFireStore) {

        String date = new SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(new Date());

        HashMap<String, String> contactMap = new HashMap<>();
        contactMap.put("Contact", "save");
        contactMap.put("Date", date);

        databaseRef.child("friendsRequests").child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                .child(modelAdapterFireStore.getID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    databaseRef.child("friendsRequests").child(modelAdapterFireStore.getID()).child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                databaseRef.child("Friends").child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                                        .child(modelAdapterFireStore.getID()).setValue(contactMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            databaseRef.child("Friends").child(modelAdapterFireStore.getID())
                                                    .child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                                                    .setValue(contactMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {

                                                        Snackbar.make(findFriendsBinding.getRoot(), "Addition was successfully accepted", Snackbar.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

    }

    private void removeFriend(ModelAdapterFireStore modelAdapterFireStore) {

        if (dialogRemoved != null || dialogRemoved == null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(FindFriends.this);

            View dialogView = LayoutInflater.from(getBaseContext()).inflate(R.layout.dialog_removerquest, findViewById(R.id.layoutRemoveRequestContainer));

            builder.setView(dialogView);
            dialogRemoved = builder.create();

            if (dialogRemoved.getWindow() != null) {

                dialogRemoved.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            TextView Title = dialogView.findViewById(R.id.title_dialog);
            TextView Remove = dialogView.findViewById(R.id.RemoveRquest_dialog);
            TextView Cancel = dialogView.findViewById(R.id.Cancel_dialog);

            Title.setText("Cancel friendship");

            Remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseRef.child("Friends").child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                            .child(modelAdapterFireStore.getID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                databaseRef.child("Friends").child(modelAdapterFireStore.getID()).child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            dialogRemoved.dismiss();
                                            Snackbar.make(findFriendsBinding.getRoot(), "Deletion was successful", Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });

                }
            });

            Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialogRemoved.dismiss();
                }
            });
        }
        dialogRemoved.show();

    }


}