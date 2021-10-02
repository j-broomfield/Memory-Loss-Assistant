package com.example.memorylosscompanion;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class PatientHome extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Vibrator vibrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_home);

        mAuth = FirebaseAuth.getInstance();
        vibrate = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        Button logoutButton = (Button) findViewById(R.id.logout);
        ImageButton notesButton = (ImageButton) findViewById(R.id.notes_btn);
        ImageButton chatButton = (ImageButton) findViewById(R.id.chat_btn);
        ImageButton contactsButton = (ImageButton) findViewById(R.id.contacts_btn);


        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrate.vibrate(50);
                mAuth.signOut();

                Intent loginIntent = new Intent(PatientHome.this, MainActivity.class);
                startActivity(loginIntent);
            }
        });

        notesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrate.vibrate(50);
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.keep");
                if (launchIntent != null) {
                    startActivity(launchIntent);
                } else {
                    Uri marketUri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.keep&hl=en_GB");
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
                    startActivity(marketIntent);
                }

            }
        });

        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrate.vibrate(50);
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.contacts");
                if (launchIntent != null) {
                    startActivity(launchIntent);
                }

            }
        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrate.vibrate(50);
                Intent ChatIntent = new Intent(PatientHome.this, MessageActivity.class);
                startActivity(ChatIntent);

            }
        });


    }


}
