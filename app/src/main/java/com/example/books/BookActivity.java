package com.example.books;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BookActivity extends AppCompatActivity {

    private DatabaseReference databaseBookReference;
    private DatabaseReference databaseUserReference;
    private SharedPreferences.Editor editor;
    private FirebaseAuth auth;
    private TextView txtBookName, txtAuthor, txtDescription, txtPrice, txtCopies;
    private ImageView bookImage;
    private String bookId;
    private Book book;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        databaseBookReference = FirebaseDatabase.getInstance().getReference("Books");
        databaseUserReference = FirebaseDatabase.getInstance().getReference("Users");
        auth = FirebaseAuth.getInstance();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        bookId = getIntent().getStringExtra("bookId");
        initView();
        setData();

        databaseUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot shot: snapshot.getChildren()) {
                    if(shot.getKey() == null || auth.getCurrentUser() == null)
                        return;

                    if(shot.getKey().equals(auth.getCurrentUser().getUid())){
                        user = shot.getValue(User.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void addToCart(View view){
        boolean isBookInCart = false;
        ArrayList<Book> books;
        if(user.getBooksToCart() != null){
            books = user.getBooksToCart();
        }
        else{
            books = new ArrayList<>();
        }
        //check if book is already in cart
        for (Book bookInCart: books) {
            if(bookInCart.getId().equals(book.getId())){
                isBookInCart = true;
                break;
            }
        }
        if(isBookInCart){
            Toast.makeText(this,"Book is already in Cart!",Toast.LENGTH_SHORT).show();
        }
        else{
            books.add(book);
            user.setBooksToCart(books);
            if(auth.getCurrentUser() == null)
                return;

            databaseUserReference.child(auth.getCurrentUser().getUid()).setValue(user);
            Toast.makeText(this,"Book added to cart!",Toast.LENGTH_SHORT).show();
        }
    }

    public void viewCart(View view){
        editor.putBoolean("showCartBooks", true);
        editor.putBoolean("showAdminBooks", false);
        editor.apply();
        startActivity(new Intent(this, MainActivity.class));
    }

    private void initView(){
        txtBookName = findViewById(R.id.textViewBookName2);
        txtPrice = findViewById(R.id.textViewPrice);
        txtAuthor = findViewById(R.id.textViewAuthorName2);
        txtDescription = findViewById(R.id.textViewDescription2);
        txtCopies = findViewById(R.id.textViewCopies);
        bookImage = findViewById(R.id.imageViewBook);
    }

    private void setData(){
        databaseBookReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot shot: snapshot.getChildren()) {

                    if(shot.getKey() != null && shot.getKey().equals(bookId)){
                        book = shot.getValue(Book.class);
                        if(book != null){
                            txtBookName.setText(book.getName());
                            txtAuthor.setText(book.getAuthor());
                            txtDescription.setText(book.getDescription());
                            txtPrice.setText(getResources().getString(R.string.price_tag,book.getPrice()));
                            txtCopies.setText(getResources().getString(R.string.books_left, book.getCopies()));
                            Glide.with(getApplicationContext())
                                    .asBitmap()
                                    .load(book.getImgUrl())
                                    .into(bookImage);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}