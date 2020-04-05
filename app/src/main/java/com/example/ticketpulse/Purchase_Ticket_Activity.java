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
import androidx.annotation.Nullable;
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
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.Random;

public class Purchase_Ticket_Activity  extends AppCompatActivity {
    private static final String TAG = "resell ticket";


    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    FirebaseAuth mAuth;

    Button displayBtn;
    TextView pTitle, pDescription, pLocation, pDate, hiddenCode, ticketPrice;

    ImageView displayTicket;



    String title,location,hiddencode,description,date;
    private String paymentAmount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purhase_ticket);
        this.setTitle("Purchase Ticket");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        Intent i = new Intent(this, PayPalService.class);
        i.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(i);

        displayBtn = findViewById(R.id.pTicketBtn);
        pTitle = findViewById(R.id.pTitleTv);
        pLocation = findViewById(R.id.myLocationTv);
        hiddenCode = findViewById(R.id.hiddenCode);
        displayTicket = findViewById(R.id.pticket_image);
        pDescription = findViewById(R.id.pDescriptionTv);
        pDate = findViewById(R.id.pDateTv);
        ticketPrice = findViewById(R.id.ticketPrice);



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
                getPayment();


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





       DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("TicketsOfSale").orderByChild("ticketcode").equalTo(hiddencode);

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });



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
