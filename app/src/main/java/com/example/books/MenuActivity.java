package com.example.books;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        auth = FirebaseAuth.getInstance();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
    }

    public void showBooks(View view){
        editor.putBoolean("showCartBooks", false);
        editor.putBoolean("showAdminBooks",false);
        editor.apply();
        startActivity(new Intent(this,MainActivity.class));
    }

    public void viewCart(View view){
        editor.putBoolean("showCartBooks", true);
        editor.putBoolean("showAdminBooks",false);
        editor.apply();
        startActivity(new Intent(this,MainActivity.class));
    }

    public void logout(View view){
        auth.signOut();
        startActivity(new Intent(this,SignIn.class));
    }
}