package com.castup.conferencecall.Requests_ProfileNav;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appodeal.ads.Appodeal;
import com.castup.conferencecall.Constants.Constants;
import com.castup.conferencecall.Constants.PerfrancesManager;
import com.castup.conferencecall.Model.ModelAdapterFireStore;
import com.castup.conferencecall.R;
import com.castup.conferencecall.databinding.ActivityReceiveRequestBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReceiveRequest extends AppCompatActivity {

    private ActivityReceiveRequestBinding receiveRequestBinding ;
    private DatabaseReference databaseRef ;
    private AlertDialog dialogRemoved;
    private FirebaseRecyclerOptions<ModelAdapterFireStore> options ;
    private FirebaseRecyclerAdapter<ModelAdapterFireStore, ReceiveRequest.RequestsViewHolder> adapter ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiveRequestBinding = ActivityReceiveRequestBinding.inflate(getLayoutInflater());
        setContentView(receiveRequestBinding.getRoot());

        databaseRef = FirebaseDatabase.getInstance().getReference();

        Toolbar();
        showReceivedRequests();
        searchRequests();

    }

    private void Toolbar() {

        setSupportActionBar(receiveRequestBinding.ToolbarSearchReceive);
        getSupportActionBar().setTitle("Received Requests");
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

    private void showReceivedRequests(){

        Query query = databaseRef.child("friendsRequests").child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID));

        options = new FirebaseRecyclerOptions.Builder<ModelAdapterFireStore>()
                .setQuery(query,ModelAdapterFireStore.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<ModelAdapterFireStore, RequestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RequestsViewHolder requestsViewHolder, int position , @NonNull ModelAdapterFireStore modelAdapterFireStore) {

                String Ref = getRef(position).getKey();

                databaseRef.child("friendsRequests").child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                        .child(Ref).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){

                            if(dataSnapshot.child("Request").getValue().toString().equals("receive")){

                                databaseRef.child("Users").child(Ref).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshotN) {

                                        if(dataSnapshotN.exists()){

                                            requestsViewHolder.textDate.setText(dataSnapshot.child("Date").getValue().toString());
                                            requestsViewHolder.textDate.setVisibility(View.VISIBLE);

                                            requestsViewHolder.InfoSenderRequest(dataSnapshotN);
                                            requestsViewHolder.Buttons(Ref);

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RequestsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.items_requstes,parent,false));
            }
        };

        receiveRequestBinding.RecyclFindReceive.setHasFixedSize(true);
        receiveRequestBinding.RecyclFindReceive.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        receiveRequestBinding.RecyclFindReceive.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();

        receiveRequestBinding.SwiperFindRecieve.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                receiveRequestBinding.SwiperFindRecieve.setRefreshing(false);
                receiveRequestBinding.RecyclFindReceive.setHasFixedSize(true);
                receiveRequestBinding.RecyclFindReceive.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                receiveRequestBinding.RecyclFindReceive.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                adapter.startListening();

            }
        });
    }

    private void searchReceivedRequests(String name){

        Query query = databaseRef.child("friendsRequests").child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID));

        options = new FirebaseRecyclerOptions.Builder<ModelAdapterFireStore>()
                .setQuery(query,ModelAdapterFireStore.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<ModelAdapterFireStore, RequestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RequestsViewHolder requestsViewHolder, int position , @NonNull ModelAdapterFireStore modelAdapterFireStore) {

                String Ref = getRef(position).getKey();

                databaseRef.child("friendsRequests").child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                        .child(Ref).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){

                            if(dataSnapshot.child("Request").getValue().toString().equals("receive")){

                                databaseRef.child("Users").child(Ref).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshotN) {

                                        if(dataSnapshotN.exists() && dataSnapshotN.child(Constants.KEY_FISRT_NAME).getValue().toString().equals(name.toLowerCase().trim())||
                                           dataSnapshotN.child(Constants.KEY_LAST_NAME).getValue().toString().equals(name.toLowerCase().trim()) ){

                                            requestsViewHolder.textDate.setText(dataSnapshot.child("Date").getValue().toString());
                                            requestsViewHolder.textDate.setVisibility(View.VISIBLE);

                                            requestsViewHolder.InfoSenderRequest(dataSnapshotN);
                                            requestsViewHolder.Buttons(Ref);

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RequestsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.items_requstes,parent,false));
            }
        };

        receiveRequestBinding.RecyclFindReceive.setHasFixedSize(true);
        receiveRequestBinding.RecyclFindReceive.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        receiveRequestBinding.RecyclFindReceive.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();

        receiveRequestBinding.SwiperFindRecieve.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                receiveRequestBinding.SwiperFindRecieve.setRefreshing(false);
                receiveRequestBinding.RecyclFindReceive.setHasFixedSize(true);
                receiveRequestBinding.RecyclFindReceive.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                receiveRequestBinding.RecyclFindReceive.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                adapter.startListening();

            }
        });
    }

    private void searchRequests(){

        receiveRequestBinding.SearchFindReceive.setSubmitButtonEnabled(true);
        receiveRequestBinding.SearchFindReceive.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                searchReceivedRequests(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchReceivedRequests(newText);
                return false;
            }
        });

        receiveRequestBinding.SearchFindReceive.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                showReceivedRequests();
                return false;
            }
        });
    }

    public class RequestsViewHolder extends RecyclerView.ViewHolder{

        private TextView txetImage , textName , textDate , textAccept , textCancel ;
        private CircleImageView Photo ;
        private LinearLayout linearRequests , linear1 , linear2;

        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);

            txetImage   = itemView.findViewById(R.id.imageTextItem);
            Photo       = itemView.findViewById(R.id.imagePhotoItem);

            textName    = itemView.findViewById(R.id.FriendName);
            textDate    = itemView.findViewById(R.id.DateSent);
            textAccept  = itemView.findViewById(R.id.acceptRequests);
            textCancel  = itemView.findViewById(R.id.cancelRequests);

            linear1      = itemView.findViewById(R.id.linear1);
            linear2      = itemView.findViewById(R.id.linear2);
            linearRequests = itemView.findViewById(R.id.linearRequests);
        }


        public void InfoSenderRequest(com.google.firebase.database.DataSnapshot dataSnapshot){

            if(dataSnapshot.child(Constants.KEY_PROFILE_PHOTO).getValue().toString().equals("")){

                char firstName = Character.toUpperCase(dataSnapshot.child(Constants.KEY_FISRT_NAME).getValue().toString().charAt(0));
                txetImage.setText(String.valueOf(firstName));
                txetImage.setVisibility(View.VISIBLE);
                Photo.setVisibility(View.GONE);

            }else {

                Picasso.get().load(dataSnapshot.child(Constants.KEY_PROFILE_PHOTO).getValue().toString()).resize(500,500).into(Photo);
                Photo.setVisibility(View.VISIBLE);
                txetImage.setVisibility(View.GONE);
            }

            String name = Character.toUpperCase(dataSnapshot.child(Constants.KEY_FISRT_NAME).getValue().toString().charAt(0))
                    + dataSnapshot.child(Constants.KEY_FISRT_NAME).getValue().toString().substring(1).toLowerCase()+" "
                    + dataSnapshot.child(Constants.KEY_LAST_NAME).getValue().toString().toLowerCase();

            textName.setText(name);
            textName.setVisibility(View.VISIBLE);
            textCancel.setVisibility(View.VISIBLE);
            textAccept.setVisibility(View.VISIBLE);
            linear1.setVisibility(View.VISIBLE);
            linear2.setVisibility(View.VISIBLE);
            linearRequests.setVisibility(View.VISIBLE);

        }

        public void Buttons(String Ref){

            textCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    cancelRequest(Ref);
                }
            });

            textAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ads(v);
                    acceptRequest(Ref);

                }
            });
        }

    }

    private void cancelRequest(String Ref) {


        if (dialogRemoved != null || dialogRemoved == null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveRequest.this);

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
                            .child(Ref).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                databaseRef.child("friendsRequests").child(Ref).child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){

                                            dialogRemoved.dismiss();
                                            Snackbar.make(receiveRequestBinding.getRoot(), "Request has been canceled", Snackbar.LENGTH_SHORT).show();
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
    private void acceptRequest(String Ref){

        String date = new SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(new Date());

        HashMap<String, String> contactMap = new HashMap<>();
        contactMap.put("Contact", "save");
        contactMap.put("Date", date);

        databaseRef.child("friendsRequests").child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                .child(Ref).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    databaseRef.child("friendsRequests").child(Ref).child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                databaseRef.child("Friends").child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                                        .child(Ref).setValue(contactMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){

                                            databaseRef.child("Friends").child(Ref).child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                                                    .setValue(contactMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if(task.isSuccessful()){

                                                        Snackbar.make(receiveRequestBinding.getRoot(), "Addition was successfully accepted", Snackbar.LENGTH_SHORT).show();
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
}