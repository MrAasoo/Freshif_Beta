package com.aasoo.freshifybeta;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CompleteRegisterActivity extends AppCompatActivity {
    private static final String TAG = "CompleteRegisterActivit";
    public Context context = this;
    String user_email = "", user_name = "", user_phone = "", user_gen = "default", user_id;
    EditText txt_name, txt_email, txt_phone;
    Button btn_submit;
    RadioButton rb_male, rb_female;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_register);

        txt_name = findViewById(R.id.user_name);
        txt_email = findViewById(R.id.user_email);
        txt_phone = findViewById(R.id.user_mobile);
        rb_male = findViewById(R.id.rb_male);
        rb_female = findViewById(R.id.rb_female);
        btn_submit = findViewById(R.id.btn_submit);
        mDialog = new ProgressDialog(context);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user_id = user.getUid();
        try {
            user_email = user.getEmail();
            user_name = user.getDisplayName();
            user_phone = user.getPhoneNumber();
            Log.e(TAG, "onCreate: "+user_phone );

            if (!user_email.equals("")) {
                txt_email.setText(user_email);
            }
            if (!user_name.equals("")) {
                txt_name.setText(user_name);
            }
            if (!user_phone.equals("")) {
                String phone = user_phone.substring(3);
                txt_phone.setText(phone);
            }
        } catch (Exception ex) {
            Log.e(TAG, "onCreate: Exception is " + ex.getMessage());
        }

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.setTitle("Registring");
                mDialog.setMessage("Please wait ...");
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();
                if (checkUserData()) {
                    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("user_info");
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int total = (int) dataSnapshot.getChildrenCount();
                            total = total + 1000;

                            HashMap<String, String> userInfo = new HashMap<>();
                            userInfo.put("user_id", user_id);
                            userInfo.put("user_account_no", String.valueOf(total));
                            userInfo.put("user_name", txt_name.getText().toString().trim().toUpperCase());
                            userInfo.put("user_phone", txt_phone.getText().toString().trim());
                            userInfo.put("user_email", txt_email.getText().toString().trim());
                            userInfo.put("user_type", "user");
                            userInfo.put("user_gender", user_gen);
                            userInfo.put("user_image", "default");
                            userInfo.put("user_address", "");
                            userInfo.put("user_balance", "0");
                            userInfo.put("user_reward", "0");
                            userInfo.put("parent_referral", "none");
                            userInfo.put("approval_status", "pending");

                            mDatabase.child(user_id).setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mDialog.dismiss();
                                        Intent intent = new Intent(context, PendingApproveActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } else {
                                        Log.e(TAG, "onComplete: " + task.getException());
                                        Toast.makeText(context, "Failed to submit data. Please try again latter....", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    mDialog.dismiss();
                }
            }
        });


    }


    private boolean checkUserData() {
        if (txt_name.getText().toString().trim().equals("")) {
            txt_name.setError("Please enter name.");
            txt_name.requestFocus();
            return false;
        }
        if (txt_phone.getText().toString().trim().equals("") ||
                txt_phone.getText().toString().length() > 10 ||
                txt_phone.getText().toString().length() < 10) {
            txt_phone.setError("Enter valid phone number,Do not use country-code before.");
            txt_phone.requestFocus();
            return false;
        }
        if (user_gen.equals("default")) {
            rb_female.setError("");
            rb_female.requestFocus();
            Toast.makeText(context, "Please select Male or Female", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void onRadioButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.rb_male:
                user_gen = rb_male.getText().toString();
                break;
            case R.id.rb_female:
                user_gen = rb_female.getText().toString();
                break;
            default:
        }
    }
}