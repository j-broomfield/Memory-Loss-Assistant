package com.example.memorylosscompanion;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MessageActivity extends AppCompatActivity {

    //initiate varibles
    private final int REQ_CODE = 100;
    FirebaseUser mUser;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    ImageButton sendButton;
    ImageButton voiceButton;
    EditText messageText;
    Intent intent;
    MessageAdapter messageAdapter;
    List<Chat> mChat;
    List<User> userInfo;
    RecyclerView recyclerView;
    Vibrator vibrate;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        //assign/implement variables
        sendButton = findViewById(R.id.messageSend);
        voiceButton = findViewById(R.id.voiceBtn);
        messageText = findViewById(R.id.messageText);
        recyclerView = findViewById(R.id.chatRecyclerView);
        recyclerView.setHasFixedSize(true);
        vibrate = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        intent = getIntent();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        final String userId = firebaseUser.getUid();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        userInfo = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Users")

                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            userInfo.add(user);
                        }

                        for (int i=0; i < userInfo.size(); i++ ){

                            System.out.println("idtest "+userInfo.get(i).getUsername());
                            System.out.println("idtest|"+userId+"|");

                            System.out.println("idtest "+i);
                            String idCycle = userInfo.get(i).getId();
                            System.out.println("idCycle|"+idCycle+"|");


                            if (userId.equals(idCycle)){



                                position = i;

                            }
                            System.out.println("idtest "+position);

                        }


                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });





        //send message to database
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference = FirebaseDatabase.getInstance().getReference();
                String messageTextString = messageText.getText().toString();
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                String userId = firebaseUser.getUid();
                User user = userInfo.get(position);

                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("sender", userId);
                hashMap.put("message", messageTextString);
                hashMap.put("uName" ,  user.getUsername());

        reference.child("Chats").push().setValue(hashMap);

            }
        });
        //FirebaseUser firebaseUser = mAuth.getCurrentUser();
        //String userId = firebaseUser.getUid();

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               readMessages(mUser.getUid());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




            //voice to text function
        voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please speak now");
                vibrate.vibrate(50);
                try {
                    startActivityForResult(intent, REQ_CODE);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Sorry your device not supported",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //sending speech result to textview
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE: {

                    ArrayList result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    messageText.setText((CharSequence) result.get(0));

                break;
            }
        }
    }


    private  void readMessages (final String myId){
        mChat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);

                    mChat.add(chat);

                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
