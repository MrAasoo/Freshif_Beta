package com.aasoo.freshifybeta;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.aasoo.freshifybeta.model.UserData;
import com.aasoo.freshifybeta.notification.Token;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminActivity extends AppCompatActivity {

    private Context context = this;
    private String TAG = "AdminActivity";

    private Toolbar mToolbar;
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        updateToken(FirebaseInstanceId.getInstance().getToken());


        mToolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(mToolbar);

        mDrawerLayout = findViewById(R.id.admin_drawer_layout);
        mNavigationView = findViewById(R.id.admin_navigation_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.admin_home,
                R.id.admin_products,
                R.id.admin_order,
                R.id.menu_myaccount,
                R.id.admin_daybook,
                R.id.menu_logout,
                R.id.menu_about
        ).setDrawerLayout(mDrawerLayout)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(mNavigationView, navController);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        View nav_view = mNavigationView.getHeaderView(0);
        final CircleImageView user_image = nav_view.findViewById(R.id.user_image);
        final TextView user_name = nav_view.findViewById(R.id.user_name);
        final TextView edit_profile = nav_view.findViewById(R.id.edit_profile);


        final String mUser = mCurrentUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_info").child(mUser);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserData user = dataSnapshot.getValue(UserData.class);
                if (user != null) {
                    if (user.getUser_gender().equals("Male")) {
                        Picasso.get().load(user.getUser_image()).placeholder(R.drawable.male_avatar).into(user_image);
                    } else {
                        Picasso.get().load(user.getUser_image()).placeholder(R.drawable.female_avatar).into(user_image);
                    }
                    user_name.setText(user.getUser_name());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(context, ProfileActivity.class);
                profileIntent.putExtra("user_id",mUser);
                startActivity(profileIntent);
            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void adminLogout(MenuItem item) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            AuthUI.getInstance().signOut(context).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(context, WelcomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Log.e(TAG, "onComplete: ", task.getException());
                    }
                }
            });
        }
    }


    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(mCurrentUser.getUid()).setValue(token1);
    }
}
