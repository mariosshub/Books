package com.example.books;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PaymentActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference databaseBooksReference, databaseUsersReference;
    private TextView booksTxt,totalPriceTxt;
    private ArrayList<Book> books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        databaseBooksReference = FirebaseDatabase.getInstance().getReference("Books");
        databaseUsersReference = FirebaseDatabase.getInstance().getReference("Users");
        auth = FirebaseAuth.getInstance();
        booksTxt = findViewById(R.id.textViewBooks2);
        totalPriceTxt = findViewById(R.id.textViewTotalPrice2);

        if(auth.getCurrentUser() == null)
            return;

        databaseUsersReference.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user == null)
                    return;

                books = user.getBooksToCart();
                String allBookNames = "" ;
                double totalPrice = 0;
                for (Book book: books) {
                    allBookNames += book.getName() + " x" + book.getAmount() + "\n";
                    totalPrice += book.getPrice() * book.getAmount();
                }
                booksTxt.setText(allBookNames);
                totalPriceTxt.setText(getResources().getString(R.string.price_tag,totalPrice));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void placeOrder(View view){
        for (Book book :books) {
            //check if copies are greater than amount of order
            if(book.getCopies() - book.getAmount() >=0){
                databaseBooksReference.child(book.getId()).child("copies").setValue(book.getCopies() - book.getAmount());
            }
        }
        if(auth.getCurrentUser() == null)
            return;

        databaseUsersReference.child(auth.getCurrentUser().getUid()).child("booksToCart").removeValue();
        Toast.makeText(this,"Order made successfully!",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this,MenuActivity.class));
    }
}