package com.aasoo.freshifybeta.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.model.Contact;
import com.aasoo.freshifybeta.model.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private Context mContext;
    private List<Contact> mContacts;

    public ContactsAdapter(Context context, List<Contact> contacts) {
        this.mContext = context;
        this.mContacts = contacts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.contact_list_layout, parent, false);
        return new ContactsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Contact contact = mContacts.get(position);
        if (contact != null) {
            holder.name.setText(contact.getContact_name());
            holder.mobile.setText(contact.getContact_phone());
            holder.email.setText(contact.getContact_email());
            holder.others.setText(contact.getContact_other());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    final String user_id = user.getUid();
                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_info");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UserData userData = dataSnapshot.child(user_id).getValue(UserData.class);
                            if (userData != null) {
                                if (userData.getUser_type().equals("admin")) {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                                    dialog.setTitle("Remove from contacts");
                                    dialog.setMessage("Do you want remove contact from list ?");
                                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            removeContact(contact.getContact_id());
                                        }
                                    });
                                    dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.create();
                                    dialog.show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }
        });
    }


    private void removeContact(String contact_id) {
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setTitle("Removing from contact us");
        dialog.setMessage("please wait ...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("contact_list");
        reference.child(contact_id).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(mContext, "Contact Removed", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("AddContactActivity", "onComplete: " + task.getException());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name, mobile, email, others;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            mobile = itemView.findViewById(R.id.mobile);
            email = itemView.findViewById(R.id.email);
            others = itemView.findViewById(R.id.others);
        }
    }
}
