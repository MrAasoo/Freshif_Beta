package com.aasoo.freshifybeta.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.adapter.ReferralAdapter;
import com.aasoo.freshifybeta.model.Referral;
import com.aasoo.freshifybeta.model.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyReferralActivity extends AppCompatActivity {


    CardView referral_reward, referral_parent;
    AlertDialog.Builder inAlert;
    private Context context = this;
    private TextView give_referral, referral_reward_txt;
    private RecyclerView recyclerView;
    private List<Referral> referrals;
    private ReferralAdapter adapter;


    CircleImageView user_image;
    TextView user_account_no, user_name, user_mobile, add_on_date;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_referral);
        getSupportActionBar().setTitle("Referral's");
        final String user_id = getIntent().getStringExtra("user_id");
        String user_type = getIntent().getStringExtra("user_type");

        final FloatingActionButton add_referral = findViewById(R.id.add_referral);
        recyclerView = findViewById(R.id.recycler_view);
        give_referral = findViewById(R.id.give_referral);
        referral_reward = findViewById(R.id.referral_reward);
        referral_parent = findViewById(R.id.referral_parent);
        referral_reward_txt = findViewById(R.id.referral_reward_txt);



        user_image = findViewById(R.id.user_image);
        user_account_no = findViewById(R.id.user_account_no);
        user_name = findViewById(R.id.user_name);
        user_mobile = findViewById(R.id.user_mobile);
        add_on_date = findViewById(R.id.add_on_date);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        referrals = new ArrayList<>();
        adapter = new ReferralAdapter(context, referrals, user_type);
        recyclerView.setAdapter(adapter);


        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_info");

        getReferralList(user_id);

        reference.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserData data = dataSnapshot.getValue(UserData.class);
                if (data != null) {
                    if (data.getUser_reward().equals("0")) {
                        referral_reward.setVisibility(View.GONE);
                    } else {
                        referral_reward.setVisibility(View.VISIBLE);
                        referral_reward_txt.setText("₹ "+data.getUser_reward());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (user_type.equals("admin")) {
            add_referral.setVisibility(View.VISIBLE);
            give_referral.setVisibility(View.VISIBLE);
        } else {
            add_referral.setVisibility(View.GONE);
            give_referral.setVisibility(View.GONE);
        }
        add_referral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);

                View dialogView = LayoutInflater.from(context).inflate(R.layout.referral_dialog_layout, null);
                final TextView txt_id = dialogView.findViewById(R.id.txt_id);
                final TextView txt_reward = dialogView.findViewById(R.id.txt_reward);
                txt_reward.setText("500");
                alert.setCancelable(false);
                alert.setView(dialogView);

                alert.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        final String id = txt_id.getText().toString().trim();
                        final String rewardTxt = txt_reward.getText().toString().trim();

                        if (!id.equals("") && !rewardTxt.equals("")) {
                            final int reward = Integer.parseInt(rewardTxt);
                            if (reward <= 500) {
                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        boolean parent = false;
                                        String parentPath = "";
                                        String parentid = "";
                                        int totalReward = 0;
                                        int parentReward = reward / 2;


                                        UserData data = dataSnapshot.child(user_id).getValue(UserData.class);

                                        if (data != null) {
                                            totalReward = reward + Integer.parseInt(data.getUser_reward());
                                            if (!data.getParent_referral().equals("none")) {
                                                parent = true;
                                                parentPath = data.getParent_referral();
                                            }
                                        }
                                        inAlert = new AlertDialog.Builder(context);
                                        inAlert.setTitle("Adding referral result");
                                        inAlert.setCancelable(false);


                                        String info_user = null;
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            UserData user = snapshot.getValue(UserData.class);
                                            if (user != null) {
                                                if (user.getUser_account_no().equals(id)) {
                                                    info_user = user.getUser_id();
                                                    break;
                                                }

                                                if (parent) {
                                                    if (user.getUser_account_no().equals(parentPath)) {
                                                        parentid = user.getUser_id();
                                                        parentReward = parentReward + Integer.parseInt(user.getUser_reward());
                                                    }
                                                }
                                            }
                                        }

                                        if (info_user == null) {

                                            inAlert.setMessage(id + " is not found or may not exist.");

                                        } else if (info_user.equals(user_id)) {
                                            inAlert.setMessage("You can not add current user as its referral.");
                                        } else if (dataSnapshot.child(info_user).child("parent_referral").getValue().equals("none")) {
                                            String myref = user_id + "/my_referral/" + info_user + "/";
                                            String userref = info_user + "/my_referral/" + user_id + "/";

                                            HashMap<String, Object> map = new HashMap<>();

                                            map.put(myref + "referral_id", info_user);
                                            map.put(myref + "referral_date", currentDate());
                                            map.put(myref + "referral_type", "referral");
                                            map.put(myref + "referral_user_id", id);

                                            map.put(userref + "referral_id", user_id);
                                            map.put(userref + "referral_date", currentDate());
                                            map.put(userref + "referral_type", "parent_referral");
                                            map.put(userref + "referral_user_id", dataSnapshot.child(user_id).child("user_account_no").getValue());


                                            map.put(info_user + "/parent_referral", dataSnapshot.child(user_id).child("user_account_no").getValue());
                                            map.put(user_id + "/user_reward", String.valueOf(totalReward));
                                            if (parent) {
                                                map.put(parentid + "/user_reward", String.valueOf(parentReward));
                                            }

                                            reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(context, "User " + id + " added successful", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });


                                        } else {
                                            inAlert.setMessage("User " + id + " is already added as referral.");
                                        }

                                        inAlert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        inAlert.create();
                                        inAlert.show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                Toast.makeText(context, "Maximum reward value is 500, Try again.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "Invalid input, Try again.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.create();
                alert.show();

            }
        });


    }

    private void getReferralList(final String user_id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_info").child(user_id).child("my_referral");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                referrals.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Referral ref = snapshot.getValue(Referral.class);
                    if (ref.getReferral_type().equals("referral")) {
                        referrals.add(ref);
                    } else if (ref.getReferral_type().equals("parent_referral")) {
                        referral_parent.setVisibility(View.VISIBLE);
                        String parent_user_id = ref.getReferral_id();
                        user_account_no.setText(ref.getReferral_user_id());
                        add_on_date.setText(ref.getReferral_date());
                        DatabaseReference parentReference = FirebaseDatabase.getInstance().getReference("user_info").child(parent_user_id);
                        parentReference.addListenerForSingleValueEvent(new ValueEventListener() {
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
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private String currentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        String currentDate = sdf.format(new Date());
        return currentDate;
    }
}
