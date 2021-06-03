package com.castup.conferencecall.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;
import com.castup.conferencecall.Constants.Constants;
import com.castup.conferencecall.Constants.PerfrancesManager;
import com.castup.conferencecall.Model.ModelAdapterFireStore;
import com.castup.conferencecall.R;
import com.castup.conferencecall.Requests_ProfileNav.Profile;
import com.castup.conferencecall.Requests_ProfileNav.ReceiveRequest;
import com.castup.conferencecall.Requests_ProfileNav.SentRequests;
import com.castup.conferencecall.databinding.ActivityMainBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;
    private FirebaseFirestore firestore;
    private DatabaseReference databaseRef;
    private StorageReference firebaseStorage;
    private static final int REQUEST_CODE_BATTERY_OPTIMIZATIONS = 1;
    private UploadTask uploadTask;
    private AlertDialog dialogRemoved, dialogBattery;
    private FirebaseRecyclerOptions<ModelAdapterFireStore> options;
    private FirebaseRecyclerAdapter<ModelAdapterFireStore, FriendViewHolder> adapter;

    private static final float END_SCALE = 0.7f;
    private static final int PHOTO_PERMISSION = 1;
    private static final int OPEN_GALLERY = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());


        firestore = FirebaseFirestore.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance().getReference();
        Toobar();
        getToken();
        onClick_Findfriends();
        RealTimeDatabaseRef();
        showRecyclFriend();
        setNavigation();
        battery_optimizations();


    }

    private void Toobar() {

        setSupportActionBar(mainBinding.toolbarMain);
        getSupportActionBar().setTitle("Home");
    }

    private void ads(View view){

        // String TS = "fee50c333ff3825fd6ad6d38cff78154de3025546d47a84f" ;
        String ID = "95b9dd1c47e6407db176bc2398bda2c8323030f814183567" ;

        Appodeal.initialize((Activity)view.getContext(),ID,Appodeal.INTERSTITIAL);
        Appodeal.show((Activity)view.getContext(), Appodeal.INTERSTITIAL);
        Appodeal.isLoaded(Appodeal.INTERSTITIAL);

    }

    private void setNavigation() {

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mainBinding.drawerMain, mainBinding.toolbarMain, R.string.open, R.string.Close);
        mainBinding.drawerMain.addDrawerListener(toggle);
        toggle.syncState();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            toggle.getDrawerArrowDrawable().setColor(Color.parseColor("#FF253BB3"));
        } else {

            toggle.getDrawerArrowDrawable().setColor(Color.GRAY);
        }

        mainBinding.navigationMain.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.ProfileMenu:

                        startActivity(new Intent(getApplicationContext(), Profile.class));
                        break;

                    case R.id.requestsMenu:

                        startActivity(new Intent(getApplicationContext(), ReceiveRequest.class));
                        break;

                    case R.id.sentMenu:

                        startActivity(new Intent(getApplicationContext(), SentRequests.class));
                        break;

                    case R.id.SignInMenu:

                        SignOut();
                        break;
                }

                mainBinding.drawerMain.closeDrawer(GravityCompat.START);

                return true;
            }
        });

        animationDrawerLayout();
        setHeaderNav();

    }

    private void animationDrawerLayout() {

        // mainBinding.drawerMain.setScrimColor(getResources().getColor(R.color.colorPrimary,null));
        mainBinding.drawerMain.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;

                mainBinding.contantView.setScaleX(offsetScale);
                mainBinding.contantView.setScaleY(offsetScale);

                final float xoffset = mainBinding.drawerMain.getWidth() * slideOffset;
                final float xoffsetDiff = mainBinding.contantView.getWidth() * diffScaledOffset / .7f;
                final float xTranslation = xoffset - xoffsetDiff;

                mainBinding.contantView.setTranslationX(xTranslation);

            }
        });

    }

    private void setPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PHOTO_PERMISSION);
            } else {
                setOpenGallery();
            }
        }

    }

    private void setOpenGallery() {

        Intent uplaodPhoto = new Intent(Intent.ACTION_GET_CONTENT);
        uplaodPhoto.setType("image/*");
        startActivityForResult(uplaodPhoto, OPEN_GALLERY);
    }

    private void setHeaderNav() {

        View header = mainBinding.navigationMain.getHeaderView(0);

        CircleImageView imageProfile = header.findViewById(R.id.ProfileImageHeader);
        ImageView uplaodImage = header.findViewById(R.id.uplaodImageHeader);
        TextView Name = header.findViewById(R.id.NameHeader);
        TextView Email = header.findViewById(R.id.EmailHeader);

        uplaodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setPermission();
            }
        });

        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .document(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful() && task.getResult() != null) {

                            if (task.getResult().getString(Constants.KEY_PROFILE_PHOTO).equals("")) {

                                imageProfile.setImageResource(R.mipmap.ic_launcher);

                            } else {

                                databaseRef.child("Users").child(PerfrancesManager.getInstance(getApplicationContext().getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                                        .child(Constants.KEY_PROFILE_PHOTO).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.exists()) {

                                            Picasso.get().load(dataSnapshot.getValue().toString()).into(imageProfile);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                            }

                            String Username = Character.toUpperCase(task.getResult().getString(Constants.KEY_FISRT_NAME).charAt(0)) + task.getResult().getString(Constants.KEY_FISRT_NAME).toLowerCase().substring(1) +
                                    " " + task.getResult().getString(Constants.KEY_LAST_NAME).toLowerCase();

                            Name.setText(Username);

                            String UserEmail = Character.toUpperCase(task.getResult().getString(Constants.KEY_EMAIL).charAt(0))
                                    + task.getResult().getString(Constants.KEY_EMAIL).substring(1).toLowerCase();

                            Email.setText(UserEmail);
                        }

                    }
                });


    }

    private void onClick_Findfriends() {

        mainBinding.SearchFriendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), FindFriends.class));
                ads(v);
            }
        });

    }

    private void getToken() {

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {

                if (task.isSuccessful() && task.getResult() != null) {
                    UpdateDateUser(task.getResult().getToken());
                }
            }
        });
    }

    private void UpdateDateUser(String Token) {

        HashMap<String, Object> Updated = new HashMap<>();
        Updated.put(Constants.KEY_FCM_TOKEN, Token);
        Updated.put(Constants.KEY_USER_ID, PerfrancesManager.getInstance(getBaseContext()).getUserInformation(Constants.KEY_USER_ID));

        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .document(PerfrancesManager.getInstance(getBaseContext()).getUserInformation(Constants.KEY_USER_ID))
                .update(Updated)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(MainActivity.this, "Unable To Send Token: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void RealTimeDatabaseRef() {

        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .document(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful() && task.getResult() != null) {

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put(Constants.KEY_USER_ID, PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID));
                            hashMap.put(Constants.KEY_FISRT_NAME, task.getResult().getString(Constants.KEY_FISRT_NAME));
                            hashMap.put(Constants.KEY_LAST_NAME, task.getResult().getString(Constants.KEY_LAST_NAME));
                            hashMap.put(Constants.KEY_EMAIL, task.getResult().getString(Constants.KEY_EMAIL));
                            hashMap.put(Constants.KEY_PROFILE_PHOTO, task.getResult().getString(Constants.KEY_PROFILE_PHOTO));
                            hashMap.put(Constants.KEY_FCM_TOKEN, task.getResult().getString(Constants.KEY_FCM_TOKEN));

                            PerfrancesManager.getInstance(getBaseContext()).setUserInformation(Constants.KEY_FISRT_NAME, task.getResult().getString(Constants.KEY_FISRT_NAME));
                            PerfrancesManager.getInstance(getBaseContext()).setUserInformation(Constants.KEY_LAST_NAME, task.getResult().getString(Constants.KEY_LAST_NAME));
                            PerfrancesManager.getInstance(getBaseContext()).setUserInformation(Constants.KEY_EMAIL, task.getResult().getString(Constants.KEY_EMAIL));
                            PerfrancesManager.getInstance(getBaseContext()).setUserInformation(Constants.KEY_PROFILE_PHOTO, task.getResult().getString(Constants.KEY_PROFILE_PHOTO));

                            databaseRef.child("Users").child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                                    .setValue(hashMap);
                        }

                    }
                });


    }

    private void showRecyclFriend() {

        Query query = databaseRef.child("Friends").child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID));
        options = new FirebaseRecyclerOptions.Builder<ModelAdapterFireStore>()
                .setQuery(query, ModelAdapterFireStore.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<ModelAdapterFireStore, FriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendViewHolder friendViewHolder, int position, @NonNull ModelAdapterFireStore modelAdapterFireStore) {

                String Ref = getRef(position).getKey();

                databaseRef.child("Users").child(Ref).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            friendViewHolder.showInfoFriends(dataSnapshot);
                            friendViewHolder.removefriend(Ref);
                            friendViewHolder.vedioCall.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    ads(v);
                                    if (dataSnapshot.hasChild(Constants.KEY_FCM_TOKEN)) {

                                        Intent intentVedio = new Intent(getApplicationContext(), OutGoingInvitation.class);
                                        intentVedio.putExtra(Constants.KEY_FISRT_NAME, dataSnapshot.child(Constants.KEY_FISRT_NAME).getValue().toString());
                                        intentVedio.putExtra(Constants.KEY_LAST_NAME, dataSnapshot.child(Constants.KEY_LAST_NAME).getValue().toString());
                                        intentVedio.putExtra(Constants.KEY_EMAIL, dataSnapshot.child(Constants.KEY_EMAIL).getValue().toString());
                                        intentVedio.putExtra(Constants.KEY_PROFILE_PHOTO, dataSnapshot.child(Constants.KEY_PROFILE_PHOTO).getValue().toString());
                                        intentVedio.putExtra(Constants.KEY_FCM_TOKEN, dataSnapshot.child(Constants.KEY_FCM_TOKEN).getValue().toString());
                                        intentVedio.putExtra("type", "vedio");
                                        startActivity(intentVedio);


                                    } else {

                                        String name = Character.toUpperCase(dataSnapshot.child(Constants.KEY_FISRT_NAME).getValue().toString().charAt(0))
                                                + dataSnapshot.child(Constants.KEY_FISRT_NAME).getValue().toString().substring(1).toLowerCase() + " " +
                                                dataSnapshot.child(Constants.KEY_LAST_NAME).getValue().toString().toLowerCase();

                                        Snackbar.make(mainBinding.getRoot(), name + " is not available for meeting", Snackbar.LENGTH_SHORT).show();
                                    }

                                }
                            });
                            friendViewHolder.voiceCall.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    ads(v);
                                    if (dataSnapshot.hasChild(Constants.KEY_FCM_TOKEN)) {

                                        Intent intentAudio = new Intent(getApplicationContext(), OutGoingInvitation.class);
                                        intentAudio.putExtra(Constants.KEY_FISRT_NAME, dataSnapshot.child(Constants.KEY_FISRT_NAME).getValue().toString());
                                        intentAudio.putExtra(Constants.KEY_LAST_NAME, dataSnapshot.child(Constants.KEY_LAST_NAME).getValue().toString());
                                        intentAudio.putExtra(Constants.KEY_EMAIL, dataSnapshot.child(Constants.KEY_EMAIL).getValue().toString());
                                        intentAudio.putExtra(Constants.KEY_PROFILE_PHOTO, dataSnapshot.child(Constants.KEY_PROFILE_PHOTO).getValue().toString());
                                        intentAudio.putExtra(Constants.KEY_FCM_TOKEN, dataSnapshot.child(Constants.KEY_FCM_TOKEN).getValue().toString());
                                        intentAudio.putExtra("type", "audio");
                                        startActivity(intentAudio);

                                    } else {

                                        String name = Character.toUpperCase(dataSnapshot.child(Constants.KEY_FISRT_NAME).getValue().toString().charAt(0))
                                                + dataSnapshot.child(Constants.KEY_FISRT_NAME).getValue().toString().substring(1).toLowerCase() + " " +
                                                dataSnapshot.child(Constants.KEY_LAST_NAME).getValue().toString().toLowerCase();

                                        Snackbar.make(mainBinding.getRoot(), name + " is not available for meeting", Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FriendViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.continer_friends, parent, false));
            }
        };

        mainBinding.RecyclMain.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mainBinding.RecyclMain.setHasFixedSize(true);
        mainBinding.RecyclMain.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();

    }

    private void searchRecyclFriend(String name) {

        Query query = databaseRef.child("Friends").child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID));
        options = new FirebaseRecyclerOptions.Builder<ModelAdapterFireStore>()
                .setQuery(query, ModelAdapterFireStore.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<ModelAdapterFireStore, FriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendViewHolder friendViewHolder, int position, @NonNull ModelAdapterFireStore modelAdapterFireStore) {

                String Ref = getRef(position).getKey();


                databaseRef.child("Users").child(Ref).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        if (dataSnapshot.exists() && dataSnapshot.child(Constants.KEY_FISRT_NAME).getValue().toString().equals(name.trim().toLowerCase())
                                || dataSnapshot.child(Constants.KEY_LAST_NAME).getValue().toString().equals(name.trim().toLowerCase())) {

                            friendViewHolder.showInfoFriends(dataSnapshot);
                            friendViewHolder.removefriend(Ref);
                            friendViewHolder.vedioCall.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    ads(v);
                                    if (dataSnapshot.hasChild(Constants.KEY_FCM_TOKEN)) {

                                        Intent intentVedio = new Intent(getApplicationContext(), OutGoingInvitation.class);
                                        intentVedio.putExtra(Constants.KEY_FISRT_NAME, dataSnapshot.child(Constants.KEY_FISRT_NAME).getValue().toString());
                                        intentVedio.putExtra(Constants.KEY_LAST_NAME, dataSnapshot.child(Constants.KEY_LAST_NAME).getValue().toString());
                                        intentVedio.putExtra(Constants.KEY_EMAIL, dataSnapshot.child(Constants.KEY_EMAIL).getValue().toString());
                                        intentVedio.putExtra(Constants.KEY_PROFILE_PHOTO, dataSnapshot.child(Constants.KEY_PROFILE_PHOTO).getValue().toString());
                                        intentVedio.putExtra(Constants.KEY_FCM_TOKEN, dataSnapshot.child(Constants.KEY_FCM_TOKEN).getValue().toString());
                                        intentVedio.putExtra("type", "vedio");
                                        startActivity(intentVedio);

                                    } else {

                                        String name = Character.toUpperCase(dataSnapshot.child(Constants.KEY_FISRT_NAME).getValue().toString().charAt(0))
                                                + dataSnapshot.child(Constants.KEY_FISRT_NAME).getValue().toString().substring(1).toLowerCase() + " " +
                                                dataSnapshot.child(Constants.KEY_LAST_NAME).getValue().toString().toLowerCase();

                                        Snackbar.make(mainBinding.getRoot(), name + " is not available for meeting", Snackbar.LENGTH_SHORT).show();
                                    }

                                }
                            });
                            friendViewHolder.voiceCall.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    ads(v);
                                    if (dataSnapshot.hasChild(Constants.KEY_FCM_TOKEN)) {

                                        Intent intentAudio = new Intent(getApplicationContext(), OutGoingInvitation.class);
                                        intentAudio.putExtra(Constants.KEY_FISRT_NAME, dataSnapshot.child(Constants.KEY_FISRT_NAME).getValue().toString());
                                        intentAudio.putExtra(Constants.KEY_LAST_NAME, dataSnapshot.child(Constants.KEY_LAST_NAME).getValue().toString());
                                        intentAudio.putExtra(Constants.KEY_EMAIL, dataSnapshot.child(Constants.KEY_EMAIL).getValue().toString());
                                        intentAudio.putExtra(Constants.KEY_PROFILE_PHOTO, dataSnapshot.child(Constants.KEY_PROFILE_PHOTO).getValue().toString());
                                        intentAudio.putExtra(Constants.KEY_FCM_TOKEN, dataSnapshot.child(Constants.KEY_FCM_TOKEN).getValue().toString());
                                        intentAudio.putExtra("type", "audio");
                                        startActivity(intentAudio);

                                    } else {

                                        String name = Character.toUpperCase(dataSnapshot.child(Constants.KEY_FISRT_NAME).getValue().toString().charAt(0))
                                                + dataSnapshot.child(Constants.KEY_FISRT_NAME).getValue().toString().substring(1).toLowerCase() + " " +
                                                dataSnapshot.child(Constants.KEY_LAST_NAME).getValue().toString().toLowerCase();

                                        Snackbar.make(mainBinding.getRoot(), name + " is not available for meeting", Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {

                            friendViewHolder.RootLinear.setVisibility(View.GONE);
                            friendViewHolder.friendName.setVisibility(View.GONE);
                            friendViewHolder.status.setVisibility(View.GONE);
                            friendViewHolder.imageChar.setVisibility(View.GONE);
                            friendViewHolder.imagePhoto.setVisibility(View.GONE);
                            friendViewHolder.voiceCall.setVisibility(View.GONE);
                            friendViewHolder.vedioCall.setVisibility(View.GONE);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FriendViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.continer_friends, parent, false));
            }
        };

        mainBinding.RecyclMain.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mainBinding.RecyclMain.setHasFixedSize(true);
        mainBinding.RecyclMain.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.searchMain).getActionView();

        searchView.setQueryHint("Your friend's name");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchRecyclFriend(newText);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                showRecyclFriend();
                return false;
            }
        });

        return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case PHOTO_PERMISSION:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    setOpenGallery();
                } else {

                    Toast.makeText(getApplicationContext(), "Entry validity must be obtained", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case OPEN_GALLERY:

                if (resultCode == RESULT_OK && data != null) {

                    ProgressDialog dialogGellary = new ProgressDialog(MainActivity.this);

                    String randamimage = UUID.randomUUID().toString();
                    Uri imageUri = data.getData();

                    StorageReference storage = firebaseStorage.child("ImagesProfile").child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                            .child(randamimage + ".jpg");
                    uploadTask = storage.putFile(imageUri);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    if (uri.isAbsolute()) {

                                        String pathPhoto = uri.toString();

                                        HashMap<String, Object> updatePhoto = new HashMap<>();
                                        updatePhoto.put(Constants.KEY_PROFILE_PHOTO, pathPhoto);

                                        firestore.collection(Constants.KEY_COLLECTION_USERS)
                                                .document(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                                                .update(updatePhoto)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        databaseRef.child("Users").child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                                                                .child(Constants.KEY_PROFILE_PHOTO).setValue(pathPhoto).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()) {

                                                                    dialogGellary.dismiss();
                                                                    Snackbar.make(mainBinding.getRoot(), "Done", Snackbar.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                    }
                                }
                            });

                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100 * taskSnapshot.getBytesTransferred() / (int) taskSnapshot.getTotalByteCount());
                            dialogGellary.setMessage("Uplaoding " + ((int) progress + " %..."));
                            dialogGellary.show();
                        }
                    });


                }
                break;

            case REQUEST_CODE_BATTERY_OPTIMIZATIONS:

                battery_optimizations();
                break;
        }
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {

        private TextView imageChar, friendName, status;
        private CircleImageView imagePhoto;
        private ImageView voiceCall, vedioCall;
        private LinearLayout RootLinear;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);

            imageChar = itemView.findViewById(R.id.imageTextContiner);
            friendName = itemView.findViewById(R.id.FriendNameContiner);
            status = itemView.findViewById(R.id.StatusFriendContiner);
            imagePhoto = itemView.findViewById(R.id.imagePhotoContiner);
            voiceCall = itemView.findViewById(R.id.CallVoice);
            vedioCall = itemView.findViewById(R.id.CallVideo);
            RootLinear = itemView.findViewById(R.id.RootLinear);

        }

        public void showInfoFriends(DataSnapshot dataSnapshot) {

            if (dataSnapshot.child(Constants.KEY_PROFILE_PHOTO).getValue().toString().equals("")) {

                char firstName = Character.toUpperCase(dataSnapshot.child(Constants.KEY_FISRT_NAME).getValue().toString().charAt(0));
                imageChar.setText(String.valueOf(firstName));
                imageChar.setVisibility(View.VISIBLE);
                imagePhoto.setVisibility(View.GONE);

            } else {

                Picasso.get().load(dataSnapshot.child(Constants.KEY_PROFILE_PHOTO).getValue().toString()).resize(500, 500).into(imagePhoto);
                imagePhoto.setVisibility(View.VISIBLE);
                imageChar.setVisibility(View.GONE);
            }

            if (dataSnapshot.hasChild(Constants.KEY_FCM_TOKEN)) {

                status.setText("Online");
                status.setTextColor(Color.rgb(12, 143, 18));

            } else {

                status.setText("Off line");
                status.setTextColor(Color.GRAY);
            }

            String name = Character.toUpperCase(dataSnapshot.child(Constants.KEY_FISRT_NAME).getValue().toString().charAt(0))
                    + dataSnapshot.child(Constants.KEY_FISRT_NAME).getValue().toString().substring(1).toLowerCase()
                    + " " + dataSnapshot.child(Constants.KEY_LAST_NAME).getValue().toString().toLowerCase();

            friendName.setText(name);
            friendName.setVisibility(View.VISIBLE);

            vedioCall.setVisibility(View.VISIBLE);
            voiceCall.setVisibility(View.VISIBLE);

        }

        public void removefriend(String Ref) {

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (dialogRemoved != null || dialogRemoved == null) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

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
                                        .child(Ref).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            databaseRef.child("Friends").child(Ref).child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {

                                                        dialogRemoved.dismiss();
                                                        Snackbar.make(mainBinding.getRoot(), "Deletion was successful", Snackbar.LENGTH_SHORT).show();
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

                    return false;
                }
            });
        }
    }

    private void battery_optimizations() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_removerquest, findViewById(R.id.layoutRemoveRequestContainer));

        builder.setView(dialogView);

        dialogBattery = builder.create();

        if (dialogBattery.getWindow() != null) {

            dialogBattery.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        }

        TextView Title = dialogView.findViewById(R.id.title_dialog);
        TextView Message = dialogView.findViewById(R.id.inputURL);
        ImageView image  = dialogView.findViewById(R.id.imageAddUrl);
        TextView Disable = dialogView.findViewById(R.id.RemoveRquest_dialog);
        TextView Cancel = dialogView.findViewById(R.id.Cancel_dialog);

        image.setImageResource(R.drawable.ic_optimization);
        Title.setText("Warning");
        Message.setText("Battery optimization is enabled. It can interrupt running background services.");
        Disable.setText("Disable");

        Disable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

                    if (!powerManager.isIgnoringBatteryOptimizations(getPackageName())) {

                        Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                        startActivityForResult(intent, REQUEST_CODE_BATTERY_OPTIMIZATIONS);

                    }
                }
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogBattery.dismiss();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

            if (!powerManager.isIgnoringBatteryOptimizations(getPackageName())) {

               dialogBattery.show();
            }
        }

    }

    private void SignOut() {

        HashMap<String, Object> delete = new HashMap<>();
        delete.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());

        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .document(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                .update(delete).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                databaseRef.child("Users").child(PerfrancesManager.getInstance(getApplicationContext()).getUserInformation(Constants.KEY_USER_ID))
                        .child("Token").removeValue();
                PerfrancesManager.getInstance(getBaseContext()).ClearFileShared();
                startActivity(new Intent(MainActivity.this, SignIn.class));
                Toast.makeText(getBaseContext(), "Sign Out...", Toast.LENGTH_SHORT).show();
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(MainActivity.this, "Unable To Sign Out", Toast.LENGTH_SHORT).show();
            }
        });


    }

}