package com.aasoo.freshifybeta;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.aasoo.freshifybeta.model.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private CircleImageView profile_image;
    private CircleImageView edit_image;
    private TextView user_name, user_mobile, user_mobile_other, user_email, user_address, user_address_optional, user_account_no;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String mCurrentUser = getIntent().getStringExtra("user_id");

        profile_image = findViewById(R.id.profile_image);
        edit_image = findViewById(R.id.edit_image);
        user_name = findViewById(R.id.user_name);
        user_mobile = findViewById(R.id.user_mobile);
        user_mobile_other = findViewById(R.id.user_mobile_other);
        user_email = findViewById(R.id.user_email);
        user_address = findViewById(R.id.user_address);
        user_address_optional = findViewById(R.id.user_address_optional);
        user_account_no = findViewById(R.id.user_account_no);

        View.OnClickListener addressClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.user_address:
                        Intent intent = new Intent(context, AddressActivity.class);
                        intent.putExtra("req_type", "address");
                        startActivity(intent);
                        break;
                    case R.id.user_address_optional:
                        Intent address = new Intent(context, AddressActivity.class);
                        address.putExtra("req_type", "optional");
                        startActivity(address);
                        break;
                    case R.id.user_mobile_other:
                        addOptionalMobile(mCurrentUser);
                        break;
                    case R.id.edit_image:
                        Intent editImage = new Intent(context, EditImageActivity.class);
                        Pair<View, String> imageAnimation = Pair.create(findViewById(R.id.profile_image),"PROFILE_IMAGE");
                        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(ProfileActivity.this,imageAnimation);
                        startActivity(editImage, activityOptions.toBundle());
                        break;
                }
            }
        };

        user_address.setOnClickListener(addressClickListener);
        user_address_optional.setOnClickListener(addressClickListener);
        user_mobile_other.setOnClickListener(addressClickListener);
        edit_image.setOnClickListener(addressClickListener);

        getUserInfo(mCurrentUser);
    }


    private void addOptionalMobile(final String mCurrentUser) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        final EditText editText = new EditText(context);
        editText.setText(user_mobile_other.getText());
        editText.setInputType(InputType.TYPE_CLASS_PHONE);
        alert.setCancelable(false);
        alert.setTitle("Change Mobile no.");
        alert.setView(editText);
        alert.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String number = editText.getText().toString();
                if (!number.trim().equals("") && number.length() == 10) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_info").child(mCurrentUser);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("user_mobile_other", number);
                    reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Mobile no. saved", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(context, "Invalid mobile number", Toast.LENGTH_SHORT).show();
                }

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        alert.show();

    }

    private void getUserInfo(String mCurrentUser) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_info").child(mCurrentUser);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserData user = dataSnapshot.getValue(UserData.class);
                if (user != null) {
                    if (user.getUser_gender().equals("Male")) {
                        Picasso.get().load(user.getUser_image()).placeholder(R.drawable.male_avatar).into(profile_image);
                    } else {
                        Picasso.get().load(user.getUser_image()).placeholder(R.drawable.female_avatar).into(profile_image);
                    }
                    user_name.setText(user.getUser_name());
                    user_mobile.setText(user.getUser_phone());
                    user_email.setText(user.getUser_email());
                    user_address.setText(user.getUser_address());
                    user_account_no.setText(user.getUser_account_no());

                    if (user.getUser_address_optional() != null) {
                        user_address_optional.setText(user.getUser_address_optional());
                    }
                    if (user.getUser_mobile_other() != null) {
                        user_mobile_other.setText(user.getUser_mobile_other());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

