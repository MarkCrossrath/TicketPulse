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
    Button chatBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_profile);
        this.setTitle("Menu");

        EventBtn = findViewById(R.id.events_button);
        PulseBtn = findViewById(R.id.myTickets_button);
        ResaleBtn = findViewById(R.id.TFS_button);
        chatBtn = findViewById(R.id.chat_button);

        findViewById(R.id.events_button).setOnClickListener(this);
        findViewById(R.id.myTickets_button).setOnClickListener(this);
        findViewById(R.id.TFS_button).setOnClickListener(this);
        findViewById(R.id.chat_button).setOnClickListener(this);

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
            case R.id.myTickets_button:

                Intent intent = new Intent(MenuActivity.this, WalletActivity.class);

                startActivity(intent);
            break;
              case R.id.TFS_button:
               startActivity(new Intent(this, TicketsForSaleActivity.class));
                break;

            case R.id.chat_button:
                startActivity(new Intent(this, ChatActivity.class));
                break;
        }

    }
}
