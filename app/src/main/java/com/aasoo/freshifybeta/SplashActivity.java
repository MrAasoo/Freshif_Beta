package com.aasoo.freshifybeta;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.aasoo.freshifybeta.model.UserData;
import com.aasoo.freshifybeta.notification.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class SplashActivity extends AppCompatActivity {

    private static final String FRESHIFY_TERMS_OF_SERVICES = "terms_key";
    Context context = this;
    private boolean flag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences sharedPreferences = context.getSharedPreferences(FRESHIFY_TERMS_OF_SERVICES, MODE_PRIVATE);
        //Checking network status
        if (!isConnected(context)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("No Internet Connection");
            builder.setMessage("Please check Internet Connection, Then try again");
            builder.setCancelable(false);
            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(context, SplashActivity.class));
                    finish();
                }
            }).setNegativeButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                    flag = true;
                }
            });
            builder.show();

        } else if (!sharedPreferences.getBoolean(FRESHIFY_TERMS_OF_SERVICES, false)) {
            TermsAndCondition condition = new TermsAndCondition();
            Dialog dialog = condition.onCreateDialog(savedInstanceState);
            dialog.show();
            ((TextView) dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            loginOrRegister();
        }

    }


    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            return (mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting());
        } else {
            return false;
        }

    }

    private void sendToWelcome() {
        Intent intent = new Intent(context, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void sendToSplash() {
        Intent intent = new Intent(context, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void sendToHome() {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void sendToAdmin() {
        Intent intent = new Intent(context, AdminActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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

    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (flag) {
            sendToSplash();
        }
    }

    private void loginOrRegister() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final String uid = user.getUid();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("user_info");
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(uid).exists()) {
                        updateToken(FirebaseInstanceId.getInstance().getToken());
                        UserData mUser = dataSnapshot.child(uid).getValue(UserData.class);
                        if (mUser != null) {
                            if (mUser.getApproval_status().equals("approve")) {
                                String user_type = mUser.getUser_type();
                                switch (user_type) {
                                    case "user":
                                        sendToHome();
                                        break;
                                    case "admin":
                                    case "member":
                                        sendToAdmin();
                                        break;
                                }
                            } else {
                                sendToApprove();
                            }
                        }
                    } else {
                        sendToCompleteRegister();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            sendToWelcome();
        }

    }

    public class TermsAndCondition extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Accept Terms");
            alert.setIcon(R.drawable.app_logo);
            alert.setMessage(Html.fromHtml("By creating an account, you agree to the Freshify <a href=\"http://www.google.com\">Terms of Service</a> and <a href=\"http://www.google.com\">Privacy Policy</a>"));
            alert.setCancelable(false);
            alert.setPositiveButton("I AGREE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences(FRESHIFY_TERMS_OF_SERVICES, MODE_PRIVATE);
                    sharedPreferences.edit().putBoolean(FRESHIFY_TERMS_OF_SERVICES, true).apply();
                    dialog.dismiss();
                    sendToSplash();
                }
            });
            alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            return alert.create();
        }
    }

}
