package com.example.cashtrack;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {
    Context context;
    ArrayList<Balance> List;



    private int selectedPosition = RecyclerView.NO_POSITION;
    public TransactionAdapter(Context context, ArrayList<Balance> list) {
        this.context = context;
        List = list;
    }

    public TransactionAdapter() {

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_transaction,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Balance balance  = List.get(position);
        holder.amount.setText(String.valueOf(balance.getAmount()));
        holder.date.setText(balance.getDate());
        holder.time.setText(balance.getTime());
        holder.balanceType.setText((CharSequence) balance.getBalanceType());
        holder.txt_options.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context,holder.txt_options);
            popupMenu.inflate(R.menu.options_menu);

            popupMenu.setOnMenuItemClickListener(item->
            {
                switch ((item.getItemId())){
                    case R.id.menu_edit:
                        Balance balance1=List.get(position); // Set the selected position when the user clicks on the "Edit" option
                        Intent intent;
                        if (balance1.getAmount() > 0) {
                            intent = new Intent(context, EditIncomeTransaction.class);

                            // Add any other necessary data here
                        } else {
                            intent = new Intent(context, EditExpenseTransaction.class);

                            // Add any other necessary data here
                        }
                        intent.putExtra("amount", balance.getAmount());
                        intent.putExtra("date", balance.getDate());
                        intent.putExtra("time", balance.getTime());
                        intent.putExtra("position",List.size()-1-position);

                        context.startActivity(intent);
                        break;



                    case R.id.menu_Delete:
                        // ... (Your existing AlertDialog.Builder code) ...

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Delete Transaction");
                        builder.setMessage("Are you sure you want to delete this transaction?");
                        builder.setPositiveButton("Yes", (dialog, which) -> {
                            // Get the selected Balance object to retrieve its index position
                            Balance selectedBalance = List.get(position);
                            int indexToDelete = position;

                            double amount = selectedBalance.getAmount();
                            boolean isIncome = amount > 0;

                            // Remove the item at the specified index from the List
                            List.remove(indexToDelete);

                            Collections.reverse(List);

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String currentUsername = user.getDisplayName();

                            // Update the entire balanceList in the Firebase Realtime Database
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUsername).child("balancesList");
                            databaseReference.setValue(List).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Removal from the Firebase Realtime Database is successful
                                    // Now update the total_balance, total_income, and total_expense values

                                    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(currentUsername);
                                    userReference.child("total_balance").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Double totalBalance = dataSnapshot.getValue(Double.class);
                                            if (totalBalance != null) {
                                                // Calculate the updated total_balance
                                                double updatedTotalBalance = totalBalance - amount;
                                                // Set the updated total_balance value in the database
                                                userReference.child("total_balance").setValue(updatedTotalBalance);
                                            }

                                            if (isIncome) {
                                                userReference.child("total_income").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        Double totalIncome = dataSnapshot.getValue(Double.class);
                                                        if (totalIncome != null) {
                                                            // Calculate the updated total_income
                                                            double updatedTotalIncome = totalIncome - amount;
                                                            // Set the updated total_income value in the database
                                                            userReference.child("total_income").setValue(updatedTotalIncome);
                                                        }

                                                        // Now notify the RecyclerView about the change
                                                        notifyItemRemoved(position);
                                                        notifyItemRangeChanged(position, List.size());
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        // Handle any errors that occur during the data retrieval process
                                                    }
                                                });
                                            } else {
                                                userReference.child("total_expense").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        Double totalExpense = dataSnapshot.getValue(Double.class);
                                                        if (totalExpense != null) {
                                                            // Calculate the updated total_expense
                                                            double updatedTotalExpense = totalExpense + amount;
                                                            // Set the updated total_expense value in the database
                                                            userReference.child("total_expense").setValue(updatedTotalExpense);
                                                        }

                                                        // Now notify the RecyclerView about the change
                                                        notifyItemRemoved(position);
                                                        notifyItemRangeChanged(position, List.size());
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        // Handle any errors that occur during the data retrieval process
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            // Handle any errors that occur during the data retrieval process
                                        }
                                    });
                                } else {
                                    // Handle any errors during the update
                                }
                            });

                        });
                        builder.setNegativeButton("No", (dialog, which) -> {
                            // Do nothing if the user cancels the deletion
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        break;
                }
                return false;
            });
            popupMenu.show();

        });

        double amount = balance.getAmount();
        if (amount >= 0) {
            holder.amount.setTextColor(0xFF00FF00); // Green color code (0xFF00FF00)
        } else {
            holder.amount.setTextColor(0xFFFF0000); // Red color code (0xFFFF0000)
        }


    }

    @Override
    public int getItemCount() {
        return List.size();
    }

    public static class  MyViewHolder extends RecyclerView.ViewHolder{

        TextView  amount,date,time,balanceType,txt_options;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            balanceType=itemView.findViewById(R.id.textViewBalanceType);
            amount=itemView.findViewById(R.id.textViewAmount);
            date=itemView.findViewById(R.id.textViewDate);
            time=itemView.findViewById(R.id.textViewTime);
            txt_options=itemView.findViewById(R.id.txt_option);

        }
    }
}
