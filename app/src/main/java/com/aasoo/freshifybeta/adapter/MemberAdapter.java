package com.aasoo.freshifybeta.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aasoo.freshifybeta.MemberDetailActivity;
import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.model.UserData;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
    private Context mContext;
    private List<UserData> mUserData;

    public MemberAdapter(Context mContext, List<UserData> mUserData) {
        this.mContext = mContext;
        this.mUserData = mUserData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_list_view, parent, false);
        return new MemberAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final UserData userData = mUserData.get(position);
        if (userData != null) {
            holder.user_account_no.setText(userData.getUser_account_no());
            holder.user_name.setText(userData.getUser_name());
            holder.user_mobile.setText(userData.getUser_phone());
            String imageUrl = userData.getUser_image();
            String gen = userData.getUser_gender();
            if (userData.getUser_type().equals("user")) {
                String dueAmount = userData.getUser_balance();
                if (Integer.parseInt(dueAmount) != 0) {
                    holder.due_amount_txt.setVisibility(View.VISIBLE);
                    holder.due_amount.setVisibility(View.VISIBLE);
                    if (Integer.parseInt(dueAmount) < 0) {
                        String show = dueAmount;
                        holder.due_amount_txt.setText("Advance balance");
                        holder.due_amount.setText("₹ " + show.substring(1));
                    } else {
                        holder.due_amount_txt.setText("Due Balance");
                        holder.due_amount.setText("₹ " + dueAmount);
                    }
                }else{
                    holder.due_amount_txt.setVisibility(View.GONE);
                    holder.due_amount.setVisibility(View.GONE);
                }
            }

            if (gen.equals("Male")) {
                if (imageUrl.equals("default")) {
                    holder.user_image.setImageResource(R.drawable.male_avatar);
                } else {
                    Picasso.get().load(imageUrl).placeholder(R.drawable.male_avatar).into(holder.user_image);
                }
            } else {
                if (imageUrl.equals("default")) {
                    holder.user_image.setImageResource(R.drawable.female_avatar);
                } else {
                    Picasso.get().load(imageUrl).placeholder(R.drawable.female_avatar).into(holder.user_image);
                }
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MemberDetailActivity.class);
                intent.putExtra("user_id", userData.getUser_id());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUserData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView user_name, user_mobile, user_account_no, due_amount, due_amount_txt;
        private CircleImageView user_image;

        private ViewHolder(View view) {
            super(view);

            due_amount = view.findViewById(R.id.due_amount);
            due_amount_txt = view.findViewById(R.id.due_amount_txt);
            user_name = view.findViewById(R.id.user_name);
            user_mobile = view.findViewById(R.id.user_mobile);
            user_image = view.findViewById(R.id.user_image);
            user_account_no = view.findViewById(R.id.user_account_no);


            due_amount.setVisibility(View.GONE);
            due_amount_txt.setVisibility(View.GONE);
        }
    }
}
