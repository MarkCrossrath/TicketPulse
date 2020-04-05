package com.example.ticketpulse;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class TicketsForSaleActivity extends AppCompatActivity {
    private static final String TAG = "Displaying the ticks for sale ";


    LinearLayoutManager mLayoutManager; //for sorting
    SharedPreferences mSharedPref; //for saving sort settings
    RecyclerView mRecyclerView;
    FirebaseDatabase mFirebaseDatabase, dFirebaseDatabase;
    DatabaseReference mRef;
    DatabaseReference dRef;



    FirebaseRecyclerAdapter<TicketsForSale, TicketForSaleViewHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<TicketsForSale> options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tfs_view);
        this.setTitle("Tickets For Sale ");




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
        mRecyclerView = findViewById(R.id.TOSRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        //send Query to FirebaseDatabase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("TicketsOfSale");

        dFirebaseDatabase = FirebaseDatabase.getInstance();
        dRef = dFirebaseDatabase.getReference("TicketsOfSale");

        //tRef = mFirebaseDatabase.getReference("Tickets").child("ticketcode").push().getKey();


        showData();
    }


    private void showDeleteDataDialog(final String currentTicketCode ) {
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(TicketsForSaleActivity.this);
        builder.setTitle("Purchase  this ticket");
        builder.setMessage("Are you sure you want to purchase this ticket?");
        //set positive/yes button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //user pressed "Yes", delete data
                String key = mRef.child("ticketcode").push().getKey();
              //  moveRecord(tRef,dRef);



                Query mQuery = mRef.orderByChild("ticketcode").equalTo(currentTicketCode);
                mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot ds: dataSnapshot.getChildren()){

                            ds.getRef().removeValue(); // remove values from firebase where ticketCode matches
                        }
                        //show message that post(s) deleted
                        Toast.makeText(TicketsForSaleActivity.this, "Ticket deleted successfully....", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //if anything goes wrong get and show error message
                        Toast.makeText(TicketsForSaleActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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




    //show data
    private void showData(){
        options = new FirebaseRecyclerOptions.Builder<TicketsForSale>().setQuery(mRef, TicketsForSale.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<TicketsForSale, TicketForSaleViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TicketForSaleViewHolder holder, int position, @NonNull TicketsForSale model) {
                holder.setDetails(getApplicationContext(), model.getTitle(), model.getEmail() ,model.getLocation(), model.getTicketName(), model.getTicketcode(), model.getDescription(), model.getDate());
            }

            @NonNull
            @Override
            public TicketForSaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //Inflating layout row.xml
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tickets_for_sale , parent, false);

                TicketForSaleViewHolder ticketForSaleviewHolder = new TicketForSaleViewHolder(itemView);
                //item click listener
                ticketForSaleviewHolder.setOnClickListener(new TicketForSaleViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //get data from firebase at the position clicked
                        String mTitle = getItem(position).getTitle();
                        String mEmail = getItem(position).getEmail();
                        String mLocation = getItem(position).getLocation();
                        String mTicketcode= getItem(position).getTicketcode();
                        String mTicketName = getItem(position).getTicketName();
                        String mDescription = getItem(position).getDescription();
                        String mDate = getItem(position).getDescription();



                        //pass this data to new activity
                        Intent intent = new Intent(view.getContext(), Purchase_Ticket_Activity.class);
                        intent.putExtra("title", mTitle); // put title
                        intent.putExtra("email", mEmail); //put description
                        intent.putExtra("ticketname",mTicketName);
                        intent.putExtra("location",mLocation);
                        intent.putExtra("ticketcode",mTicketcode);
                        intent.putExtra("description",mDescription);
                        intent.putExtra("date",mDate);
                        startActivity(intent); //start activity
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        //get current title
                        final String wTitle = getItem(position).getTitle();
                        //get current Email
                        final String wEmail = getItem(position).getEmail();

                        final String cLocation = getItem(position).getLocation();
                        final String cTicketName = getItem(position).getTicketName();
                        final String wTicketcode = getItem(position).getTicketcode();


                        //show dialog on long clcik
                        AlertDialog.Builder builder = new AlertDialog.Builder(TicketsForSaleActivity.this);
                        //options to display in dialog
                        String[] options = {"Sell my Ticket"};
                        //set to dialog
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //handle dialog item clicks
                                if (which == 0) {
                                    showDeleteDataDialog(wTicketcode);
                                }
                            }
                        });
                        builder.create().show(); //show dialog

                    }
                });

                return ticketForSaleviewHolder;
            }
        };

        //set layout as LinearLayout
        mRecyclerView.setLayoutManager(mLayoutManager);
        firebaseRecyclerAdapter.startListening();
        //set adapter to firebase recycler view
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    //search data
    private void firebaseSearch(String searchText) {

        //convert string entered in SearchView to lowercase
        String query = searchText.toLowerCase();

        Query firebaseSearchQuery = mRef.orderByChild("search").startAt(query).endAt(query + "\uf8ff");


        options = new FirebaseRecyclerOptions.Builder<TicketsForSale>().setQuery(firebaseSearchQuery, TicketsForSale.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<TicketsForSale, TicketForSaleViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TicketForSaleViewHolder holder, int position, @NonNull TicketsForSale model) {
                holder.setDetails(getApplicationContext(), model.getTitle(), model.getEmail(), model.getLocation(), model.getTicketName(),model.getTicketcode(), model.getDescription(), model.getDate());
            }

            @NonNull
            @Override
            public TicketForSaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //Inflating layout row.xml
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_wallet, parent, false);

                TicketForSaleViewHolder ticketForSaleviewHolder = new TicketForSaleViewHolder(itemView);
                //item click listener
                ticketForSaleviewHolder.setOnClickListener(new TicketForSaleViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String mTitle = getItem(position).getTitle();
                        String mEmail = getItem(position).getEmail();
                        String mLocation = getItem(position).getLocation();
                        String mTicketcode= getItem(position).getTicketcode();
                        String mTicketName = getItem(position).getTicketName();
                        String mDescription = getItem(position).getDescription();
                        String mDate = getItem(position).getDescription();



                        //pass this data to new activity
                        Intent intent = new Intent(view.getContext(), Purchase_Ticket_Activity.class);
                        intent.putExtra("title", mTitle); // put title
                        intent.putExtra("email", mEmail); //put description
                        intent.putExtra("ticketname",mTicketName);
                        intent.putExtra("location",mLocation);
                        intent.putExtra("ticketcode",mTicketcode);
                        intent.putExtra("description",mDescription);
                        intent.putExtra("date",mDate);
                        startActivity(intent); //start activity
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        String mTitle = getItem(position).getTitle();
                        String mEmail = getItem(position).getEmail();
                        String mLocation = getItem(position).getLocation();
                        String mTicketcode= getItem(position).getTicketcode();
                        String mTicketName = getItem(position).getTicketName();
                        String mDescription = getItem(position).getDescription();
                        String mDate = getItem(position).getDescription();



                        //pass this data to new activity
                        Intent intent = new Intent(view.getContext(), Purchase_Ticket_Activity.class);
                        intent.putExtra("title", mTitle); // put title
                        intent.putExtra("email", mEmail); //put description
                        intent.putExtra("ticketname",mTicketName);
                        intent.putExtra("location",mLocation);
                        intent.putExtra("ticketcode",mTicketcode);
                        intent.putExtra("description",mDescription);
                        intent.putExtra("date",mDate);
                        startActivity(intent); //start activity
                    }
                });

                return ticketForSaleviewHolder;
            }
        };

        //set layout as LinearLayout
        mRecyclerView.setLayoutManager(mLayoutManager);
        firebaseRecyclerAdapter.startListening();
        //set adapter to firebase recycler view
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    //load data into recycler view onStart
    @Override
    protected void onStart() {
        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
        if (firebaseRecyclerAdapter !=null){
            firebaseRecyclerAdapter.startListening();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //handle other action bar item clicks here
        if (id == R.id.action_sort) {
            //display alert dialog to choose sorting
            showSortDialog();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void showSortDialog() {
        //options to display in dialog
        String[] sortOptions = {" Newest", " Oldest"};
        //create alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort by") //set title
                .setIcon(R.drawable.ic_action_sort) //set icon
                .setItems(sortOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position of the selected item
                        // 0 means "Newest" and 1 means "oldest"
                        if (which == 0) {
                            //sort by newest
                            //Edit our shared preferences
                            SharedPreferences.Editor editor = mSharedPref.edit();
                            editor.putString("Sort", "newest"); //where 'Sort' is key & 'newest' is value
                            editor.apply(); // apply/save the value in our shared preferences
                            recreate(); //restart activity to take effect
                        } else if (which == 1) {
                            {
                                //sort by oldest
                                //Edit our shared preferences
                                SharedPreferences.Editor editor = mSharedPref.edit();
                                editor.putString("Sort", "oldest"); //where 'Sort' is key & 'oldest' is value
                                editor.apply(); // apply/save the value in our shared preferences
                                recreate(); //restart activity to take effect
                            }
                        }
                    }
                });
        builder.show();
    }




    private void moveRecord(DatabaseReference fromPath, final DatabaseReference toPath) {

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()) {
                            Log.d(TAG, "Success!");
                        } else {
                            Log.d(TAG, "Copy failed!");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        fromPath.addListenerForSingleValueEvent(valueEventListener);
    }



    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }


}




