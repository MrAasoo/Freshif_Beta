package com.aasoo.freshifybeta.admin_ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.notification.APIService;
import com.aasoo.freshifybeta.notification.Client;
import com.aasoo.freshifybeta.notification.Data;
import com.aasoo.freshifybeta.notification.MyResponse;
import com.aasoo.freshifybeta.notification.Sender;
import com.aasoo.freshifybeta.notification.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayBillActivity extends AppCompatActivity {

    TextView txt_due_amount, txt_pay_amount, txt_balance, txt_date;
    EditText detail;
    Button btn_pay, btn_cancel;
    Context context = this;


    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_bill);
        getSupportActionBar().setTitle("Confirm pay bill");


        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        txt_due_amount = findViewById(R.id.txt_due_amount);
        txt_pay_amount = findViewById(R.id.txt_pay_amount);
        txt_balance = findViewById(R.id.txt_balance_amount);
        txt_date = findViewById(R.id.txt_date);
        detail = findViewById(R.id.txt_detail);
        btn_pay = findViewById(R.id.btn_pay);
        btn_cancel = findViewById(R.id.btn_cancel);


        Intent intent = getIntent();
        String due_amount = intent.getStringExtra("due_amount");
        final String user_id = intent.getStringExtra("user_id");
        final String pay_amount = intent.getStringExtra("pay_amount");


        int i = Integer.parseInt(due_amount) - Integer.parseInt(pay_amount);
        final String balance = String.valueOf(i);
        String date = currentDate() + "  " + currentTime();
        txt_date.setText(date);
        txt_due_amount.setText("₹ " + due_amount);
        txt_pay_amount.setText("₹ " + pay_amount);
        txt_balance.setText("₹ " + balance);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelPayBills();
            }
        });

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (detail.getText().toString().trim().equals("")) {
                    detail.setError("Enter detail");
                    detail.requestFocus();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Confirm paying bills");
                    alert.setMessage("Note : Once you press confirm, you will not able to make changes.");
                    alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateInDashboard(user_id, pay_amount, balance);
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
            }
        });

    }


    private void updateInDashboard(final String user_id, final String pay_amount, final String balance) {

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_info").child(user_id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String sr_no = String.valueOf(dataSnapshot.child("my_dashboard").getChildrenCount() + 1);
                String notification = reference.child("my_notification").push().getKey();

                String path = "my_dashboard/" + sr_no + "/";
                String notPath = "my_notification/" + notification + "/";

                //***********Updating user dashboard*************
                HashMap<String, Object> map = new HashMap<>();
                map.put(path + "dash_sr_no", sr_no);
                map.put(path + "dash_date", txt_date.getText());
                map.put(path + "dash_type", "Payment");
                map.put(path + "dash_detail", detail.getText().toString().trim());
                map.put(path + "dash_amount", pay_amount);
                map.put(path + "dash_due_amount", balance);
                map.put(path + "dash_remark", "By admin");

                //************Update user due balance*********
                map.put("user_balance", balance);

                //**************Updating notification**************

                map.put(notPath + "notification_id", notification);
                map.put(notPath + "notification_type", "Payment");
                map.put(notPath + "notification_title", "Payment Successful");
                map.put(notPath + "notification_msg", "₹" + pay_amount + " paid.");
                map.put(notPath + "notification_date", txt_date.getText());
                map.put(notPath + "notification_reference", sr_no);


                reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        sendNotification(user_id,"Payment", "₹" + pay_amount + " paid.");
                        AlertDialog.Builder alert = new AlertDialog.Builder(context);
                        alert.setTitle("Billing status");
                        if (task.isSuccessful()) {
                            alert.setMessage("Billing successful !");
                            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                            alert.create();
                            alert.show();
                            Toast.makeText(context, "Dashboard updated", Toast.LENGTH_SHORT).show();
                        } else {
                            alert.setMessage(task.getException().getMessage());
                            alert.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alert.create();
                            alert.show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void cancelPayBills() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Cancel paying bills")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();

    }


    private String currentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        String currentDate = sdf.format(new Date());
        return currentDate;
    }

    private String currentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh.mm a");
        String currentDate = sdf.format(new Date());
        return currentDate;
    }

    @Override
    public void onBackPressed() {
        cancelPayBills();
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
                                    Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
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
