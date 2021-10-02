package com.example.memorylosscompanion;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText createEmail;
    private EditText createPassword;
    private EditText createUsername;
    private Button createAccButton;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mAuth = FirebaseAuth.getInstance();
            //Implement/fill variables
        createEmail = (EditText) findViewById(R.id.add_email);
        createUsername = (EditText) findViewById(R.id.add_username);
        createPassword = (EditText) findViewById(R.id.add_password);
        createAccButton = (Button) findViewById(R.id.add_account_button);

            //check for create account button press
        createAccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = createEmail.getText().toString();
                String password = createPassword.getText().toString();

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                            //account added successfully
                        if (task.isSuccessful()){

                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            String userId = firebaseUser.getUid();
                            String username = createUsername.getText().toString();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id",userId);
                            hashMap.put("username", username);
                            hashMap.put("imageURL", "default");

                            reference.setValue(hashMap);



                            Toast.makeText(CreateAccountActivity.this, "Account successfully  created. Please Login",Toast.LENGTH_LONG).show();
                            Intent createAccIntent = new Intent(CreateAccountActivity.this, MainActivity.class);
                            startActivity(createAccIntent);
                            finish();
                        }else {

                            String toastErrorMessage = task.getException().getMessage();
                            Toast.makeText(CreateAccountActivity.this, "Error : " + toastErrorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }
}
