package com.aasoo.freshifybeta;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddressActivity extends AppCompatActivity {

    Button btn_save, btn_cancel;
    EditText address_house, address_city, address_state, address_pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        final String req_type = getIntent().getStringExtra("req_type");
        final String mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        btn_save = findViewById(R.id.btn_save);
        btn_cancel = findViewById(R.id.btn_cancel);
        address_pin = findViewById(R.id.address_pin);
        address_state = findViewById(R.id.address_state);
        address_house = findViewById(R.id.address_house);
        address_city = findViewById(R.id.address_city);


        View.OnClickListener btnlistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                switch (v.getId()) {
                    case R.id.btn_save:
                        if (values()) {
                            String address = address_house.getText().toString().toUpperCase() +
                                    "\n" + address_city.getText().toString().toUpperCase() +
                                    "\n" + address_state.getText().toString().toUpperCase() +
                                    "\n" + address_pin.getText();
                            switch (req_type) {
                                case "order":
                                    updateOrderAddress(address);
                                    break;
                                case "address":
                                    updateAddress(address, mCurrentUser);
                                    break;
                                case "optional":
                                    updateOptionalAddress(address, mCurrentUser);
                                    break;
                            }
                        }
                        break;

                    case R.id.btn_cancel:
                        finish();
                        break;
                }

            }
        };

        btn_save.setOnClickListener(btnlistener);
        btn_cancel.setOnClickListener(btnlistener);
    }

    private void updateOptionalAddress(String address, String user) {
        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setTitle("Updating address");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_info").child(user);
        HashMap<String, Object> map = new HashMap<>();
        map.put("user_address_optional", address);
        reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mDialog.dismiss();
                    Toast.makeText(AddressActivity.this, "Address saved", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void updateAddress(String address, String user) {
        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setTitle("Updting address");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_info").child(user);
        HashMap<String, Object> map = new HashMap<>();
        map.put("user_address", address);
        reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mDialog.dismiss();
                    Toast.makeText(AddressActivity.this, "Address saved", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void updateOrderAddress(String address) {
        Intent intent = new Intent();
        intent.putExtra("address", address);
        setResult(RESULT_OK, intent);
        finish();
    }

    private boolean values() {
        if (address_house.getText().toString().trim().equals("")) {
            address_house.setError("Enter House / Town / Village");
            address_house.requestFocus();
            return false;
        }
        if (address_city.getText().toString().trim().equals("")) {
            address_city.setError("Enter City / District");
            address_city.requestFocus();
            return false;
        }
        if (address_state.getText().toString().trim().equals("")) {
            address_state.setError("Enter State");
            address_state.requestFocus();
            return false;
        }
        if (address_pin.getText().toString().trim().equals("") || address_pin.getText().toString().trim().length() < 6 || address_pin.getText().toString().trim().length() > 6) {
            address_pin.setError("Enter valid pin");
            address_pin.requestFocus();
            address_pin.setText("");
            return false;
        } else
            return true;
    }
}
