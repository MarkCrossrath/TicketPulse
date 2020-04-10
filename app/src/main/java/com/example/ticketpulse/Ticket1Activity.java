package com.example.ticketpulse;

import android.app.Activity;
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
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import org.json.JSONException;
import java.math.BigDecimal;
import java.util.Random;

public class Ticket1Activity extends AppCompatActivity {


    private static final String TAG = "AddToDatabase";

    Button buttonBtn;
    ImageView ticketImageIV;

    TextView t1Title,t1Desc,t1Location,t1Date;
    TextView random;
    TextView ticketPrice;
    //firebase


    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

   String title,desc,date,location;
    private String paymentAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_1);
        this.setTitle("Purchase Ticket");
       mAuth = FirebaseAuth.getInstance();
       mFirebaseDatabase = FirebaseDatabase.getInstance();
       mRef = mFirebaseDatabase.getReference();


        Intent i = new Intent(this, PayPalService.class);
        i.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(i);


        buttonBtn = findViewById(R.id.ticket_purchase);
        ticketImageIV = findViewById(R.id.ticket_image);
        random= findViewById(R.id.random);

        t1Title = findViewById(R.id.t1TitleTv);
        t1Desc = findViewById(R.id.t1DescriptionTv);
        t1Location = findViewById(R.id.t1LocationTv);
        t1Date = findViewById(R.id.t1DateTv);
        ticketPrice = findViewById(R.id.ticketPrice);

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

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");

                }
                // ...
            }
        };

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Object value = dataSnapshot.getValue();
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });





        buttonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPayment();
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
                    mRef.child("Users").child(userID).child("ticket1").setValue(ticket);
                    mRef.child("Tickets").child(ticket).child("ticketcode").setValue(ticket);
                    mRef.child("Tickets").child(ticket).child("User").setValue(userID);
                    mRef.child("Tickets").child(ticket).child("email").setValue(email);
                    mRef.child("Tickets").child(ticket).child("title").setValue(title);
                    mRef.child("Tickets").child(ticket).child("description").setValue(desc);
                    mRef.child("Tickets").child(ticket).child("date").setValue(date);
                    mRef.child("Tickets").child(ticket).child("location").setValue(location);
                    //reset the text
                    random.setText("");
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



    //Paypal intent request code to track onActivityResult method
    public static final int PAYPAL_REQUEST_CODE = 123;


    //Paypal Configuration Object
    private static PayPalConfiguration config = new PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PayPalConfig.PAYPAL_CLIENT_ID);

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }


    private void getPayment() {
        //Getting the amount from editText
        paymentAmount = ticketPrice.getText().toString();

        //Creating a paypalpayment
        PayPalPayment payment = new PayPalPayment(new BigDecimal(paymentAmount), "EUR", "Ticket Price",
                PayPalPayment.PAYMENT_INTENT_SALE);

        //Creating Paypal Payment activity intent
        Intent intent = new Intent(this, PaymentActivity.class);

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        //Puting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result is from paypal
        super.onActivityResult(requestCode , resultCode , data);
        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("paymentExample" , paymentDetails);

                        //Starting a new activity for the payment details and also putting the payment details with intent
                        startActivity(new Intent(this , PaymentConformationActivity.class)
                                .putExtra("PaymentDetails" , paymentDetails)
                                .putExtra("PaymentAmount" , paymentAmount));

                    } catch (JSONException e) {
                        Log.e("paymentExample" , "an extremely unlikely failure occurred: " , e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample" , "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample" , "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }





}
