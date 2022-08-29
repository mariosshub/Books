package com.example.books;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InsertBookActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private EditText bookName ,author ,price ,img ,description, copies;
    private String bookId;
    private Book book;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_book);
        bookName = findViewById(R.id.editTextBookName);
        author = findViewById(R.id.editTextAuthorsName);
        price = findViewById(R.id.editTextBookPrice);
        img = findViewById(R.id.editTextBookImg);
        description = findViewById(R.id.editTextBookDescr);
        copies = findViewById(R.id.editTextBookCopies);
        databaseReference = FirebaseDatabase.getInstance().getReference("Books");
        bookId = getIntent().getStringExtra("bookId");
        if(bookId != null){
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot shot: snapshot.getChildren()) {
                        if (shot.getKey()!= null && shot.getKey().equals(bookId)) {
                            book = shot.getValue(Book.class);
                            if(book == null)
                                return;

                            bookName.setText(book.getName());
                            author.setText(book.getAuthor());
                            description.setText(book.getDescription());
                            price.setText(String.valueOf(book.getPrice()));
                            copies.setText(String.valueOf(book.getCopies()));
                            img.setText(book.getImgUrl());
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    public void addBook(View view){
        if(bookName.getText().toString().isEmpty() || author.getText().toString().isEmpty() || price.getText().toString().isEmpty()
            || img.getText().toString().isEmpty() || copies.getText().toString().isEmpty()){
            Toast.makeText(this,"Fill all fields",Toast.LENGTH_SHORT).show();
        }
        else{
            if(bookId != null){
                databaseReference.child(bookId).setValue(new Book(bookId,bookName.getText().toString(),author.getText().toString(),
                        Double.parseDouble(price.getText().toString()),img.getText().toString(),description.getText().toString(),Integer.parseInt(copies.getText().toString()),1));
            }
            else{
                DatabaseReference pushBook = databaseReference.push();
                pushBook.setValue(new Book(pushBook.getKey(),bookName.getText().toString(),author.getText().toString(),
                        Double.parseDouble(price.getText().toString()),img.getText().toString(),description.getText().toString(),Integer.parseInt(copies.getText().toString()),1));
            }
            Toast.makeText(this,"Book Added Successfully!",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,AdminMenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}