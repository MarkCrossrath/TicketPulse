package com.example.ticketpulse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminMenuActivity extends AppCompatActivity  {

    Button EventBtn;
    Button PulseBtn;
    Button ResaleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_menu);


        EventBtn = findViewById(R.id.events_button);
        PulseBtn = findViewById(R.id.the_pulse_button);
        ResaleBtn = findViewById(R.id.resell_button);



        EventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMenuActivity.this, AdminEventListActivity.class);

                startActivity(intent);
            }
        });

    }




}
