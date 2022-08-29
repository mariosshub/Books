package com.example.books;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private MyTTS tts;
    private EditText firstName, lastName, email, password;
    private CheckBox checkBoxAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        firstName = findViewById(R.id.editTextFirstName);
        lastName = findViewById(R.id.editTextLastName);
        email = findViewById(R.id.editTextSignUpEmail);
        password = findViewById(R.id.editTextSignUpPassword);
        checkBoxAdmin = findViewById(R.id.checkBoxAdmin);

        tts = new MyTTS(this);
    }


    public void signup(View view){

        auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    if(auth.getCurrentUser() != null){
                        databaseReference.child(auth.getCurrentUser().getUid()).setValue(new User(firstName.getText().toString(), lastName.getText().toString(),checkBoxAdmin.isChecked(), new ArrayList<>()));
                        Toast.makeText(SignUp.this,"Registration Successful", Toast.LENGTH_LONG).show();
                        tts.speak("Registration Successful");
                        startActivity(new Intent(SignUp.this, SignIn.class));
                    }
                }else {
                    if(task.getException() != null)
                        new AlertDialog.Builder(SignUp.this).setTitle("Error").setMessage(task.getException().getLocalizedMessage()).setCancelable(true).show();
                }
            }
        });
    }
}