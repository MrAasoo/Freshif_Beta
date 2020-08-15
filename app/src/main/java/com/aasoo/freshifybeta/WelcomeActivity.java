package com.aasoo.freshifybeta;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.aasoo.freshifybeta.adapter.Pager_adapter;
import com.aasoo.freshifybeta.model.UserData;
import com.aasoo.freshifybeta.notification.Token;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends AppCompatActivity {

    TextView txt_login,txt_signup;
    private ViewPager viewPager;
    private int[] layout = {R.layout.slide_1,R.layout.slide_2,R.layout.slide_3,R.layout.slide_4};

    Context context = this;
    List<AuthUI.IdpConfig> providers;
    private static final String TAG ="WelcomeActivity";
    int AUTHUI_REQUEST_CODE = 10001;
    private Pager_adapter pager_adapter;


    private LinearLayout dot_layout;
    private ImageView[] dots;

    ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        progressBar = findViewById(R.id.progress_bar);

        txt_login = findViewById(R.id.txt_login);
        txt_signup = findViewById(R.id.txt_signup);

        View.OnClickListener txt_click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()){
                    case R.id.txt_login:
                    case R.id.txt_signup:
                        handleloginregister();
                        break;
                }

            }
        };

        txt_signup.setOnClickListener(txt_click);
        txt_login.setOnClickListener(txt_click);

        viewPager = findViewById(R.id.slide_image);
        pager_adapter = new Pager_adapter(layout,context);
        viewPager.setAdapter(pager_adapter);

        dot_layout = findViewById(R.id.dot_layout);
        createDots(0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                createDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(),2000,4000);

    }

    private void createDots(int current_position){
        if(dot_layout!=null){
            dot_layout.removeAllViews();
        }

        dots = new ImageView[layout.length];

        for(int i = 0; i<layout.length; i++){
            dots[i] = new ImageView(context);
            if(i==current_position){
                dots[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.selected_dot));
            }else{
                dots[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.default_dot));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10,0,10,0);
            dot_layout.addView(dots[i],params);
        }
    }


    private class MyTimerTask extends TimerTask{

        @Override
        public void run() {
          WelcomeActivity.this.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                  if(viewPager.getCurrentItem() <layout.length -1){
                      viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                  }else{
                      viewPager.setCurrentItem(0);
                  }
              }
          });
        }
    }



    public void handleloginregister(){

        // init providers for Email, Phone and Google
        providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build(),    //Phone Builder
                new AuthUI.IdpConfig.EmailBuilder().build(),    //Email Builder
                new AuthUI.IdpConfig.GoogleBuilder().build()    //Google Builder
        );

        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTosAndPrivacyPolicyUrls("http://example.com","http://example.com")
                .setLogo(R.drawable.app_logo)
                .build();
        startActivityForResult(intent, AUTHUI_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AUTHUI_REQUEST_CODE){
            progressBar.setVisibility(View.VISIBLE);
            if(resultCode == RESULT_OK){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final String uid = user.getUid();
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("user_info");
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(uid).exists()){
                            UserData mUser = dataSnapshot.child(uid).getValue(UserData.class);
                            if(mUser != null && mUser.getApproval_status().equals("approve")){
                                String user_type = mUser.getUser_type();
                                switch (user_type){
                                    case "user":
                                        Intent intent = new Intent(context,HomeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        break;
                                    case "admin":
                                    case "member":
                                        Intent Adminintent = new Intent(context,AdminActivity.class);
                                        Adminintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(Adminintent);
                                        break;
                                }
                            }else{
                               sendToApprove();
                            }
                        }else {
                            updateToken(FirebaseInstanceId.getInstance().getToken());
                            sendToCompleteRegister();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }else{
                progressBar.setVisibility(View.GONE);
                //If login or register failed
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if(response == null){
                    Log.d(TAG, "onActivityResult: The user canceled request");
                }else {
                    Log.e(TAG, "onActivityResult: "+response.getError());
                }
            }
        }else {
            progressBar.setVisibility(View.GONE);
            Log.e(TAG, "onActivityResult: Unknown error");
        }
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token1);
    }
    private void sendToApprove() {
        Intent intent = new Intent(context, PendingApproveActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void sendToCompleteRegister() {
        Intent intent = new Intent(context, CompleteRegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
