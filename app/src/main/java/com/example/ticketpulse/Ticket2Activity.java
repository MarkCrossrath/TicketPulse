package com.example.ticketpulse;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Random;

public class Ticket2Activity extends AppCompatActivity {


    private static final String TAG = "AddToDatabase";

    Button buttonBtn;
    ImageView ticketImageIV;
    TextView code;
    TextView t1Title,t1Desc,t1Location,t1Date;
    TextView random;
    //firebase
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    String title,desc,date,location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_1);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();




        buttonBtn = findViewById(R.id.ticket_purchase);
        ticketImageIV = findViewById(R.id.ticket_image);
        random= findViewById(R.id.random);
        code = findViewById(R.id.ticketPrice);
        t1Title = findViewById(R.id.t1TitleTv);
        t1Desc = findViewById(R.id.t1DescriptionTv);
        t1Location = findViewById(R.id.t1LocationTv);
        t1Date = findViewById(R.id.t1DateTv);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        desc = intent.getStringExtra("description");
        date = intent.getStringExtra("date");
        location = intent.getStringExtra("location");

        t1Title.setText(title);
        t1Date.setText(date);
        t1Desc.setText(desc);
        t1Location.setText(location);







        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Successfully signed out.");
                }
                // ...
            }
        };

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Object value = dataSnapshot.getValue();
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });





        buttonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent i=new Intent(Ticket2Activity.this,EventActivity.class);
                        startActivity(i);
                    }
                }, 3000);

                random.setText(getRandomString(16));


                String data = random.getText().toString();



                if(data != null){
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    try {


                        BitMatrix bitMatrix = multiFormatWriter.encode(data, BarcodeFormat.QR_CODE,500,500);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        ticketImageIV.setImageBitmap(bitmap);

                    }catch (WriterException e){
                        e.printStackTrace();
                    }

                }



                Log.d(TAG, "onClick: Attempting to add object to database.");
                String ticket = random.getText().toString();
                if(!ticket.equals("")){
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userID = user.getUid();
                    String email = user.getEmail();
                    mRef.child("Users").child(userID).child("ticket2").setValue(ticket);
                    mRef.child("Tickets").child(ticket).setValue(userID);
                    mRef.child("Tickets").child(ticket).setValue(email);
                    toastMessage("Adding " + ticket + " to database...");
                    //reset the text
                    random.setText("");
                }


            }

        });

    }

    private static String getRandomString(int i ){
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmonpqrstuvwxyz0123456789";
        StringBuilder results = new StringBuilder();
        while (i > 0) {

            Random rand = new Random();
            results.append(chars.charAt(rand.nextInt(chars.length())));
            i--;
        }
        return results.toString();
    }




    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }


}
