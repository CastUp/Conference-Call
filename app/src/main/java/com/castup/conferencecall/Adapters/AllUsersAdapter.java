package com.castup.conferencecall.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.castup.conferencecall.Constants.Constants;
import com.castup.conferencecall.Constants.PerfrancesManager;
import com.castup.conferencecall.Listeners.OnClickLictenerAcceptRequest;
import com.castup.conferencecall.Listeners.OnClickLictenerCancelRequest;
import com.castup.conferencecall.Listeners.OnClickLictenerRemoveFriend;
import com.castup.conferencecall.Listeners.OnClickListenersSentRequest;
import com.castup.conferencecall.Model.ModelAdapterFireStore;
import com.castup.conferencecall.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.UsersViewHolder> {

    private Context context;
    private DatabaseReference databaseRef;
    private List<ModelAdapterFireStore> storeList;
    private OnClickListenersSentRequest onClickListenersSentRequest;
    private OnClickLictenerCancelRequest onClickLictenerCancelRequest;
    private OnClickLictenerAcceptRequest onClickLictenerAcceptRequest;
    private OnClickLictenerRemoveFriend onClickLictenerRemoveFriend;

    public AllUsersAdapter(Context context, DatabaseReference databaseRef, List<ModelAdapterFireStore> storeList, OnClickListenersSentRequest onClickListenersSentRequest, OnClickLictenerCancelRequest onClickLictenerCancelRequest, OnClickLictenerAcceptRequest onClickLictenerAcceptRequest, OnClickLictenerRemoveFriend onClickLictenerRemoveFriend) {
        this.context = context;
        this.databaseRef = databaseRef;
        this.storeList = storeList;
        this.onClickListenersSentRequest = onClickListenersSentRequest;
        this.onClickLictenerCancelRequest = onClickLictenerCancelRequest;
        this.onClickLictenerAcceptRequest = onClickLictenerAcceptRequest;
        this.onClickLictenerRemoveFriend = onClickLictenerRemoveFriend;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UsersViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.find_friends_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {

        holder.infoUser(storeList, position);
        holder.showButtons(context, databaseRef, storeList, position);
        holder.showButton(context, databaseRef, storeList, position);

    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {

        private TextView firstChar, userName;
        private CircleImageView imagePhoto;
        private ImageView addUser, cancel, accept, friend;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);

            firstChar = itemView.findViewById(R.id.imageText);
            userName = itemView.findViewById(R.id.UserName);
            imagePhoto = itemView.findViewById(R.id.image_photo);
            addUser = itemView.findViewById(R.id.addUser);
            friend = itemView.findViewById(R.id.saveContact);
            cancel = itemView.findViewById(R.id.cancelRequest);
            accept = itemView.findViewById(R.id.acceptRequest);

            addUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListenersSentRequest.onClickSentRequest(storeList.get(getAdapterPosition()));
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onClickLictenerCancelRequest.onClickCancelRequest(storeList.get(getAdapterPosition()));
                }
            });

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onClickLictenerAcceptRequest.onClickAcceptRequest(storeList.get(getAdapterPosition()));
                }
            });

            friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onClickLictenerRemoveFriend.OnClickLictenerRemoveFriend(storeList.get(getAdapterPosition()));
                }
            });

        }

        public void infoUser(List<ModelAdapterFireStore> stores, int position) {

            if (stores.get(position).getPhoto().equals("")) {

                char fr_name = Character.toUpperCase(stores.get(position).getFirstName().charAt(0));
                firstChar.setText(String.valueOf(fr_name));
                firstChar.setVisibility(View.VISIBLE);
                imagePhoto.setVisibility(View.GONE);

            } else {

                Picasso.get().load(stores.get(position).getPhoto()).resize(500,500).into(imagePhoto);
                firstChar.setVisibility(View.GONE);
                imagePhoto.setVisibility(View.VISIBLE);

            }

            String name = Character.toUpperCase(stores.get(position).getFirstName().charAt(0)) + stores.get(position).getFirstName().toLowerCase().substring(1) +
                    " " + stores.get(position).getLastName().toLowerCase();
            userName.setText(name);

        }

        public void showButtons(Context context, DatabaseReference databaseRef, List<ModelAdapterFireStore> stores, int position) {

            databaseRef.child("friendsRequests").child(stores.get(position).getID()).child(PerfrancesManager.getInstance(context.getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                    .child("Request").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {

                        String request = dataSnapshot.getValue().toString();

                        if (request.equals("receive")) {

                            addUser.setVisibility(View.GONE);
                            friend.setVisibility(View.GONE);
                            accept.setVisibility(View.GONE);
                            cancel.setVisibility(View.VISIBLE);

                        } else if (request.equals("sent")) {

                            addUser.setVisibility(View.GONE);
                            friend.setVisibility(View.GONE);
                            accept.setVisibility(View.VISIBLE);
                            cancel.setVisibility(View.VISIBLE);

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            databaseRef.child("Friends").child(stores.get(position).getID()).child(PerfrancesManager.getInstance(context.getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                    .child("Contact").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {

                        String contact = dataSnapshot.getValue().toString();

                        if (contact.equals("save")) {

                            addUser.setVisibility(View.GONE);
                            friend.setVisibility(View.VISIBLE);
                            accept.setVisibility(View.GONE);
                            cancel.setVisibility(View.GONE);

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        public void showButton(Context context, DatabaseReference databaseRef, List<ModelAdapterFireStore> stores, int position) {

            databaseRef.child("friendsRequests").child(stores.get(position).getID()).child(PerfrancesManager.getInstance(context.getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                    .child("Request").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.exists()) {

                        databaseRef.child("Friends").child(stores.get(position).getID()).child(PerfrancesManager.getInstance(context.getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                                .child("Contact").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (!dataSnapshot.exists()) {

                                    addUser.setVisibility(View.VISIBLE);
                                    friend.setVisibility(View.GONE);
                                    accept.setVisibility(View.GONE);
                                    cancel.setVisibility(View.GONE);

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }
}
