package com.example.ticketpulse;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class MyTicketActivity  extends AppCompatActivity {

    Button displayBtn;
    TextView displayTitle, displayLocation ,hiddenCode;
    ImageView displayTicket;



    String title,location,hiddencode;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_ticket);



        displayBtn = findViewById(R.id.myTicketBtn);
        displayTitle = findViewById(R.id.myTitleTv);
        displayLocation = findViewById(R.id.myLocationTv);
        hiddenCode = findViewById(R.id.hiddenCode);
        displayTicket = findViewById(R.id.myticket_image);



        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        hiddencode = intent.getStringExtra("ticketcode");
        location = intent.getStringExtra("location");

        displayTitle.setText(title);
        hiddenCode.setText(hiddencode);
        displayLocation.setText(location);


        displayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





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



            }

        });












    }
}
