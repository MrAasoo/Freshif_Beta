package com.aasoo.freshifybeta;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.aasoo.freshifybeta.admin_ui.PayBillActivity;
import com.aasoo.freshifybeta.model.UserData;
import com.aasoo.freshifybeta.ui.DashboardActivity;
import com.aasoo.freshifybeta.ui.MyReferralActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberDetailActivity extends AppCompatActivity {

    static String msg = "";
    private Context context = this;
    private Button btn_action;
    private TextView member_phone, member_email, member_address, user_account_no, pay_bill, due_amount, show_dashboard, show_order, due_amount_txt,show_referral;
    private CircleImageView member_image;
    private RelativeLayout account_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail);
        getSupportActionBar().setTitle("Member detail");
        final String user_id_profile = getIntent().getStringExtra("user_id");


        btn_action = findViewById(R.id.btn_action);
        member_phone = findViewById(R.id.member_phone);
        member_email = findViewById(R.id.member_email);
        member_address = findViewById(R.id.member_address);
        user_account_no = findViewById(R.id.user_account_no);
        member_address = findViewById(R.id.member_address);
        pay_bill = findViewById(R.id.pay_bill);
        member_image = findViewById(R.id.member_image);
        account_info = findViewById(R.id.account_info);
        show_dashboard = findViewById(R.id.show_dashboard);
        show_order = findViewById(R.id.show_order);
        due_amount = findViewById(R.id.due_amount);
        due_amount_txt = findViewById(R.id.due_amount_txt);
        show_referral = findViewById(R.id.show_referral);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_info").child(user_id_profile);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserData userData = dataSnapshot.getValue(UserData.class);
                if (userData != null) {
                    getSupportActionBar().setTitle(userData.getUser_name());
                    final String dueAmount = userData.getUser_balance();
                    if (Integer.parseInt(dueAmount) < 0) {
                        String show = dueAmount;
                        due_amount_txt.setText("Advance balance");
                        due_amount.setText("₹ " + show.substring(1));
                    } else {
                        due_amount_txt.setText("Due Balance");
                        due_amount.setText("₹ " + dueAmount);
                    }
                    user_account_no.setText(userData.getUser_account_no());
                    String mobileNo = userData.getUser_phone();
                    String address = userData.getUser_address();
                    if (userData.getUser_mobile_other() != null) {
                        mobileNo = mobileNo + "\n" + userData.getUser_mobile_other();
                    }
                    if (userData.getUser_address_optional() != null) {
                        address = address + "\nAnd\n" + userData.getUser_address_optional();
                    }
                    member_phone.setText(mobileNo);
                    member_email.setText(userData.getUser_email());
                    member_address.setText(address);
                    String imageUrl = userData.getUser_image();
                    String gen = userData.getUser_gender();
                    if (gen.equals("Male")) {
                        if (imageUrl.equals("default")) {
                            member_image.setImageResource(R.drawable.male_avatar);
                        } else {
                            Picasso.get().load(imageUrl).placeholder(R.drawable.male_avatar).into(member_image);
                        }
                    } else {
                        if (imageUrl.equals("default")) {
                            member_image.setImageResource(R.drawable.female_avatar);
                        } else {
                            Picasso.get().load(imageUrl).placeholder(R.drawable.female_avatar).into(member_image);
                        }
                    }
                    String user_type = userData.getUser_type();
                    if (user_type.equals("member")) {
                        btn_action.setText("Change to user");
                        account_info.setVisibility(View.GONE);
                    } else {
                        btn_action.setText("Change to member");
                        account_info.setVisibility(View.VISIBLE);
                    }


                    pay_bill.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(context);

                            final EditText editText = new EditText(context);
                            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                            alert.setCancelable(false);
                            alert.setTitle("Enter Amount");
                            alert.setView(editText);

                            alert.setPositiveButton("Pay", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String amount = editText.getText().toString();
                                    if (!amount.trim().equals("") && Integer.parseInt(amount) > 0) {
                                        Intent intent = new Intent(context, PayBillActivity.class);
                                        intent.putExtra("due_amount", dueAmount);
                                        intent.putExtra("user_id", user_id_profile);
                                        intent.putExtra("pay_amount", amount);

                                        startActivity(intent);

                                    } else {
                                        Toast.makeText(context, "Enter amount", Toast.LENGTH_SHORT).show();
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

                    show_dashboard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, DashboardActivity.class);
                            intent.putExtra("user_id", user_id_profile);
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        pay_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Change user type");
                alert.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user_info").child(user_id_profile);
                        Map<String, Object> map = new HashMap<>();
                        switch (btn_action.getText().toString()) {
                            case "Change to user":
                                map.put("user_type", "user");
                                msg = "User type changed to user";
                                break;
                            case "Change to member":
                                map.put("user_type", "member");
                                msg = "User type changed to member";
                                break;
                        }
                        databaseReference.updateChildren(map, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                Toast.makeText(MemberDetailActivity.this, "" + msg, Toast.LENGTH_SHORT).show();
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


        show_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ManageOrdersActivity.class);
                intent.putExtra("user_id", user_id_profile);
                startActivity(intent);
            }
        });


        show_referral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, MyReferralActivity.class);
                intent.putExtra("user_id",user_id_profile);
                intent.putExtra("user_type","admin");
                startActivity(intent);
            }
        });
    }
}
