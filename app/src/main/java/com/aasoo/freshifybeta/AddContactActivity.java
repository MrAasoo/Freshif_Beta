package com.aasoo.freshifybeta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddContactActivity extends AppCompatActivity {

    EditText name,mobile,email,others;
    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        name = findViewById(R.id.name);
        mobile = findViewById(R.id.mobile);
        email = findViewById(R.id.email);
        others = findViewById(R.id.others);
        save = findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sName = name.getText().toString().trim();
                String sMobile = mobile.getText().toString().trim();
                String sEmail = email.getText().toString().trim();
                String sOthers = others.getText().toString().trim();
                if(checkData(sName,sMobile,sEmail)){
                    AddContact(sName,sMobile,sEmail,sOthers);
                }
            }
        });
    }

    private void AddContact(String sName, String sMobile, String sEmail, String sOthers) {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Adding contact us");
        dialog.setMessage("please wait ...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("contact_list");
        String id = reference.push().getKey();
        HashMap<String,String> map = new HashMap<>();

        map.put("contact_name",sName);
        map.put("contact_phone",sMobile);
        map.put("contact_email",sEmail);
        map.put("contact_other",sOthers);
        map.put("contact_id",id);

        reference.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.dismiss();
                if(task.isSuccessful()){
                    finish();
                }else {
                    Log.e("AddContactActivity", "onComplete: "+task.getException());
                }
            }
        });
    }

    private boolean checkData(String sName, String sMobile, String sEmail) {
        if(sName.equals("")){
            name.setError("Enter name");
            name.requestFocus();
            return false;
        }else{
            name.setText(sName);
        }
        if(sMobile.equals("") && sEmail.equals("")){
            Toast.makeText(this, "Please Enter mobile or email", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
