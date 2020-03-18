package com.example.ticketpulse;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class WalletActivity extends AppCompatActivity {
    private static final String TAG = "listview in for wallet ";


    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    LinearLayoutManager mLayoutManager; //for sorting
    SharedPreferences mSharedPref;
    RecyclerView mRecyclerView;

    FirebaseRecyclerAdapter<Wallet, WalletViewHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Wallet> options;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_listview);



        //Actionbar
        ActionBar actionBar = getSupportActionBar();
        //set title
        mSharedPref = getSharedPreferences("SortSettings", MODE_PRIVATE);
        String mSorting = mSharedPref.getString("Sort", "newest"); //where if no settingsis selected newest will be default

        if (mSorting.equals("newest")) {
            mLayoutManager = new LinearLayoutManager(this);
            //this will load the items from bottom means newest first
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
        } else if (mSorting.equals("oldest")) {
            mLayoutManager = new LinearLayoutManager(this);
            //this will load the items from bottom means oldest first
            mLayoutManager.setReverseLayout(false);
            mLayoutManager.setStackFromEnd(false);
        }

        //RecyclerView
        mRecyclerView = findViewById(R.id.walletRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        //send Query to FirebaseDatabase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Data").child("Tickets");


        showData();




    }

    private void showDeleteDataDialog(final String currentTitle, final String currentEmail) {
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(WalletActivity.this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure to delete this post?");
        //set positive/yes button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //user pressed "Yes", delete data


                Query mQuery = mRef.orderByChild("title").equalTo(currentTitle);
                mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ds.getRef().removeValue(); // remove values from firebase where title matches
                        }
                        //show message that post(s) deleted
                        Toast.makeText(WalletActivity.this, "Post deleted successfully...", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //if anything goes wron get and show error message
                        Toast.makeText(WalletActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                //delete image using reference of url from FirebaseStorage
                StorageReference mPictureRefe = getInstance().getReferenceFromUrl(currentEmail);
                mPictureRefe.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //delete successfully
                        Toast.makeText(WalletActivity.this, "Image deleted successfully...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //unable to delete
                        //if anything goes wrong while deleting image, get and show error message
                        Toast.makeText(WalletActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
        //set negative/no button
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //user pressed "No", just dismiss dialog
                dialog.dismiss();
            }
        });
        //show dialog
        builder.create().show();
    }



    private void showData(){
        options = new FirebaseRecyclerOptions.Builder<Wallet>().setQuery(mRef, Wallet.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Wallet, WalletViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull WalletViewHolder holder, int position, @NonNull Wallet model) {
                holder.setDetails(getApplicationContext(), model.getTitle(), model.getEmail(), model.getLocation(),model.getTicketName());
            }

            @NonNull
            @Override
            public WalletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //Inflating layout my_wallet.xml
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_wallet, parent, false);

               WalletViewHolder  walletViewHolder = new WalletViewHolder(itemView);
            //
                //item click listener
                walletViewHolder.setOnClickListener(new WalletViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //get data from firebase at the position clicked
                        String wTitle = getItem(position).getTitle();
                        String wEmail = getItem(position).getEmail();
                        String wLocation = getItem(position).getLocation();
                        String wTicketName = getItem(position).getTicketName();


                        //pass this data to new activity
                        Intent intent = new Intent(view.getContext(), TicketSelectionActivity.class);
                        intent.putExtra("title", wTitle); // put title
                        intent.putExtra("email", wEmail); //put description
                        intent.putExtra("location",wLocation);
                        intent.putExtra("ticketName",wTicketName);

                        startActivity(intent); //start activity
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        //get current title
                        final String cTitle = getItem(position).getTitle();
                        //get current description
                        final String cEmail = getItem(position).getEmail();

                        final String cLocation = getItem(position).getLocation();

                        final String cTicketName = getItem(position).getTicketName();


                        //show dialog on long clcik
                        AlertDialog.Builder builder = new AlertDialog.Builder(WalletActivity.this);
                        //options to display in dialog
                        String[] options = {" Update", " Delete"};
                        //set to dialog
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //handle dialog item clicks
                                if (which == 0){
                                    //update clicked
                                    //start activity with putting current data
                                  //  Intent intent = new Intent(WalletActivity.this, CreateEventActivity.class);
                                 // intent.putExtra("cTitle", cTitle);
                                  //  intent.putExtra("cemail", cEmail);
                                 //  intent.putExtra("cLocation", cLocation);
                                   // intent.putExtra("cTicketName", cTicketName);

                                  //  startActivity(intent);
                                }
                                if (which == 1){
                                    //delete clicked
                                    //method call
                                    showDeleteDataDialog(cTitle, cEmail);
                                }
                            }
                        });
                        builder.create().show(); //show dialog

                    }
                });

                return walletViewHolder;
            }
        };

        //set layout as LinearLayout
        mRecyclerView.setLayoutManager(mLayoutManager);
        firebaseRecyclerAdapter.startListening();
        //set adapter to firebase recycler view
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }



    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
