package com.aasoo.freshifybeta.ui.myactivities;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.aasoo.freshifybeta.ui.DashboardActivity;
import com.aasoo.freshifybeta.ui.MyReferralActivity;
import com.aasoo.freshifybeta.ProfileActivity;
import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.model.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment {


    CircleImageView profile_image;
    TextView user_account_no, user_name, edit_profile, dashboard, user_balance,user_balance_txt,my_referral;
    CardView user_layout;

    public MyAccountFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);



        profile_image = view.findViewById(R.id.profile_image);
        user_account_no = view.findViewById(R.id.user_account_no);
        user_name = view.findViewById(R.id.user_name);
        user_balance = view.findViewById(R.id.user_balance);
        dashboard = view.findViewById(R.id.all_transaction);
        edit_profile = view.findViewById(R.id.edit_profile);
        user_layout = view.findViewById(R.id.user_layout);
        user_balance_txt = view.findViewById(R.id.user_balance_txt);
        my_referral = view.findViewById(R.id.my_referral);

        final String mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_info").child(mCurrentUser);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserData user = dataSnapshot.getValue(UserData.class);
                if (user != null) {
                    if (user.getUser_gender().equals("Male")) {
                        Picasso.get().load(user.getUser_image()).placeholder(R.drawable.male_avatar).into(profile_image);
                    } else {
                        Picasso.get().load(user.getUser_image()).placeholder(R.drawable.female_avatar).into(profile_image);
                    }
                    user_name.setText(user.getUser_name());
                    user_account_no.setText(user.getUser_account_no());
                    if(user.getUser_type().equals("user")){
                        user_layout.setVisibility(View.VISIBLE);
                        String balance = user.getUser_balance();
                        int bal = Integer.parseInt(balance);
                        if(bal<0){
                            user_balance_txt.setText("Advance amount");
                            user_balance.setText("₹ "+balance.substring(1));
                        }else {
                            user_balance_txt.setText("Due balance");
                        user_balance.setText("₹ "+balance);
                    }}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                profileIntent.putExtra("user_id",mCurrentUser);
                startActivity(profileIntent);
            }
        });

        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DashboardActivity.class);
                intent.putExtra("user_id",mCurrentUser);
                startActivity(intent);
            }
        });

        my_referral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyReferralActivity.class);
                intent.putExtra("user_id",mCurrentUser);
                intent.putExtra("user_type","user");
                startActivity(intent);
            }
        });

        return view;
    }
}