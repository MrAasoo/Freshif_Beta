package com.aasoo.freshifybeta.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.model.Referral;
import com.aasoo.freshifybeta.model.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReferralAdapter extends RecyclerView.Adapter<ReferralAdapter.ViewHolder> {

    private Context mContext;
    private List<Referral> mReferrals;
    private String mUserType;

    public ReferralAdapter(Context mContext, List<Referral> mReferrals, String mUserType) {
        this.mContext = mContext;
        this.mReferrals = mReferrals;
        this.mUserType = mUserType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.referral_layout, parent, false);
        return new ReferralAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Referral referral = mReferrals.get(position);
        if (referral != null) {
            holder.user_account_no.setText(referral.getReferral_user_id());
            holder.add_on_date.setText(referral.getReferral_date());

            getUserDetails(referral.getReferral_id(), holder.user_image, holder.user_name, holder.user_mobile);
        }

    }

    private void getUserDetails(String user_id, final CircleImageView user_image, final TextView user_name, final TextView user_mobile) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_info").child(user_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserData user = dataSnapshot.getValue(UserData.class);
                if (user != null) {
                    user_name.setText(user.getUser_name());
                    user_mobile.setText(user.getUser_phone());
                    if (user.getUser_gender().equals("Male")) {
                        Picasso.get().load(user.getUser_image()).placeholder(R.drawable.male_avatar).into(user_image);
                    } else {
                        Picasso.get().load(user.getUser_image()).placeholder(R.drawable.female_avatar).into(user_image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return mReferrals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView user_image;
        TextView user_account_no, user_name, user_mobile, add_on_date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            user_image = itemView.findViewById(R.id.user_image);
            user_account_no = itemView.findViewById(R.id.user_account_no);
            user_name = itemView.findViewById(R.id.user_name);
            user_mobile = itemView.findViewById(R.id.user_mobile);
            add_on_date = itemView.findViewById(R.id.add_on_date);
        }
    }
}
