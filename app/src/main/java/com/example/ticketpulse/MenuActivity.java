package com.example.ticketpulse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    Button EventBtn;
    Button PulseBtn;
    Button ResaleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_profile);

        EventBtn = findViewById(R.id.events_button);
        PulseBtn = findViewById(R.id.the_pulse_button);
        ResaleBtn = findViewById(R.id.resell_button);

        findViewById(R.id.events_button).setOnClickListener(this);

    }

    private void userEvent(){
        Intent intent = new Intent(MenuActivity.this, EventActivity.class);

        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.events_button:
                userEvent();

                break;
            //case R.id.the_pulse_button:
            //  startActivity(new Intent(this, PulseActivity.class));
            //    break;
            //  case R.id.resell_button:
            //   startActivity(new Intent(this, ResaleActivity.class));
            //     break;

        }

    }
}
