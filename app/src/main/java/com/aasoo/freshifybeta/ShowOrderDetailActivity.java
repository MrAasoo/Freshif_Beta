package com.aasoo.freshifybeta;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.aasoo.freshifybeta.model.Order;
import com.aasoo.freshifybeta.model.Product;
import com.aasoo.freshifybeta.model.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ShowOrderDetailActivity extends AppCompatActivity {

    TextView user_account, order_no, order_model_no, order_quantity, order_item_price, order_date, order_total_price, order_address, order_name, order_mobile, product_name, product_price;
    ImageView product_title_image;

    TextView order_pay, order_advance, order_due, show_in_dashboard;
    LinearLayout pay_layout;

    private Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_order_detail);


        order_no = findViewById(R.id.order_no);
        order_item_price = findViewById(R.id.order_item_price);
        order_quantity = findViewById(R.id.order_quantity);
        order_total_price = findViewById(R.id.order_total_price);
        order_name = findViewById(R.id.order_name);
        order_address = findViewById(R.id.order_address);
        order_mobile = findViewById(R.id.order_mobile);
        order_model_no = findViewById(R.id.product_model_no);
        product_name = findViewById(R.id.product_name);
        product_price = findViewById(R.id.product_price);
        product_title_image = findViewById(R.id.product_title_image);
        order_advance = findViewById(R.id.order_advance);
        order_pay = findViewById(R.id.order_pay);
        order_due = findViewById(R.id.order_due);
        user_account = findViewById(R.id.user_account);
        order_date = findViewById(R.id.order_date);
        pay_layout = findViewById(R.id.pay_layout);
        show_in_dashboard = findViewById(R.id.show_in_dashboard);


        getSupportActionBar().setTitle("Order Info & Status");
        final String order_id = getIntent().getStringExtra("order_id");
        String mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();


        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_info").child(mCurrentUser);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserData userData = dataSnapshot.getValue(UserData.class);
                if (userData != null) {
                    if (userData.getUser_type().equals("admin")) {
                        user_account.setVisibility(View.VISIBLE);
                        pay_layout.setVisibility(View.VISIBLE);
                    } else {
                        user_account.setVisibility(View.GONE);
                        pay_layout.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        final DatabaseReference orderreference = FirebaseDatabase.getInstance().getReference();
        orderreference.child("order_list").child(order_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Order order = dataSnapshot.getValue(Order.class);
                if (order != null) {
                    String date = order.getOrder_date() + "  " + order.getOrder_time();
                    order_date.setText(date);
                    order_no.setText(order.getOrder_no());
                    order_item_price.setText(order.getOrder_item_price());
                    order_quantity.setText(order.getOrder_quantity());
                    order_total_price.setText(order.getOrder_total_price());
                    order_name.setText(order.getOrder_name());
                    order_address.setText(order.getOrder_address());
                    order_mobile.setText(order.getOrder_mobile());
                    order_model_no.setText(order.getOrder_model_no());


                    order_due.setText("₹ "+order.getOrder_due());
                    order_advance.setText("₹ "+order.getOrder_advance());

                    getItemDetail(order.getOrder_model_no());

                    if (order.getOrder_due().equals("0")) {
                        pay_layout.setVisibility(View.GONE);
                    }

                    user_account.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ShowOrderDetailActivity.this, MemberDetailActivity.class);
                            intent.putExtra("user_id", order.getOrder_by_user());
                            startActivity(intent);
                        }
                    });


                    show_in_dashboard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            alert.setCancelable(false);
                            alert.setTitle("Clear due and show in Dashboard");
                            alert.setMessage("Note: This will only clear due in order and you will not able to pay/change in order due amount.\nIt will not pay/clear in total due amount.If you want pay please select 'Pay'.");
                            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    final ProgressDialog progressDialog = new ProgressDialog(context);
                                    progressDialog.setTitle("Updating order info");
                                    progressDialog.setTitle("Please wait.......");
                                    progressDialog.setCanceledOnTouchOutside(false);
                                    progressDialog.create();
                                    progressDialog.show();

                                    String myOrderPath = "user_info/" + order.getOrder_by_user() + "/my_order/" + order_id + "/";
                                    String orderPath = "order_list/" + order_id + "/";

                                    HashMap<String, Object> map = new HashMap<>();


                                    //Update in order
                                    map.put(myOrderPath + "order_due", "0");
                                    map.put(orderPath + "order_due", "0");

                                    orderreference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();
                                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                            alert.setTitle("Bill cleared");
                                            if (task.isSuccessful()) {
                                                alert.setMessage("Order due amount cleared  !");
                                                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                                alert.create();
                                                alert.show();
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
                            });
                            alert.create();
                            alert.show();


                        }
                    });


                    order_pay.setOnClickListener(new View.OnClickListener() {
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
                                        int bal = Integer.parseInt(order.getOrder_due());
                                        int pay = Integer.parseInt(amount);

                                        if (pay > bal) {
                                            Toast.makeText(context, "Can not pay more than due balance", Toast.LENGTH_SHORT).show();
                                        } else {
                                            payForOrder(order.getOrder_no(), order.getOrder_by_user(), pay, bal);
                                        }

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
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void payForOrder(final String order_no, final String user_id, final int pay, final int bal) {


        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Updating order advance/due");
        progressDialog.setTitle("Please wait.......");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.create();
        progressDialog.show();


        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("user_info").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final int sr_no = (int) dataSnapshot.child("my_dashboard").getChildrenCount() + 1;
                final int due = Integer.parseInt(dataSnapshot.child("user_balance").getValue().toString());
                int orderDue = bal - pay;
                int total = due - pay;
                String notification = reference.child("my_notification").push().getKey();


                //Paths for update values


                String pathDash = "user_info/" + user_id + "/my_dashboard/" + sr_no + "/";
                String notPath = "user_info/" + user_id + "/my_notification/" + notification + "/";
                String myOrderPath = "user_info/" + user_id + "/my_order/" + order_no + "/";
                String orderPath = "order_list/" + order_no + "/";


                Order order = dataSnapshot.child("my_order").child(order_no).getValue(Order.class);
                if (order != null) {

                    int ad = Integer.parseInt(order.getOrder_advance());
                    int orderPay = pay + ad;
                    HashMap<String, Object> map = new HashMap<>();


                    //Update in order
                    map.put(myOrderPath + "order_due", String.valueOf(orderDue));
                    map.put(myOrderPath + "order_advance", String.valueOf(orderPay));


                    map.put(orderPath + "order_due", String.valueOf(orderDue));
                    map.put(orderPath + "order_advance", String.valueOf(orderPay));

                    //Update user due balance
                    map.put("user_info/" + user_id + "/user_balance", String.valueOf(total));

                    //Update in notification


                    map.put(notPath + "notification_id", notification);
                    map.put(notPath + "notification_type", "Order Payment");
                    map.put(notPath + "notification_title", "Amount paid");
                    map.put(notPath + "notification_msg", pay + " for " + order.getOrder_quantity() + " " + order.getOrder_model_no() + " completed.");
                    map.put(notPath + "notification_date", currentDate() + " " + currentTime());
                    map.put(notPath + "notification_reference", order_no);


                    //Update in Dashboard

                    String date = currentDate() + "  " + currentTime();
                    String detail = pay + " Advance/Paid for " + order.getOrder_quantity() + ", (model) " + order.getOrder_model_no();

                    map.put(pathDash + "dash_sr_no", String.valueOf(sr_no));
                    map.put(pathDash + "dash_date", date);
                    map.put(pathDash + "dash_type", "Order Payment");
                    map.put(pathDash + "dash_detail", detail);
                    map.put(pathDash + "dash_amount", String.valueOf(pay));
                    map.put(pathDash + "dash_due_amount", String.valueOf(total));
                    map.put(pathDash + "dash_remark", "");


                    reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            alert.setTitle("Billing status");
                            if (task.isSuccessful()) {
                                alert.setMessage("Billing successful !");
                                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getItemDetail(String model_no) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("product_detail").child(model_no);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.getValue(Product.class);
                if (product != null) {
                    product_name.setText(product.getProduct_name());
                    product_price.setText(product.getProduct_price());
                    Picasso.get().load(product.getProduct_title_image_url()).placeholder(R.drawable.placeholder).into(product_title_image);
                }
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

    private String currentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh.mm a");
        String currentDate = sdf.format(new Date());
        return currentDate;
    }
}
