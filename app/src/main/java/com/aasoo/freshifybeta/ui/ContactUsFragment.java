package com.aasoo.freshifybeta.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aasoo.freshifybeta.AddContactActivity;
import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.adapter.ContactsAdapter;
import com.aasoo.freshifybeta.model.Contact;
import com.aasoo.freshifybeta.model.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactUsFragment extends Fragment {
    TextView add_contact;
    private RecyclerView recyclerView;
    private ContactsAdapter adapter;
    private List<Contact> mContact;

    public ContactUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        add_contact = view.findViewById(R.id.txt_add_contact);

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
                            add_contact.setVisibility(View.VISIBLE);
                        } else {
                            add_contact.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddContactActivity.class));
            }
        });
        recyclerView = view.findViewById(R.id.contact_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mContact = new ArrayList<>();
        adapter = new ContactsAdapter(getContext(), mContact);
        recyclerView.setAdapter(adapter);

        readContacts();

        return view;
    }

    private void readContacts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("contact_list");
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mContact.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Contact contact = snapshot.getValue(Contact.class);
                    if (contact != null) {
                        mContact.add(contact);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
