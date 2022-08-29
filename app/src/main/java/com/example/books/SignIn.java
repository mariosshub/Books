package com.example.books;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SignIn extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private MyTTS tts;
    private Button loginBtn, signUpBtn;
    private EditText email,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.editTextSignInEmail);
        password = findViewById(R.id.editTextSignInPass);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        loginBtn = findViewById(R.id.buttonSignInLogin);
        signUpBtn = findViewById(R.id.buttonSignInSignUp);
        FloatingActionButton voiceBtn = findViewById(R.id.floatingVoiceBtn);
        voiceBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Please insert your credentials and say login, or signup to register");
            startActivityForResult(intent,123);
        });

        tts = new MyTTS(this);
    }

    // login button
    public void login(View view){
        if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty()){
            Toast.makeText(SignIn.this,"Please fill the required fields",Toast.LENGTH_SHORT).show();
        }
        else{
            auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(SignIn.this,"login Successful",Toast.LENGTH_SHORT).show();
                        tts.speak("Login Successful");
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot shot: snapshot.getChildren()) {
                                    if(shot.getKey() != null && auth.getCurrentUser() != null && shot.getKey().equals(auth.getCurrentUser().getUid())){
                                        User user = shot.getValue(User.class);
                                        if(user == null)
                                            return;

                                        if(user.isAdmin()){
                                            startActivity(new Intent(SignIn.this, AdminMenuActivity.class));
                                        }
                                        else
                                            startActivity(new Intent(SignIn.this,MenuActivity.class));
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    else{
                        tts.speak("There was an error");
                        if(task.getException() != null)
                            new AlertDialog.Builder(SignIn.this).setTitle("Error").setMessage(task.getException().getLocalizedMessage()).setCancelable(true).show();
                    }
                }
            });
        }
    }

    //signup button
    public void signup(View view){
        startActivity(new Intent(SignIn.this, SignUp.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==123 && resultCode==RESULT_OK && data != null){
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            if(matches != null){
                String voiceMessage = matches.get(0);
                if(voiceMessage.equalsIgnoreCase("log in") || voiceMessage.equalsIgnoreCase("login")){
                    loginBtn.performClick();
                }
                if(voiceMessage.equalsIgnoreCase("sign up") || voiceMessage.equalsIgnoreCase("signup")){
                    signUpBtn.performClick();
                }
            }
        }
    }
}