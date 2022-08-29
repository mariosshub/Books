package com.example.books;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private SharedPreferences sharedPreferences;
    private TextView buyButton;
    private ArrayList<Book> books = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        RecyclerView booksRecView = findViewById(R.id.booksRecView);
        booksRecView.setLayoutManager(new LinearLayoutManager(this));
        BooksRecViewAdapter adapter = new BooksRecViewAdapter(this);
        booksRecView.setAdapter(adapter);

        TextView header = findViewById(R.id.header);
        buyButton = findViewById(R.id.buyButton);

        // for displaying users Cart books
        DatabaseReference databaseReference;
        if(sharedPreferences.getBoolean("showCartBooks",false)){
            header.setText(getResources().getString(R.string.your_cart));
            databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            auth = FirebaseAuth.getInstance();
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot shot: snapshot.getChildren()) {
                        if(shot.getKey() != null && auth.getCurrentUser() != null && shot.getKey().equals(auth.getCurrentUser().getUid())){
                            User user = shot.getValue(User.class);
                            if(user == null)
                                return;

                            if(user.getBooksToCart() != null){
                                books = user.getBooksToCart();
                                buyButton.setVisibility(View.VISIBLE);
                            }
                            else{
                                books = new ArrayList<>();
                            }
                        }
                    }
                    adapter.setBooks(books);
                    buyButton.setOnClickListener(v ->
                            startActivity(new Intent(MainActivity.this, PaymentActivity.class)));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        // for displaying all available books
        else if(!sharedPreferences.getBoolean("showCartBooks",false)){
            header.setText(getResources().getString(R.string.available_books));
            databaseReference = FirebaseDatabase.getInstance().getReference("Books");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot bookShot: snapshot.getChildren()) {
                        //check if copies are > 0 only for user
                        if(sharedPreferences.getBoolean("showAdminBooks",false)){
                            books.add(bookShot.getValue(Book.class));
                        }
                        else{
                            Book book = bookShot.getValue(Book.class);
                            if(book != null && book.getCopies() > 0)
                                books.add(book);
                        }
                    }
                    adapter.setBooks(books);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        if(sharedPreferences.getBoolean("showAdminBooks",false)){
            Intent intent = new Intent(this,AdminMenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(this,MenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity(intent);
        }
    }
}