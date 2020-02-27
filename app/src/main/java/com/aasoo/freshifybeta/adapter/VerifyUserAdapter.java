package com.aasoo.freshifybeta.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.model.UserData;
import com.aasoo.freshifybeta.notification.APIService;
import com.aasoo.freshifybeta.notification.Client;
import com.aasoo.freshifybeta.notification.Data;
import com.aasoo.freshifybeta.notification.MyResponse;
import com.aasoo.freshifybeta.notification.Sender;
import com.aasoo.freshifybeta.notification.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyUserAdapter extends RecyclerView.Adapter<VerifyUserAdapter.ViewHolder> {
    private Context mContext;
    private List<UserData> mUserData;
    private APIService apiService;

    public VerifyUserAdapter(Context mContext, List<UserData> mUserData) {
        this.mContext = mContext;
        this.mUserData = mUserData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.verify_user_list_view, parent, false);
        return new VerifyUserAdapter.ViewHolder(view);
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

            holder.btn_approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                    alert.setTitle("Approve user account");
                    alert.setMessage("Allow user login");
                    alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {

                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("user_info").child(userData.getUser_id());
                            HashMap<String, Object> userInfo = new HashMap<>();
                            userInfo.put("approval_status", "approve");
                            mDatabase.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
                                        sendNotification(userData.getUser_id(),"Hello "+userData.getUser_name(), "Congratulations! Your account has been approved. From now on, you can surf our app to explore a lot of Designer jewellery with your account.");
                                        dialog.dismiss();
                                        Toast.makeText(mContext, "User login approved", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.create();
                    alert.show();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mUserData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView user_name, user_mobile, user_account_no;
        private CircleImageView user_image;
        private Button btn_approve;

        private ViewHolder(View view) {
            super(view);
            user_name = view.findViewById(R.id.user_name);
            user_mobile = view.findViewById(R.id.user_mobile);
            user_image = view.findViewById(R.id.user_image);
            user_account_no = view.findViewById(R.id.user_account_no);
            btn_approve = view.findViewById(R.id.btn_approve);
        }
    }

    private void sendNotification(final String receiver,final String title, final String msg) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(R.drawable.app_logo, msg, title,receiver);
                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.code() == 200) {
                                if (response.body().success != 1) {
                                    Toast.makeText(mContext, "Notification Failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

