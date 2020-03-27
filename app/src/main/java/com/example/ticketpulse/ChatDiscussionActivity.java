package com.example.ticketpulse;


import android.os.Bundle;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatDiscussionActivity extends AppCompatActivity {

    Button btnSendMsg;
    EditText etMsg;

    ListView lvDiscussion;
    ArrayList<String> listConversation = new ArrayList<>();
    ArrayAdapter<String> arrayAdpt;

    String UserName, SelectedTopic, user_msg_key;

    private DatabaseReference dbr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_discussion);

        btnSendMsg = findViewById(R.id.btnSendMsg);
        etMsg = findViewById(R.id.etMessage);

        lvDiscussion = findViewById(R.id.lvConversation);
        arrayAdpt = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listConversation);
        lvDiscussion.setAdapter(arrayAdpt);


        UserName = getIntent().getExtras().get("user_name").toString();
        SelectedTopic = getIntent().getExtras().get("selected_topic").toString();
        setTitle("Topic : " + SelectedTopic);

        dbr = FirebaseDatabase.getInstance().getReference().child(SelectedTopic);

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> map = new HashMap<>();
                user_msg_key = dbr.push().getKey();
                dbr.updateChildren(map);

                DatabaseReference dbr2 = dbr.child(user_msg_key);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("msg", etMsg.getText().toString());
                map2.put("user", UserName);
                dbr2.updateChildren(map2);

                etMsg.setText("");
            }
        });


        dbr.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                updateConversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                updateConversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    public void updateConversation(DataSnapshot dataSnapshot){
        String msg, user, conversation;
        Iterator<DataSnapshot> i = dataSnapshot.getChildren().iterator();
        while(i.hasNext()){
            msg = (String) (i.next()).getValue();
            user = (String) (i.next()).getValue();

            conversation = user + ": " + msg;
            arrayAdpt.insert(conversation, arrayAdpt.getCount());
            arrayAdpt.notifyDataSetChanged();
        }
    }
}
