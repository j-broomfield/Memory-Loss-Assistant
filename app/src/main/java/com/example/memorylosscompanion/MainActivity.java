package com.example.memorylosscompanion;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private EditText loginEmailTxt;
    private EditText loginPasswordTxt;
    private FirebaseAuth mAuth;
    private Vibrator vibrate;
    EasyLocationProvider easyLocationProvider;
    DatabaseReference reference;
    SensorManager sensorManager;
    String pattern = "MM/dd/yyyy HH:mm:ss";
    final DateFormat df = new SimpleDateFormat(pattern);
    private Sensor accelerometer;
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        vibrate = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        loginEmailTxt = (EditText) findViewById(R.id.login_email);
        loginPasswordTxt = (EditText) findViewById(R.id.login_pass);
        Button loginButton = (Button) findViewById(R.id.login_btn);
        Button createAccountButton = (Button) findViewById(R.id._acc_btn);
        String pattern = "MM/dd/yyyy HH:mm:ss";
        final DateFormat df = new SimpleDateFormat(pattern);
        reference = FirebaseDatabase.getInstance().getReference();
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate.vibrate(50);
                String loginEmail = loginEmailTxt.getText().toString();
                String loginPassword = loginPasswordTxt.getText().toString();

                if ( loginEmail!=null && loginPassword!=null ){

                    mAuth.signInWithEmailAndPassword(loginEmail,loginPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                Toast.makeText(MainActivity.this, "Successful Login",Toast.LENGTH_SHORT).show();
                                Intent mainIntent = new Intent(MainActivity.this, PatientHome.class);
                                startActivity(mainIntent);


                            }else{

                                String errorToast = task.getException().getMessage();
                                Toast.makeText(MainActivity.this, "error : "+errorToast,Toast.LENGTH_LONG).show();


                            }
                        }
                    });
                }

            }
        });

        //Create account button pressed
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate.vibrate(50);
                Intent mainIntent = new Intent(MainActivity.this, CreateAccountActivity.class);
                startActivity(mainIntent);

            }
        });




        easyLocationProvider = new EasyLocationProvider.Builder(MainActivity.this)
                .setInterval(60000)
                .setFastestInterval(2000)
                //setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setListener(new EasyLocationProvider.EasyLocationCallback() {
                    @Override
                    public void onGoogleAPIClient(GoogleApiClient googleApiClient, String message) {
                        Log.e("EasyLocationProvider","onGoogleAPIClient123: "+message);
                    }

                    @Override
                    public void onLocationUpdated(double latitude, double longitude) {
                        Log.e("EasyLocationProvider","onLocationUpdated:: "+ "Latitude: "+latitude+" Longitude: "+longitude);

                        Date currentTime = Calendar.getInstance().getTime();
                        String TimeString = df.format(currentTime);

                        String Long = Double.toString(longitude);
                        String Lat = Double.toString(latitude);
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();

                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("Longitude", Long);
                        hashMap.put("Latitude", Lat);
                        hashMap.put("Time", TimeString);

                        reference.child("Location").child("Loc1").setValue(hashMap);

                    }

                    @Override
                    public void onLocationUpdateRemoved() {
                        Log.e("EasyLocationProvider","onLocationUpdateRemoved");
                    }
                }).build();

        getLifecycle().addObserver(easyLocationProvider);


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // User is signed in
            Intent mainIntent = new Intent(MainActivity.this, PatientHome.class);
            startActivity(mainIntent);
        } else {
            // No user is signed in

            //finish();
        }
        sensorManager.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            mGravity = event.values.clone();
            // Shake detection
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            mAccelLast = mAccelCurrent;

            mAccelCurrent = (float)Math.sqrt(x*x + y*y + z*z);
            float delta = mAccelCurrent - mAccelLast;

            mAccel = mAccel * 0.9f + delta;
            // Make this higher or lower according to how much
            // motion you want to detect
            if(mAccel > 2){

                Log.e("Movement","working");

                Date currentTime = Calendar.getInstance().getTime();
                String TimeString = df.format(currentTime);
                FirebaseUser firebaseUser = mAuth.getCurrentUser();

                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("Movement Detected", "True");
                hashMap.put("Time", TimeString);

                reference.child("Movement").child("Loc1").setValue(hashMap);
                // do something
            }
        }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
