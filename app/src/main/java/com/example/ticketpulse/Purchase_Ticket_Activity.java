package com.example.ticketpulse;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Random;

public class Purchase_Ticket_Activity  extends AppCompatActivity {
    private static final String TAG = "resell ticket";


    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    Button displayBtn;
    TextView pTitle, pDescription, pLocation, pDate, hiddenCode;

    ImageView displayTicket;



    String title,location,hiddencode,description,date;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purhase_ticket);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();



        displayBtn = findViewById(R.id.pTicketBtn);
        pTitle = findViewById(R.id.pTitleTv);
        pLocation = findViewById(R.id.myLocationTv);
        hiddenCode = findViewById(R.id.hiddenCode);
        displayTicket = findViewById(R.id.pticket_image);
        pDescription = findViewById(R.id.pDescriptionTv);
        pDate = findViewById(R.id.pDateTv);



        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        hiddencode = intent.getStringExtra("ticketcode");
        location = intent.getStringExtra("location");
        description = intent.getStringExtra("description");
        date = intent.getStringExtra("date");

        pTitle.setText(title);
        hiddenCode.setText(hiddencode);

        pDescription.setText(description);
        pDate.setText(date);

        displayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    showDeleteDataDialog();



                hiddenCode.setText(getRandomString(16));

                String data = hiddenCode.getText().toString();



                if(data != null){
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    try {


                        BitMatrix bitMatrix = multiFormatWriter.encode(data, BarcodeFormat.QR_CODE,500,500);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        displayTicket.setImageBitmap(bitmap);

                    }catch (WriterException e){
                        e.printStackTrace();
                    }

                }



                Log.d(TAG, "onClick: Attempting to add object to database.");
                String ticket = hiddenCode.getText().toString();
                if(!ticket.equals("")){
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userID = user.getUid();
                    String email = user.getEmail();
                    mRef.child("Users").child(userID).child("ticket1").setValue(ticket);
                    mRef.child("Tickets").child(ticket).child("ticketcode").setValue(ticket);
                    mRef.child("Tickets").child(ticket).child("User").setValue(userID);
                    mRef.child("Tickets").child(ticket).child("email").setValue(email);
                    mRef.child("Tickets").child(ticket).child("title").setValue(title);
                    mRef.child("Tickets").child(ticket).child("description").setValue(description);
                    mRef.child("Tickets").child(ticket).child("date").setValue(date);


                    mRef.child("Tickets").child(ticket).child("ticketName").setValue("1234");



                    toastMessage("Adding " + ticket + " to database...");
                    //reset the text
                    hiddenCode.setText("");
                }


            }

        });












    }
    private static String getRandomString(int i ){
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder results = new StringBuilder();
        while (i > 0) {

            Random rand = new Random();
            results.append(chars.charAt(rand.nextInt(chars.length())));
            i--;
        }
        return results.toString();

    }




    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }




    private void showDeleteDataDialog() {



        //DatabaseReference deleteticket = FirebaseDatabase.getInstance().getReference()

       DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("TicketsOfSale").orderByChild("ticketcode").equalTo(hiddencode);

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });



    }






}
