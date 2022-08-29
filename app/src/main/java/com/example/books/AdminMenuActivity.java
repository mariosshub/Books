package com.example.books;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminMenuActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        auth = FirebaseAuth.getInstance();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
    }

    public void insertBooks(View view){
        startActivity(new Intent(this,InsertBookActivity.class));
    }

    public void displayBooks(View view){
        editor.putBoolean("showAdminBooks", true);
        editor.putBoolean("showCartBooks",false);
        editor.apply();
        startActivity(new Intent(this,MainActivity.class));
    }

    public void logout(View view){
        auth.signOut();
        startActivity(new Intent(this,SignIn.class));
    }
}