package com.example.ticketpulse;



import android.content.Intent;
import android.graphics.Bitmap;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;



import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;



public class TicketSelectionActivity extends AppCompatActivity {

    TextView mTitleTv, mDetailTv, mLocationTv, mDateTv;
    ImageView mImageIv;



    Button mTicket1Btn, mTicket2Btn, mTicket3Btn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_selection);

        //Action bar
        ActionBar actionBar = getSupportActionBar();
        //Actionbar title
        actionBar.setTitle("Ticket Selection");
        //set back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //initialize views
        mTitleTv = findViewById(R.id.titleTv);
        mDetailTv = findViewById(R.id.descriptionTv);
        mImageIv = findViewById(R.id.imageView);
        mLocationTv = findViewById(R.id.locationTv);
        mDateTv = findViewById(R.id.dateTv);
        mTicket1Btn = findViewById(R.id.ticket1Btn);
        mTicket2Btn = findViewById(R.id.ticket2Btn);
        mTicket3Btn = findViewById(R.id.ticket3Btn);

        //get data from intent
        String image = getIntent().getStringExtra("image");
        String title = getIntent().getStringExtra("title");
        String desc = getIntent().getStringExtra("description");
        String location = getIntent().getStringExtra("location");
        final String date = getIntent().getStringExtra("date");

        //set data to views
        mTitleTv.setText(title);
        mDetailTv.setText(desc);
        mLocationTv.setText(location);
        mDateTv.setText(date);
        Picasso.get().load(image).into(mImageIv);

        //save btn click handle
        mTicket1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = mTitleTv.getText().toString();
                String desc = mDetailTv.getText().toString();
                String location = mLocationTv.getText().toString();
                String date = mDateTv.getText().toString();



                Intent intent = new Intent(TicketSelectionActivity.this, Ticket1Activity.class);
                intent.putExtra("title", title); // put title
                intent.putExtra("description", desc); //put description
               // intent.putExtra("image", mImage); //put image url
                intent.putExtra("location", location);
                intent.putExtra("date", date);
                startActivity(intent);





            }
        });

        mTicket2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = mTitleTv.getText().toString();
                String desc = mDetailTv.getText().toString();
                String location = mLocationTv.getText().toString();
                String date = mDateTv.getText().toString();



                Intent intent = new Intent(TicketSelectionActivity.this, Ticket2Activity.class);
                intent.putExtra("title", title); // put title
                intent.putExtra("description", desc); //put description
                // intent.putExtra("image", mImage); //put image url
                intent.putExtra("location", location);
                intent.putExtra("date", date);
                startActivity(intent);

            }
        });

        mTicket3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = mTitleTv.getText().toString();
                String desc = mDetailTv.getText().toString();
                String location = mLocationTv.getText().toString();
                String date = mDateTv.getText().toString();



                Intent intent = new Intent(TicketSelectionActivity.this, Ticket3Activity.class);
                intent.putExtra("title", title); // put title
                intent.putExtra("description", desc); //put description
                // intent.putExtra("image", mImage); //put image url
                intent.putExtra("location", location);
                intent.putExtra("date", date);
                startActivity(intent);

            }
        });

    }





}