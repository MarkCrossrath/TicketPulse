package com.example.ticketpulse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminMenuActivity extends AppCompatActivity  {

    Button EventBtn;
    Button Chatbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_menu);


        EventBtn = findViewById(R.id.events_button);
        Chatbtn = findViewById(R.id.chat_button);



        EventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMenuActivity.this, AdminEventListActivity.class);

                startActivity(intent);
            }
        });


        Chatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMenuActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });

    }




}
