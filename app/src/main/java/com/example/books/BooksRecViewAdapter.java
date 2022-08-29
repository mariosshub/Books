package com.example.books;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BooksRecViewAdapter extends RecyclerView.Adapter<BooksRecViewAdapter.ViewHolder>{
    private final Context context;
    private DatabaseReference databaseReferenceUsers, databaseReferenceBooks;
    private FirebaseAuth auth;
    private ArrayList<Book> books = new ArrayList<>();
    public BooksRecViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.books_list_item, parent,false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView txtName, txtAuthor, txtPrice, txtCopies, txtDescription, amountTxt, deleteBtn, editBookBtn ,deleteBookBtn;
        private final ImageView image;
        private final CardView parent;
        private final Spinner amountSpinner;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            parent = itemView.findViewById(R.id.parent);
            txtAuthor = itemView.findViewById(R.id.txtAuthor);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtCopies = itemView.findViewById(R.id.txtCopies);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            image = itemView.findViewById(R.id.image);
            amountTxt = itemView.findViewById(R.id.amountTxt);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            editBookBtn = itemView.findViewById(R.id.editBookBtn);
            deleteBookBtn = itemView.findViewById(R.id.deleteBookBtn);
            amountSpinner = itemView.findViewById(R.id.amountSpinner);
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void setBooks(ArrayList<Book> books) {
        this.books = books;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        holder.txtName.setText(books.get(position).getName());
        holder.txtAuthor.append(books.get(position).getAuthor());
        holder.txtPrice.append(String.valueOf(books.get(position).getPrice()));
        Glide.with(context)
                .asBitmap()
                .load(books.get(position).getImgUrl())
                .into(holder.image);
        databaseReferenceBooks = FirebaseDatabase.getInstance().getReference("Books");
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("Users");

        // Cart View
        if(sharedPreferences.getBoolean("showCartBooks", false) && !sharedPreferences.getBoolean("showAdminBooks",false)){
            auth = FirebaseAuth.getInstance();
            holder.amountTxt.setVisibility(View.VISIBLE);
            holder.amountSpinner.setVisibility(View.VISIBLE);
            holder.deleteBtn.setVisibility(View.VISIBLE);

            Integer[] items = new Integer[]{1,2,3,4};
            ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter<Integer>(context,android.R.layout.simple_spinner_item, items);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.amountSpinner.setAdapter(arrayAdapter);

            // set listener for the amount of books selected
            holder.amountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(auth.getCurrentUser() == null){
                        return;
                    }
                    databaseReferenceUsers.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //get selected value from dropdown
                            int amount = (int) parent.getItemAtPosition(position);
                            boolean EnoughCopies = false;
                            User currentUser = snapshot.getValue(User.class);
                            if(currentUser == null)
                                return;

                            ArrayList<Book> booksInCart = currentUser.getBooksToCart();
                            if(booksInCart != null) {
                                for (Book book : booksInCart) {
                                    if (book.getId().equals(books.get(holder.getAbsoluteAdapterPosition()).getId())) {
                                        //check if copies are enough
                                        if(book.getCopies() - amount >= 0){
                                            book.setAmount(amount);
                                            EnoughCopies = true;
                                        }
                                        else{
                                            EnoughCopies = false;
                                            parent.setSelection(0);
                                            Toast.makeText(context,"Not enough copies",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                if (EnoughCopies){
                                    currentUser.setBooksToCart(booksInCart);
                                    databaseReferenceUsers.child(auth.getCurrentUser().getUid()).setValue(currentUser);
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            holder.deleteBtn.setOnClickListener(v -> {
                AlertDialog.Builder alertDialogue = new AlertDialog.Builder(context);
                alertDialogue.setTitle("Are you sure?")
                        .setMessage("You are about to delete this book from your Cart")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(auth.getCurrentUser() == null)
                                    return;

                                databaseReferenceUsers.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        User currentUser = snapshot.getValue(User.class);
                                        if(currentUser == null)
                                            return;

                                        ArrayList<Book> booksInCart = currentUser.getBooksToCart();
                                        if(!booksInCart.isEmpty()){
                                            int indexForDelete = 0;
                                            for (Book book: booksInCart) {
                                                if(book.getId().equals(books.get(holder.getAbsoluteAdapterPosition()).getId())){
                                                    break;
                                                }
                                                indexForDelete += 1;
                                            }
                                            booksInCart.remove(indexForDelete);
                                            currentUser.setBooksToCart(booksInCart);
                                            databaseReferenceUsers.child(auth.getCurrentUser().getUid()).setValue(currentUser);
                                            context.startActivity(new Intent(context,MainActivity.class));
                                            Toast.makeText(context,books.get(holder.getAdapterPosition()).getName() + " Deleted",Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        });
                alertDialogue.show();
            });
        }
        // user all available books
        else if(!sharedPreferences.getBoolean("showCartBooks", false) && !sharedPreferences.getBoolean("showAdminBooks",false)){
            holder.parent.setOnClickListener(v -> {
                Intent intent = new Intent(context, BookActivity.class);
                intent.putExtra("bookId", books.get(holder.getAbsoluteAdapterPosition()).getId());
                context.startActivity(intent);
            });
        }
        // admin all available books
        else if(!sharedPreferences.getBoolean("showCartBooks",false) && sharedPreferences.getBoolean("showAdminBooks",false)){
            holder.txtCopies.setVisibility(View.VISIBLE);
            holder.txtDescription.setVisibility(View.VISIBLE);
            holder.editBookBtn.setVisibility(View.VISIBLE);
            holder.deleteBookBtn.setVisibility(View.VISIBLE);
            holder.txtCopies.append(Integer.toString(books.get(position).getCopies()));
            if(!books.get(position).getDescription().isEmpty())
                holder.txtDescription.append(books.get(position).getDescription());

            holder.editBookBtn.setOnClickListener(v -> {
                Intent intent = new Intent(context,InsertBookActivity.class);
                intent.putExtra("bookId",books.get(holder.getAbsoluteAdapterPosition()).getId());
                context.startActivity(intent);
            });

            holder.deleteBookBtn.setOnClickListener(v -> {
                AlertDialog.Builder alertDialogue = new AlertDialog.Builder(context);
                alertDialogue.setTitle("Are you sure?")
                        .setMessage("You are about to delete this book from the database")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                databaseReferenceBooks.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot shot: snapshot.getChildren()) {
                                            if(shot.getKey() != null && shot.getKey().equals(books.get(holder.getAbsoluteAdapterPosition()).getId())){
                                                databaseReferenceBooks.child(shot.getKey()).removeValue();
                                                context.startActivity(new Intent(context,MainActivity.class));
                                                Toast.makeText(context,"Book Removed !",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                alertDialogue.show();
            });

        }
    }

}
